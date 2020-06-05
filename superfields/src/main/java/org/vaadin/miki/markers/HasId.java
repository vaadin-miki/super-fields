package org.vaadin.miki.markers;

import java.util.Optional;

/**
 * Marker interface for objects that provide access to their {@code id} property.
 * Note: this is Vaadin-compatible, so the getter returns an {@link Optional}.
 * @author miki
 * @since 2020-06-05
 */
public interface HasId {

    /**
     * Sets this object's id.
     * @param id A new id. Can be {@code null} to remove existing id.
     */
    void setId(String id);

    /**
     * Returns the id, if exists.
     * @return The id, if any.
     */
    Optional<String> getId();

}
