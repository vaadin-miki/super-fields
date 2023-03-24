package org.vaadin.miki.markers;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasValue;

/**
 * Marker interface for objects that can be in read-only state without having value.
 * @author miki
 * @since 2021-09-04
 */
public interface HasReadOnly {

    /**
     * Checks whether this object is in read-only mode.
     * @return Whether the object is in read-only mode.
     */
    boolean isReadOnly();

    /**
     * Sets the new read-only state.
     * @param readOnly Whether the object should be in read-only mode.
     */
    void setReadOnly(boolean readOnly);

    /**
     * Helper method to update read-only state of a component if it supports the method.
     * This method is recursive, as it applies itself to the contents of {@link Component#getChildren()}.
     * @param readOnly New state.
     * @param component Component.
     *                 If it implements {@link HasReadOnly} or {@link HasValue}, the state will be updated.
     *                 If it implements {@link HasEnabled} and does not implement {@link HasComponents}, read-only means disabled.
     *                 Otherwise, nothing happens.
     */
    static void setReadOnly(boolean readOnly, Component component) {
        if(component instanceof HasReadOnly hasReadOnly)
            hasReadOnly.setReadOnly(readOnly);
        else if(component instanceof HasValue<?, ?> hasValue)
            hasValue.setReadOnly(readOnly);
        else if(component instanceof HasEnabled hasEnabled && !(component instanceof HasComponents)) // HasComponents implements HasEnabled, that caused #402
            hasEnabled.setEnabled(!readOnly);
        // delegate to children
        component.getChildren().forEach(child -> setReadOnly(readOnly, child));
    }

}
