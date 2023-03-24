package org.vaadin.miki.superfields.lazyload;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.internal.JsonSerializer;
import com.vaadin.flow.shared.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.miki.markers.WithIdMixin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A component that wraps Intersection Observer on the client-side to notify server-side about visibility changes.
 * Note: Intersection Observer API is experimental and not all browsers implement it.
 *
 * @author miki
 * @since 2020-04-28
 */
@JsModule("./component-observer.js")
@Tag("component-observer")
public class ComponentObserver extends Component implements WithIdMixin<ComponentObserver> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentObserver.class);

    private final double[] ranges;

    private final Map<String, Component> observedComponents = new HashMap<>();
    private final Map<Component, Registration> observedDetachedListeners = new HashMap<>();

    private final Element rootElement;
    private final String rootMargin;

    private int sequence = 0;

    /**
     * This component may be removed from DOM, client needs to be reinitialised each time.
     */
    private boolean clientInitialised = false;

    /**
     * Creates the observer using entire document as viewport.
     * @param visibilityRanges Ranges at which to trigger visibility change events. If not specified, {@code [0.0, 1.0]} will be used.
     */
    public ComponentObserver(double... visibilityRanges) {
        this(null, "0px", visibilityRanges);
    }

    /**
     * Creates the observer using provided document as viewport root and provided root margins.
     * @param viewportRoot Component to use as viewport root.
     * @param rootMargin Root margin (CSS-like expression).
     * @param visibilityRanges Ranges at which to trigger visibility change events. If not specified, {@code [0.0, 1.0]} will be used.
     */
    public ComponentObserver(Component viewportRoot, String rootMargin, double... visibilityRanges) {
        this.ranges = (visibilityRanges == null || visibilityRanges.length == 0) ?
                new double[]{0.0d, 1.0d} :
                visibilityRanges;
        this.rootElement = viewportRoot == null ? null : viewportRoot.getElement();
        this.rootMargin = rootMargin;
        this.initClient();
    }

    protected void initClient() {
        if(!this.clientInitialised) {
            this.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this, context ->
                    this.getElement().callJsFunction(
                            "initObserver",
                            rootElement, rootMargin, JsonSerializer.toJson(ranges)
                    )));
            this.observedComponents.forEach(this::observe);
            this.clientInitialised = true;
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // indicate the client code needs to be redone
        this.clientInitialised = false;
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        // make sure the client is properly set up
        this.initClient();
        super.onAttach(attachEvent);
    }

    @ClientCallable
    private void componentStatusChanged(String key, double range) {
        LOGGER.debug("component with key {} changed visibility range to {}", key, range);
        this.fireComponentObservationEvent(new ComponentObservationEvent(this, this.observedComponents.get(key), range));
    }

    /**
     * Broadcasts the event using {@link #getEventBus()}.
     * @param event Event with relevant information about what happened.
     */
    protected void fireComponentObservationEvent(ComponentObservationEvent event) {
        this.getEventBus().fireEvent(event);
    }

    /**
     * Observes the given component with given index. Will override the component currently associated with given index, if the was any.
     * @param indexString Index string to register the component with.
     * @param component Component to observe.
     */
    protected void observe(String indexString, Component component) {
        this.observedComponents.put(indexString, component);
        this.observedDetachedListeners.put(component, component.addDetachListener(event -> this.unobserve(event.getSource())));

        this.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this, executionContext ->
                this.getElement().callJsFunction("observe", component.getElement(), indexString)
        ));
    }

    /**
     * Starts observation of given components.
     * If a given component is already being observed, it will not be observed an additional time.
     * Given component will stop being observed when it gets detached or when {@link #unobserve(Component...)} method is called.
     * @param components Components to observe.
     */
    public void observe(Component... components) {
        for (Component component : components)
            if(!this.observedComponents.containsValue(component)) {
                final String indexString = "component-observer-"+(this.sequence++);
                this.observe(indexString, component);
            }
    }

    /**
     * Stops observation of given components.
     * If a given component has not been registered with {@link #observe(Component...)}, nothing will happen.
     * @param components Components to stop observing.
     */
    public void unobserve(Component... components) {
        for(Component component: components) {
            Optional<String> perhapsKey = this.observedComponents.entrySet().stream().
                    filter(entry -> entry.getValue().equals(component)).findFirst().map(Map.Entry::getKey);
            perhapsKey.ifPresent(key -> {
                final Component removed = this.observedComponents.remove(key);
                this.observedDetachedListeners.get(removed).remove();
                this.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this, executionContext ->
                        this.getElement().callJsFunction("unobserve", removed.getElement())
                ));
            });
        }
    }

    /**
     * Adds a listener that will be informed about changes in observed components.
     * @param listener Listener to add.
     * @return Registration that can be used to stop listening.
     */
    public Registration addComponentObservationListener(ComponentObservationListener listener) {
        return this.getEventBus().addListener(ComponentObservationEvent.class, listener);
    }

    /**
     * Checks whether or not given {@link Component} is being observed by this object.
     * @param component A {@link Component}.
     * @return {@code true} when the {@code component} has been added through {@link #observe(Component...)} and not removed with {@link #unobserve(Component...)}.
     */
    public boolean isObserving(Component component) {
        return this.observedComponents.containsValue(component);
    }

    /**
     * Returns an array with visibility ranges registered for this component.
     * Modifying the resulting array has no effect on this object.
     * @return A non-empty array with visibility ranges.
     */
    public double[] getVisibilityRanges() {
        double[] result = new double[this.ranges.length];
        System.arraycopy(this.ranges, 0, result, 0, this.ranges.length);
        return result;
    }

}
