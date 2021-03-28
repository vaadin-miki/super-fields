package org.vaadin.miki.markers;

/**
 * Marker interface for objects that can modify text at given coordinates.
 * @author miki
 * @since 2021-03-26
 */
@FunctionalInterface
public interface CanModifyText {

    /**
     * Modifies the text at given coordinates.
     * @param replacement Text to put.
     * @param from The starting index of what to replace.
     * @param to The end index of what to replace.
     */
    void modifyText(String replacement, int from, int to);

    /**
     * Modifies the text currently selected.
     * @param replacement Text to put.
     */
    default void modifyText(String replacement) {
        this.modifyText(replacement, -1, -1);
    }

    /**
     * Modifies the text currently selected - from the specified index to the end of current selection.
     * @param replacement Text to put.
     * @param from Starting index.
     */
    default void modifyText(String replacement, int from) {
        this.modifyText(replacement, from, -1);
    }

}
