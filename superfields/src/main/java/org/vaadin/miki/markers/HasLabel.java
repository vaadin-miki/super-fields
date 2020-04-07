package org.vaadin.miki.markers;

/**
 * Marker interface for components that have a label.
 * This is a workaround for <a href="https://github.com/vaadin/flow/issues/3241">issue #3241 in Vaadin Flow</a>.
 * @author miki
 * @since 2020-04-07
 */
public interface HasLabel {

    /**
     * Sets the label associated with this object.
     * @param label Label to set. Can be {@code null}, which means no label.
     */
    void setLabel(String label);

    /**
     * Returns the label associated with this object.
     * @return Current label. Can be {@code null}, meaning no label.
     */
    String getLabel();

}
