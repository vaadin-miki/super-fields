package org.vaadin.miki.superfields.collections;

/**
 * Marker interface for objects having an index.
 * It is left out for implementations to define what that index means.
 *
 * @author miki
 * @since 2021-08-18
 */
public interface HasIndex {

    /**
     * Returns the current index of the object.
     * @return Current index.
     */
    int getIndex();

    /**
     * Changes the index of the object.
     * @param index New index.
     */
    void setIndex(int index);

}
