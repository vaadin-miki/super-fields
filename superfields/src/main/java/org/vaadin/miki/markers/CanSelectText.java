package org.vaadin.miki.markers;

import com.vaadin.flow.component.HasElement;

/**
 * Marker interface for components that can select text.
 * What selecting text means and how it is done is left to the implementations.
 * @author miki
 * @since 2020-05-29
 */
public interface CanSelectText extends HasElement {

    /**
     * Selects entire text in the component.
     */
    void selectAll();

    /**
     * Removes the current selection and selects no text.
     */
    void selectNone();

    /**
     * Selects text starting from index {@code from} (inclusive) and ending at index {@code to} (exclusive).
     * @param from Starting index (inclusive).
     * @param to Ending index (exclusive).
     */
    void select(int from, int to);

}
