package org.vaadin.miki.shared.dates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Objects;

/**
 * A description of pattern for dates.
 * @author miki
 * @since 2020-04-23
 */
public class DatePattern implements Serializable {

    private static final long serialVersionUID = 20200423L;

    /**
     * Defines available display order of dates.
     */
    public enum Order {DAY_MONTH_YEAR, MONTH_DAY_YEAR, YEAR_MONTH_DAY}

    /**
     * Shorthand for no separator (character 0).
     */
    public static final char NO_SEPARATOR = 0;

    /**
     * Default separator, {@code -}.
     */
    public static final char DEFAULT_SEPARATOR = '-';

    private static final Logger LOGGER = LoggerFactory.getLogger(DatePattern.class);

    private final String displayName;

    private char separator = DEFAULT_SEPARATOR;

    private boolean zeroPrefixedDay = true;

    private boolean zeroPrefixedMonth = true;

    private boolean shortYear = false;

    private int baseCentury = 21;

    private int centuryBoundaryYear = 40;

    private boolean previousCenturyBelowBoundary = false;

    private Order displayOrder = Order.YEAR_MONTH_DAY;

    /**
     * Creates a new pattern, with no display name.
     */
    public DatePattern() {
        this(null);
    }

    /**
     * Creates a new pattern using given display name.
     * @param string Display name. It is only used in {@link #toString()}.
     */
    public DatePattern(String string) {
        this.displayName = string;
    }

    /**
     * Returns current separator between parts.
     * @return Separator.
     */
    public char getSeparator() {
        return separator;
    }

    /**
     * Checks whether or not there is a separator present.
     * @return Whether or not {@link #getSeparator()} returns something else than {@link #NO_SEPARATOR}.
     */
    public boolean hasSeparator() {
        return this.getSeparator() != NO_SEPARATOR;
    }

    /**
     * Sets new separator.
     * If the separator is {@link #NO_SEPARATOR} (zero), zero-prefixed month and zero-prefixed day will be automatically enabled.
     * @param separator Separator between parts.
     */
    public void setSeparator(char separator) {
        this.separator = separator;
        if(separator == NO_SEPARATOR) {
            this.withZeroPrefixedDay(true).setZeroPrefixedMonth(true);
            LOGGER.warn("disabling date pattern separator, turning on zero-prefixed day and zero-prefixed month");
        }
    }

    /**
     * Chains {@link #setSeparator(char)} and returns itself.
     * @param separator Separator.
     * @return This.
     * @see #setSeparator(char)
     * @see #withoutSeparator()
     */
    public DatePattern withSeparator(char separator) {
        this.setSeparator(separator);
        return this;
    }

    /**
     * Identical to {@code withSeparator(DatePattern.NO_SEPARATOR}.
     * @return This.
     * @see #withSeparator(char)
     * @see #setSeparator(char)
     */
    public DatePattern withoutSeparator() {
        return this.withSeparator(NO_SEPARATOR);
    }

    /**
     * Checks whether days should be prefixed with {@code 0}.
     * @return Whether or not days will be zero-prefixed ({@code 09} instead of {@code 9}); {@code true} by default.
     */
    public boolean isZeroPrefixedDay() {
        return zeroPrefixedDay;
    }

    /**
     * Sets whether or not days should be prefixed with {@code 0}.
     * When there is no separator and this flag is turned off, the separator will be set to {@link #DEFAULT_SEPARATOR}.
     * @param zeroPrefixedDay When {@code true} and day is one digit, zero will be added in front of that number.
     */
    public void setZeroPrefixedDay(boolean zeroPrefixedDay) {
        this.zeroPrefixedDay = zeroPrefixedDay;
        if(!zeroPrefixedDay && !this.hasSeparator()) {
            this.setSeparator(DEFAULT_SEPARATOR);
            LOGGER.warn("turning off zero-prefixed day requires a separator, setting it to be the default one ({})", DEFAULT_SEPARATOR);
        }
    }

