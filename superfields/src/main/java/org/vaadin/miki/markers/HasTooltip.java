package org.vaadin.miki.markers;

/**
 * Marker interface for objects that have a tooltip.
 *
 * @author miki
 * @since 2023-01-31
 */
public interface HasTooltip {

    /**
     * Returns the current tooltip text of the object.
     * @return Current tooltip.
     */
    String getTooltipText();

    /**
     * Sets new tooltip text of the object.
     * @param tooltipText New tooltip text.
     */
    void setTooltipText(String tooltipText);

}
