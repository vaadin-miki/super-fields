package org.vaadin.miki.superfields.dates.patterns;

/**
 * Defines available display order for {@link SimpleDatePattern}.
 * @author miki
 * @since 2020-05-26
 */
public enum DisplayOrder {

    DAY_MONTH_YEAR(0, 1, 2),
    MONTH_DAY_YEAR(1, 0, 2),
    YEAR_MONTH_DAY(2, 1, 0);

    private final int dayPosition;
    private final int monthPosition;
    private final int yearPosition;

    DisplayOrder(int dayPosition, int monthPosition, int yearPosition) {
        this.dayPosition = dayPosition;
        this.monthPosition = monthPosition;
        this.yearPosition = yearPosition;
    }

    /**
     * The position of day part in the order.
     * @return Position of the day in the display order.
     */
    public int getDayPosition() {
        return dayPosition;
    }

    /**
     * The position of month part in the order.
     * @return Position of the month in the display order.
     */
    public int getMonthPosition() {
        return monthPosition;
    }

    /**
     * The position of year part in the order.
     * @return Position of the year in the display order.
     */
    public int getYearPosition() {
        return yearPosition;
    }
}
