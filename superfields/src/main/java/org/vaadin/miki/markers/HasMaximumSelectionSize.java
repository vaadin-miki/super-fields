package org.vaadin.miki.markers;

/**
 * Marker interface for objects that have a limit to selection size.
 * Whatever selection size means is left for implementations to decide.
 * @author miki
 * @since 2020-12-09
 */
public interface HasMaximumSelectionSize {

    /**
     * Indicates unlimited selection size. This should be the default for each implementing class.
     * Any maximum selection size not larger than this number means unlimited selection.
     */
    int UNLIMITED = 0;

    /**
     * Defines the new maximum selection size.
     * When current selection is above the new limit, the selection should change so that it fits the new limit.
     * @param maximumSelectionSize Maximum selection size.
     */
    void setMaximumSelectionSize(int maximumSelectionSize);

    /**
     * Returns the current maximum selection size.
     * @return Maximum allowed selection size; {@link #UNLIMITED} by default.
     */
    int getMaximumSelectionSize();

}
