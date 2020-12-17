package org.vaadin.miki.superfields.itemgrid;

/**
 * Container class for typical {@link RowPaddingStrategy} variants.
 * @author miki
 * @since 2020-12-15
 */
public final class RowPaddingStrategies {

    /**
     * Indicates no padding at all (no empty cells generated).
     */
    public static final RowPaddingStrategy NO_PADDING = (rowNumber, gridColumns, itemsLeft) -> RowPadding.NONE;

    /**
     * Indicates padding at the end of the last row.
     */
    public static final RowPaddingStrategy LAST_ROW_FILL_END = (rowNumber, gridColumns, itemsLeft) -> itemsLeft >= gridColumns ? RowPadding.NONE : new RowPadding(0, gridColumns-itemsLeft);

    /**
     * Indicates padding at the beginning of the last row.
     */
    public static final RowPaddingStrategy LAST_ROW_FILL_BEGINNING = (rowNumber, gridColumns, itemsLeft) -> itemsLeft >= gridColumns ? RowPadding.NONE : new RowPadding(gridColumns-itemsLeft, 0);

    /**
     * Indicates last row should be centred, with optional extra empty cell put at the beginning.
     */
    public static final RowPaddingStrategy LAST_ROW_CENTRE_BEGINNING = (rowNumber, gridColumns, itemsLeft) -> itemsLeft >= gridColumns ? RowPadding.NONE : RowPadding.centred(itemsLeft, gridColumns, true);

    /**
     * Indicates last row should be centred, with optional extra empty cell put at the end.
     */
    public static final RowPaddingStrategy LAST_ROW_CENTRE_END = (rowNumber, gridColumns, itemsLeft) -> itemsLeft >= gridColumns ? RowPadding.NONE : RowPadding.centred(itemsLeft, gridColumns, false);

    /**
     * Indicates padding at the end of the first row.
     */
    public static final RowPaddingStrategy FIRST_ROW_FILL_END = (rowNumber, gridColumns, itemsLeft) -> rowNumber == 0 ? new RowPadding(0, gridColumns - (itemsLeft%gridColumns)) : RowPadding.NONE;

    /**
     * Indicates padding at the beginning of the first row.
     */
    public static final RowPaddingStrategy FIRST_ROW_FILL_BEGINNING = (rowNumber, gridColumns, itemsLeft) -> rowNumber == 0 ? new RowPadding(gridColumns - (itemsLeft%gridColumns), 0) : RowPadding.NONE;

    private RowPaddingStrategies() {
        // no instances allowed
    }
}
