package org.vaadin.miki.markers;

/**
 * Marker interface for components that can be clicked.
 * @author miki
 * @since 2020-07-07
 */
@FunctionalInterface
public interface Clickable {

    /**
     * Clicks this object.
     * What it means is left for implementations to figure out.
     */
    void click();

}
