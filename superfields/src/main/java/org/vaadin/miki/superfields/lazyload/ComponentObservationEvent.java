package org.vaadin.miki.superfields.lazyload;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

/**
 * Information about the component being observed by {@link ComponentObserver}.
 * @author miki
 * @since 2020-04-28
 */
public class ComponentObservationEvent extends ComponentEvent<ComponentObserver> {

    private final Component observedComponent;
    private final double visibilityRange;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred
     * @param observedComponent Component that was observed.
     * @param visibilityRange Visibility range when the event happened.
     * @throws IllegalArgumentException if source is null
     */
    public ComponentObservationEvent(ComponentObserver source, Component observedComponent, double visibilityRange) {
        super(source, true);
        this.observedComponent = observedComponent;
        this.visibilityRange = visibilityRange;
    }

    /**
     * Returns the component that has been observed.
     * @return A {@link Component}.
     */
    public Component getObservedComponent() {
        return this.observedComponent;
    }

    /**
     * Returns the visibility range that happened to the observed component.
     * @return A number between {@code 0} and {@code 1}, close to one of the registered visibility ranges.
     */
    public double getVisibilityRange() {
        return this.visibilityRange;
    }

    /**
     * Helper method for checking if the observed component is no longer visible.
     * @return {@code getVisibilityRange() == 0.0d}
     */
    public final boolean isNotVisible() {
        return this.getVisibilityRange() == 0.0d;
    }

    /**
     * Helper method for checking if the observed component is fully visible.
     * @return {@code getVisibilityRange() == 1.0d}
     */
    public final boolean isFullyVisible() {
        return this.getVisibilityRange() == 1.0d;
    }
}
