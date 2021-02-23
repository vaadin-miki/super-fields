package org.vaadin.miki.shared.text;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.shared.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.miki.events.text.TextSelectionEvent;
import org.vaadin.miki.events.text.TextSelectionListener;
import org.vaadin.miki.events.text.TextSelectionNotifier;
import org.vaadin.miki.markers.CanReceiveSelectionEventsFromClient;
import org.vaadin.miki.markers.CanSelectText;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A class that handles common behaviour related to text selection. This is mostly for internal use.
 *
 * There are a lot of steps that need to be taken care of to integrate this with any component. Please check project's wiki.
 * The methods {@code onAttach onDetach} must be overwritten in the delegating class and delegated here.
 * The source component must implement {@link CanSelectText} and {@link CanReceiveSelectionEventsFromClient} and delegate them
 * to this object.
 * The client-side component must mix in {@code text-selection-mixin.js} or otherwise react to needed JS method calls.
 * Finally, the delegating class must implement methods: {@code @ClientCallable void selectionChanged(int, int, String)} and
 * {@code @ClientCallable void reinitialiseEventListening()}. The first method should call this object's
 * {@link #fireTextSelectionEvent(boolean, int, int, String)} and the second method should just be delegated to this object.
 *
 * @author miki
 * @since 2020-06-01
 */
public class TextSelectionDelegate<C extends Component & CanSelectText & CanReceiveSelectionEventsFromClient>
       implements Serializable, CanSelectText, CanReceiveSelectionEventsFromClient, TextSelectionNotifier<C> {

    /**
     * Defines the name of the HTML attribute that contains the selected text.
     */
    public static final String SELECTED_TEXT_ATTRIBUTE_NAME = "data-selected-text";

    private static final int MAX_REINITIALISING_ATTEMPTS = 3;

    private static final Logger LOGGER = LoggerFactory.getLogger(TextSelectionDelegate.class);

    private final C source;
    
    private final Element sourceElement;

    private final ComponentEventBus eventBus;

    private final SerializableSupplier<String> stringValueSupplier;
    
    private boolean receivingSelectionEventsFromClient = false;

    private int reinitialisationAttemptsLeft = MAX_REINITIALISING_ATTEMPTS;

    /**
     * Creates the delegate for a given component.
     * @param source Source of all events, data, etc.
     * @param eventBus Event bus to use for firing events. Typically, {@code source.getEventBus()}.
     * @param stringValueSupplier Method to obtain current value of the component as a {@link String}.
     */
    public TextSelectionDelegate(C source, ComponentEventBus eventBus, SerializableSupplier<String> stringValueSupplier) {
        this.source = source;
        this.sourceElement = source.getElement();
        this.eventBus = eventBus;
        this.stringValueSupplier = stringValueSupplier;
        if(source instanceof HasValue<?, ?>)
            ((HasValue<?, ?>) source).addValueChangeListener(event -> this.clearSelectionOnValueChange());
    }

    /**
     * Sends information to the client side about whether or not it should forward text selection change events.
     * @param value When {@code true}, client-side will notify server about changes in text selection.
     */
    public void informClientAboutSendingEvents(boolean value) {
        this.sourceElement.getNode().runWhenAttached(ui -> ui.beforeClientResponse(this.source, context ->
                this.sourceElement.callJsFunction(
                        "setCallingServer",
                        value
                )
        ));
    }

    /**
     * Fires text selection event.
     * @param fromClient Whether or not the event originates from the client.
     * @param start Selection start.
     * @param end Selection end.
     * @param text Selection text.
     */
    public void fireTextSelectionEvent(boolean fromClient, int start, int end, String text) {
        TextSelectionEvent<C> event = new TextSelectionEvent<>(this.source, fromClient, start, end, text);
        this.eventBus.fireEvent(event);
    }

    @Override
    public void selectAll() {
        this.sourceElement.getNode().runWhenAttached(ui -> ui.beforeClientResponse(this.source, context ->
                this.sourceElement.callJsFunction("selectAll", this.source.getElement())
        ));
        // send event if the client is not doing it
        if(!this.isReceivingSelectionEventsFromClient()) {
            final String value = this.stringValueSupplier.get();
            this.sourceElement.setAttribute(SELECTED_TEXT_ATTRIBUTE_NAME, value);
            this.fireTextSelectionEvent(false, 0, value.length(), value);
        }
    }

    @Override
    public void selectNone() {
        this.sourceElement.getNode().runWhenAttached(ui -> ui.beforeClientResponse(this.source, context ->
                this.sourceElement.callJsFunction("selectNone", this.source.getElement())
        ));
        // send event if the client is not doing it
        if(!this.isReceivingSelectionEventsFromClient()) {
            this.sourceElement.setAttribute(SELECTED_TEXT_ATTRIBUTE_NAME, "");
            this.fireTextSelectionEvent(false, -1, -1, "");
        }
    }

    @Override
    public void select(int from, int to) {
        if(from <= to)
            this.sourceElement.getNode().runWhenAttached(ui -> ui.beforeClientResponse(this.source, context ->
                    this.sourceElement.callJsFunction("select", this.source.getElement(), from, to)
            ));
        // send event if the client is not doing it
        if(!this.isReceivingSelectionEventsFromClient()) {
            final String value = this.stringValueSupplier.get().substring(from, to);
            this.sourceElement.setAttribute(SELECTED_TEXT_ATTRIBUTE_NAME, value);
            this.fireTextSelectionEvent(false, from, to, value);
        }
    }

    /**
     * Handles selection change on value change if there are no client notifications.
     * Does nothing if the component is receiving client-side notifications.
     */
    public void clearSelectionOnValueChange() {
        // special case here: if there was selection, no client-side events are caught and value is set, event must be fired
        if(!this.isReceivingSelectionEventsFromClient()) {
            final String lastSelected = Optional.ofNullable(this.sourceElement.getAttribute(SELECTED_TEXT_ATTRIBUTE_NAME)).orElse("");
            this.sourceElement.setAttribute(SELECTED_TEXT_ATTRIBUTE_NAME, "");
            if(!Objects.equals(lastSelected, ""))
                this.fireTextSelectionEvent(false, -1, -1, "");
        }
    }

    /**
     * Informs client about sending events and calls the original method.
     * @param event Event.
     * @param originalMethod Method to call. Must not be {@code null}.
     */
    public void onAttach(AttachEvent event, Consumer<AttachEvent> originalMethod) {
        this.informClientAboutSendingEvents(this.isReceivingSelectionEventsFromClient());
        originalMethod.accept(event);
    }

    /**
     * Informs client to not send events (if needed) and calls the original method.
     * @param event Event.
     * @param originalMethod Method to call. Must not be {@code null}.
     */
    public void onDetach(DetachEvent event, Consumer<DetachEvent> originalMethod) {
        // detaching means server should not be informed
        if(this.isReceivingSelectionEventsFromClient())
            this.informClientAboutSendingEvents(false);
        originalMethod.accept(event);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Registration addTextSelectionListener(TextSelectionListener<C> listener) {
        return this.eventBus.addListener((Class<TextSelectionEvent<C>>)(Class<?>)TextSelectionEvent.class, listener);
    }
    
    @Override
    public boolean isReceivingSelectionEventsFromClient() {
        return this.receivingSelectionEventsFromClient;
    }

    @Override
    public void setReceivingSelectionEventsFromClient(boolean receivingSelectionEventsFromClient) {
        this.receivingSelectionEventsFromClient = receivingSelectionEventsFromClient;
        this.informClientAboutSendingEvents(receivingSelectionEventsFromClient);
    }

    /**
     * This method should be called in response to {@code @ClientCallable void reinitialiseListeners()} on the owning object.
     */
    // fix for https://github.com/vaadin-miki/super-fields/issues/243 and the way components are initialised inside Grid
    // also included in the workaround for https://github.com/vaadin-miki/super-fields/issues/274 and GridPro
    public void reinitialiseListeners() {
        if(this.reinitialisationAttemptsLeft-- > 0)
            this.informClientAboutSendingEvents(this.isReceivingSelectionEventsFromClient());
        else LOGGER.warn("failed {} init attempts for {} (perhaps it is used in GridPro?) - browser-initiated text selection events may not work correctly", MAX_REINITIALISING_ATTEMPTS, this.source.getClass().getSimpleName());
    }
}
