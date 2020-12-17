package org.vaadin.miki.superfields.itemgrid;

import java.util.Objects;

/**
 * Contains data about how to pad a given row.
 *
 * @author miki
 * @since 2020-12-15
 */
// note: this would make a nice record, but those are available in Java 14
public final class RowPadding {

    /**
     * Constant for no padding.
     */
    public static final RowPadding NONE = new RowPadding(0, 0);

    /**
     * Creates {@link RowPadding} where given number of cells are centred in given number of columns (optionally putting one odd cell to the beginning).
     * @param cells Number of cells to split. If not less than columns, {@link #NONE} will be returned.
     * @param columns Number of columns to fill. Must be a positive number.
     * @param whenOddPutMoreToBeginning When there is odd number of empty cells to distribute and this parameter is {@code true}, extra empty cell will be added to the beginning. Otherwise, it will be added to the end if needed.
     * @return Row padding data.
     */
    public static RowPadding centred(int cells, int columns, boolean whenOddPutMoreToBeginning) {
        if(cells >= columns || columns == 0)
            return NONE;
        else {
            final int split = (columns - cells) / 2;
            if((columns - cells) % 2 == 0)
                return new RowPadding(split, split);
            else if(whenOddPutMoreToBeginning)
                return new RowPadding(split+1, split);
            else return new RowPadding(split, split+1);
        }
    }

    private final int beginning;
    private final int end;

    /**
     * Creates information about padding a row.
     * @param beginning Number of empty cells at the beginning of the row. Must not be a negative number,
     * @param end Number of empty cells at the end of the row. Must not be a negative number.
     * @throws IllegalArgumentException When either {@code beginning} or {@code end} are negative.
     */
    public RowPadding(int beginning, int end) {
        if(beginning < 0 || end < 0)
            throw new IllegalArgumentException("row padding cannot have negative padding at beginning or end");
        this.beginning = beginning;
        this.end = end;
    }

    /**
     * Returns the number of empty cells at the beginning of the row.
     * @return Non-negative number of empty cells at the beginning of the row.
     */
    public int getBeginning() {
        return this.beginning;
    }

    /**
     * Returns the number of empty cells at the end of the row.
     * @return Non-negative number of empty cells at the beginning of the row.
     */
    public int getEnd() {
        return this.end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RowPadding that = (RowPadding) o;
        return getBeginning() == that.getBeginning() && getEnd() == that.getEnd();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBeginning(), getEnd());
    }
}
