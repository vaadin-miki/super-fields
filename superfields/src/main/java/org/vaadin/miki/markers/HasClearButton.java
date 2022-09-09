package org.vaadin.miki.markers;

/**
 * Marker interface for objects that have a button for clearing their value.
 * @author miki
 * @since 2021-01-10
 */
public interface HasClearButton {

    /**
     * Checks if the clear button is currently visible.
     * @return Whether the clear button is visible. Implementations are free to determine the default value.
     */
    boolean isClearButtonVisible();

    /**
     * Sets the visibility of the clear button.
     * @param state New state.
     */
    void setClearButtonVisible(boolean state);

}