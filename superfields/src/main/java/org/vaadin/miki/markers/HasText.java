package org.vaadin.miki.markers;

/**
 * Marker interface for objects that have a String property called {@code text}.
 * It is up to the implementation to define what this means.
 *
 * @author miki
 * @since 2021-08-31
 */
public interface HasText {

    /**
     * Sets text of this object.
     * @param text Text to set.
     */
    void setText(String text);

    /**
     * Returns current text of this object.
     * @return Current text.
     */
    String getText();

}
