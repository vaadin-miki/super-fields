package org.vaadin.miki.superfields.itemgrid;

import java.io.Serializable;

/**
 * Defines a strategy for padding each row in the grid.
 * Note that by default the strategy has no access to the data in the grid.
 * @author miki
 * @since 2020-12-15
 */
@FunctionalInterface
public interface RowPaddingStrategy extends Serializable {

    /**
     * Calculates padding for a given row.
     * @param rowNumber Number of the row for which the padding is done (zero-based).
     * @param gridColumns Number of columns (passed from item grid).
     * @param itemsLeft Number of items left in the grid (includes items in the current row).
     * @return Information about padding. To avoid endless loops, item grid will check if padding is at least 1 less than grid's column count.
     */
    RowPadding getRowPadding(int rowNumber, int gridColumns, int itemsLeft);

}