    /**
     * Chains {@link #setZeroPrefixedDay(boolean)} and returns itself.
     * @param zeroPrefixedDay Whether or not to zero-prefix days.
     * @return This.
     * @see #setZeroPrefixedDay(boolean)
     */
    public DatePattern withZeroPrefixedDay(boolean zeroPrefixedDay) {
        this.setZeroPrefixedDay(zeroPrefixedDay);
        return this;
    }

    /**
     * Checks whether months should be prefixed with {@code 0}.
     * @return Whether or not months will be zero-prefixed ({@code 09} instead of {@code 9}); {@code true} by default.
     */
    public boolean isZeroPrefixedMonth() {
        return zeroPrefixedMonth;
    }

    /**
     * Sets whether or not months should be prefixed with {@code 0}.
     * @param zeroPrefixedMonth When {@code true} and month is one digit, zero will be added in front of that number.
     */
    public void setZeroPrefixedMonth(boolean zeroPrefixedMonth) {
        this.zeroPrefixedMonth = zeroPrefixedMonth;
        if(!zeroPrefixedMonth && !this.hasSeparator()) {
            this.setSeparator(DEFAULT_SEPARATOR);
            LOGGER.warn("turning off zero-prefixed month requires a separator, setting it to be the default one ({})", DEFAULT_SEPARATOR);
        }
    }

    /**
     * Chains {@link #setZeroPrefixedMonth(boolean)} and returns itself.
     * @param zeroPrefixedMonth Whether or not to zero-prefix months.
     * @return This.
     * @see #setZeroPrefixedMonth(boolean)
     */
    public DatePattern withZeroPrefixedMonth(boolean zeroPrefixedMonth) {
        this.setZeroPrefixedMonth(zeroPrefixedMonth);
        return this;
    }

    /**
     * Checks whether year number should be shortened to two digits; {@code false} by default.
     * @return When {@code true}, only the last two digits of the year will be displayed.
     */
    public boolean isShortYear() {
        return shortYear;
    }

    /**
     * Sets whether or not to use only the last two digits of the year.
     * @param shortYear When {@code true}, year will be truncated to last two digits.
     * @see #setBaseCentury(int)
     * @see #setCenturyBoundaryYear(int)
     * @see #setPreviousCenturyBelowBoundary(boolean)
     */
    public void setShortYear(boolean shortYear) {
        this.shortYear = shortYear;
    }

    /**
     * Chains {@link #setShortYear(boolean)} and returns itself.
     * @param shortYear Whether or not to use shortened year.
     * @return This.
     */
    public DatePattern withShortYear(boolean shortYear) {
        this.setShortYear(shortYear);
        return this;
    }

    /**
     * Returns the base century for use with {@link #setShortYear(boolean)}.
     * @return The number corresponding to the century. Defaults to {@code 21}, which means years are assumed to be in {@code 2000}s.
     * @see #isShortYear()
     */
    public int getBaseCentury() {
        return baseCentury;
    }

    /**
     * Sets the base century for parsing dates with {@link #setShortYear(boolean)}.
     * @param baseCentury Base century to use (e.g. when set to {@code 19}, year will be in {@code 1800}s).
     * @see #setShortYear(boolean)
     */
    public void setBaseCentury(int baseCentury) {
        this.baseCentury = baseCentury;
    }

    /**
     * Chains {@link #setBaseCentury(int)} and returns itself.
     * @param baseCentury New value for base century.
     * @return This.
     */
    public DatePattern withBaseCentury(int baseCentury) {
        this.setBaseCentury(baseCentury);
        return this;
    }

    /**
     * Returns current display order.
     * @return A display {@link Order}. Defaults to {@link Order#YEAR_MONTH_DAY}.
     */
    public Order getDisplayOrder() {
        return displayOrder;
    }

    /**
     * Sets new display order.
     * @param displayOrder A display {@link Order}.
     */
    public void setDisplayOrder(Order displayOrder) {
        this.displayOrder = displayOrder;
    }

