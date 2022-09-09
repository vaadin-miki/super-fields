package org.vaadin.miki.markers;

/**
 * Marker interface for objects that can be required. Whatever that means is left for implementations to decide.
 *
 * @author miki
 * @since 2022-09-09
 */
// reported by Koen De Cock in #386
public interface HasRequired {

    /**
     * Checks if this object is required.
     * @return Whether this component is required.
     */
    boolean isRequired();

    /**
     * Sets the new required state.
     * @param required Whether this component should be required.
     */
    void setRequired(boolean required);

}
