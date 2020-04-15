package org.vaadin.miki.superfields.itemgrid;

import java.util.Objects;

/**
 * Stores information about a cell being selected or not.
 *
 * @param <T> Type of data associated with each cell event.
 * @author miki
 * @since 2020-04-15
 */
public class CellSelectionEvent<T> {

    private final CellInformation<T> cellInformation;

    private final boolean selected;

    /**
     * Creates the event.
     * @param information Information about the affected cell.
     * @param selected Whether or not the cell is now selected.
     */
    public CellSelectionEvent(CellInformation<T> information, boolean selected) {
        this.cellInformation = information;
        this.selected = selected;
    }

    /**
     * Helper method that creates a new event with selection reversed.
     * @return A new object, identical to the current one except {@link #isSelected()} flag.
     */
    public CellSelectionEvent<T> reversed() {
        return new CellSelectionEvent<>(this.cellInformation, !this.isSelected());
    }

    public boolean isSelected() {
        return selected;
    }

    public CellInformation<T> getCellInformation() {
        return cellInformation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellSelectionEvent<?> that = (CellSelectionEvent<?>) o;
        return isSelected() == that.isSelected() &&
                getCellInformation().equals(that.getCellInformation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCellInformation(), isSelected());
    }

    @Override
    public String toString() {
        return "CellSelectionEvent{" +
                "cellInformation=" + cellInformation +
                ", selected=" + selected +
                '}';
    }
}
