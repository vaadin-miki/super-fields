package org.vaadin.miki.superfields.dates;

/**
 * Enumeration class with some common {@link DatePattern}s.
 * @author miki
 * @since 2020-04-24
 */
public final class DatePatterns {

    /**
     * Default pattern. Uses full year, zero-prefixed month and day, separated by {@code -}.
     */
    public static final DatePattern YYYY_MM_DD = new DatePattern("yyyy-MM-dd");

    /**
     * Uses zero-prefixed day and month, full year, separated by {@code .}.
     */
    public static final DatePattern DD_MM_YYYY_DOTTED = new DatePattern("dd.MM.yyyy").
            withDisplayOrder(DatePattern.Order.DAY_MONTH_YEAR).
            withSeparator('.');

    /**
     * Uses day, month and short year with century boundary year 40 (years less than 40 are from 21st century), separated by {@code .}.
     */
    public static final DatePattern D_M_YY_DOTTED = new DatePattern("d.M.yy").
            withDisplayOrder(DatePattern.Order.DAY_MONTH_YEAR).
            withShortYear(true).
            withSeparator('.').withZeroPrefixedDay(false).withZeroPrefixedMonth(false).
            withBaseCentury(21).withCenturyBoundaryYear(40).withPreviousCenturyBelowBoundary(false);

    /**
     * Uses month, day and full year, separated by {@code /}.
     */
    public static final DatePattern M_D_YYYY_SLASH = new DatePattern("M/d/yyyy").
            withDisplayOrder(DatePattern.Order.MONTH_DAY_YEAR).
            withSeparator('/').withZeroPrefixedDay(false).withZeroPrefixedMonth(false);

    private DatePatterns() {} // instances not needed
}
