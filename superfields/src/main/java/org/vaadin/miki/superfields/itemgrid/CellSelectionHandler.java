package org.vaadin.miki.superfields.itemgrid;

/**
 * Interface for objects handling {@link CellSelectionEvent}s in {@link ItemGrid}.
 * @param <T> Type of data associated with both the grid and the event.
 * @author miki
 * @since 2020-04-15
 */
@FunctionalInterface
public interface CellSelectionHandler<T> {

    /**
     * Triggered whenever a cell selection has been changed.
     * @param event Information about the event.
     */
    void cellSelectionChanged(CellSelectionEvent<T> event);
}
