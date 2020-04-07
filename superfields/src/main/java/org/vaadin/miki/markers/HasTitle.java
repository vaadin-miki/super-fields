package org.vaadin.miki.markers;

/**
 * Marker interface for components that have a title (or, a description tooltip shown on hovering).
 * @author miki
 * @since 2020-04-07
 */
public interface HasTitle {

    /**
     * Sets the new title (description) for this object.
     * @param title New title. Can be {@code null}, meaning no title.
     */
    void setTitle(String title);

    /**
     * Returns the current title (description) of this object.
     * @return Current title. Can be {@code null}, meaning no title.
     */
    String getTitle();

}
