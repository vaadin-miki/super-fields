package org.vaadin.miki.superfields.itemgrid;

import com.vaadin.flow.component.Component;

/**
 * Interface for objects generating cells.
 * @param <T> Type of data associated with the cell.
 * @author miki
 * @since 2020-04-15
 */
@FunctionalInterface
public interface CellGenerator<T> {

    /**
     * Constructs a component that would represent given value in given row and column.
     * @param value Value to generate component for.
     * @param row Row the component will be placed in.
     * @param column Column the component will be placed in.
     * @return A component. Must not be {@code null}.
     */
    Component generateComponent(T value, int row, int column);

}