    /**
     * Chains {@link #setDisplayOrder(Order)} and returns itself.
     * @param displayOrder Display {@link Order} to use.
     * @return This.
     */
    public DatePattern withDisplayOrder(Order displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    /**
     * Returns boundary year to distinguish between {@link #getBaseCentury()} and the previous one, when {@link #isShortYear()} is {@code true}.
     * @return A year. Default is {@code 40}.
     */
    public int getCenturyBoundaryYear() {
        return centuryBoundaryYear;
    }

    /**
     * Sets new century boundary year for use with {@link #setShortYear(boolean)} and {@link #setBaseCentury(int)}.
     * @param centuryBoundaryYear A year. Please use values between {@code 0} and {@code 99}.
     */
    public void setCenturyBoundaryYear(int centuryBoundaryYear) {
        this.centuryBoundaryYear = Math.abs(centuryBoundaryYear) % 100;
    }

    /**
     * Chains {@link #setCenturyBoundaryYear(int)} and returns itself.
     * @param centuryBoundaryYear New century boundary year.
     * @return This.
     * @see #setCenturyBoundaryYear(int)
     */
    public DatePattern withCenturyBoundaryYear(int centuryBoundaryYear) {
        this.setCenturyBoundaryYear(centuryBoundaryYear);
        return this;
    }

    /**
     * Checks whether or not the years below the {@link #getCenturyBoundaryYear()} belong to century previous than {@link #getBaseCentury()}, used when {@link #isShortYear()} is {@code true}.
     * @return When {@code true}, years below {@link #getCenturyBoundaryYear()} are assumed to be in a century before {@link #getBaseCentury()}; {@code false} by default.
     */
    public boolean isPreviousCenturyBelowBoundary() {
        return previousCenturyBelowBoundary;
    }

    /**
     * Sets whether or not years before {@link #getCenturyBoundaryYear()} belong to the century previous than {@link #getBaseCentury()}.
     * @param previousCenturyBelowBoundary New value to use.
     */
    public void setPreviousCenturyBelowBoundary(boolean previousCenturyBelowBoundary) {
        this.previousCenturyBelowBoundary = previousCenturyBelowBoundary;
    }

    /**
     * Chains {@link #setPreviousCenturyBelowBoundary(boolean)} and returns itself.
     * @param belowBoundaryIsPreviousCentury Value to use.
     * @return This.
     * @see #setPreviousCenturyBelowBoundary(boolean)
     */
    public DatePattern withPreviousCenturyBelowBoundary(boolean belowBoundaryIsPreviousCentury) {
        this.setPreviousCenturyBelowBoundary(belowBoundaryIsPreviousCentury);
        return this;
    }

    /**
     * Returns display name defined in the constructor. The display name is irrelevant in {@link #equals(Object)} and {@link #hashCode()}.
     * @return Display name. May be {@code null} when no-arg constructor has been used.
     */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatePattern pattern = (DatePattern) o;
        return getSeparator() == pattern.getSeparator() &&
                isZeroPrefixedDay() == pattern.isZeroPrefixedDay() &&
                isZeroPrefixedMonth() == pattern.isZeroPrefixedMonth() &&
                isShortYear() == pattern.isShortYear() &&
                getBaseCentury() == pattern.getBaseCentury() &&
                getCenturyBoundaryYear() == pattern.getCenturyBoundaryYear() &&
                isPreviousCenturyBelowBoundary() == pattern.isPreviousCenturyBelowBoundary() &&
                getDisplayOrder() == pattern.getDisplayOrder();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSeparator(), isZeroPrefixedDay(), isZeroPrefixedMonth(), isShortYear(), getBaseCentury(), getCenturyBoundaryYear(), isPreviousCenturyBelowBoundary(), getDisplayOrder());
    }

    @Override
    public String toString() {
        return getDisplayName() == null ?
                "DatePattern{" +
                "separator=" + separator +
                ", zeroPrefixedDay=" + zeroPrefixedDay +
                ", zeroPrefixedMonth=" + zeroPrefixedMonth +
                ", shortYear=" + shortYear +
                ", baseCentury=" + baseCentury +
                ", centuryBoundaryYear=" + centuryBoundaryYear +
                ", previousCenturyBelowBoundary=" + previousCenturyBelowBoundary +
                ", displayOrder=" + displayOrder +
                '}' :
                getDisplayName();
    }
}
