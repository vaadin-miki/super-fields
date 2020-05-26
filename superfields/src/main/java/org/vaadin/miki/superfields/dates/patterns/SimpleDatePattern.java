package org.vaadin.miki.superfields.dates.patterns;

import org.vaadin.miki.superfields.dates.ClientSideSupportedDatePattern;
import org.vaadin.miki.superfields.dates.DatePattern;

import java.time.LocalDate;
import java.util.Objects;

/**
 * An implementation of {@link DatePattern} that works on predefined pattern parts.
 * @author miki
 * @since 2020-05-26
 */
public class SimpleDatePattern implements ClientSideSupportedDatePattern {

    private static final long serialVersionUID = 20200423L;

    private final String displayName;

    private char separator = '-';

    private boolean zeroPrefixedDay = true;

    private boolean zeroPrefixedMonth = true;

    private boolean shortYear = false;

    private int baseCentury = 21;

    private int centuryBoundaryYear = 40;

    private boolean previousCenturyBelowBoundary = false;

    private DisplayOrder displayOrder = DisplayOrder.YEAR_MONTH_DAY;

    /**
     * Creates a new pattern, with no display name.
     */
    public SimpleDatePattern() {
        this(null);
    }

    /**
     * Creates a new pattern using given display name.
     * @param displayName Display name. It is only used in {@link #toString()}.
     */
    public SimpleDatePattern(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public LocalDate parseDate(String text) {
        final String splitOn =
            (this.getSeparator() == '.' || this.getSeparator() == '\\' || this.getSeparator() == '^' || this.getSeparator() == '$') ?
                    String.valueOf('\\') + this.getSeparator() :
                    String.valueOf(this.getSeparator());
        final String[] parts = text.split(splitOn);
        final LocalDate today = LocalDate.now();
        int year = parts.length > this.getDisplayOrder().getYearPosition() ?
                Integer.parseInt(parts[this.getDisplayOrder().getYearPosition()]) :
                today.getYear();
        // year may be shortened
        if(this.isShortYear() && year < 100 && year >= 0) {
            if((year >= this.getCenturyBoundaryYear() && this.isPreviousCenturyBelowBoundary()) || (year <= this.getCenturyBoundaryYear() && !this.isPreviousCenturyBelowBoundary()))
                year += 100;
            year += (this.getBaseCentury()-2) * 100;
        }

        // parsing month requires no extra work
        final int month = parts.length > this.getDisplayOrder().getMonthPosition() ?
                Integer.parseInt(parts[this.getDisplayOrder().getMonthPosition()]) :
                today.getMonthValue();
        // same as parsing day
        final int day = parts.length > this.getDisplayOrder().getDayPosition() ?
                Integer.parseInt(parts[this.getDisplayOrder().getDayPosition()]) :
                today.getDayOfMonth();
        return LocalDate.of(year, month, day);
    }

    @Override
    public String formatDate(LocalDate date) {
        final String[] parts = new String[3];
        parts[this.getDisplayOrder().getDayPosition()] = this.isZeroPrefixedDay() ? String.format("%02d", date.getDayOfMonth()) : String.valueOf(date.getDayOfMonth());
        parts[this.getDisplayOrder().getMonthPosition()] = this.isZeroPrefixedMonth() ? String.format("%02d", date.getMonthValue()) : String.valueOf(date.getMonthValue());
        parts[this.getDisplayOrder().getYearPosition()] = this.isShortYear() ? String.format("%02d", date.getYear() % 100) : String.valueOf(date.getYear());
        return String.join(String.valueOf(this.getSeparator()), parts);
    }

    /**
     * Returns the pattern descriptor string expected by the client-side code.
     * The descriptor starts with a separator character, followed (in whatever order is decided) by
     * {@code 0d} or {@code _d} for (zero prefixed) day, {@code 0M} or {@code _M} for (zero prefixed) month,
     * {@code 0y} or {@code _y} for short or full year. If short year is used, the resulting string ends
     * with {@code +} or {@code -} depending on {@link SimpleDatePattern#isPreviousCenturyBelowBoundary()}, followed by
     * two digits of the {@link SimpleDatePattern#getBaseCentury()} and two digits of {@link SimpleDatePattern#getCenturyBoundaryYear()}.
     *
     * @return Current representation of this object.
     */
    @Override
    public String getClientSidePattern() {
        final String dayPart = this.isZeroPrefixedDay() ? "0d" : "_d";
        final String monthPart = this.isZeroPrefixedMonth() ? "0M" : "_M";
        final String yearPart = this.isShortYear() ? "0y" : "_y";

        StringBuilder builder = new StringBuilder();
        builder.append(this.getSeparator());
        switch (this.getDisplayOrder()) {
            case DAY_MONTH_YEAR:
                builder.append(dayPart).append(monthPart).append(yearPart);
                break;
            case MONTH_DAY_YEAR:
                builder.append(monthPart).append(dayPart).append(yearPart);
                break;
            default:
                builder.append(yearPart).append(monthPart).append(dayPart);
                break;
        }
        if (this.isShortYear()) {
            builder.append(this.isPreviousCenturyBelowBoundary() ? '+' : '-');
            builder.append(String.format("%02d", this.getBaseCentury() % 100));
            builder.append(String.format("%02d", this.getCenturyBoundaryYear() % 100));
        }

        return builder.toString();
    }

    /**
     * Returns current separator between parts.
     * @return Separator.
     */
    public char getSeparator() {
        return separator;
    }

    /**
     * Sets new separator.
     * @param separator Separator between parts.
     */
    public void setSeparator(char separator) {
        this.separator = separator;
    }

    /**
     * Chains {@link #setSeparator(char)} and returns itself.
     * @param separator Separator.
     * @return This.
     * @see #setSeparator(char)
     */
    public SimpleDatePattern withSeparator(char separator) {
        this.setSeparator(separator);
        return this;
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
     * @param zeroPrefixedDay When {@code true} and day is one digit, zero will be added in front of that number.
     */
    public void setZeroPrefixedDay(boolean zeroPrefixedDay) {
        this.zeroPrefixedDay = zeroPrefixedDay;
    }

    /**
     * Chains {@link #setZeroPrefixedDay(boolean)} and returns itself.
     * @param zeroPrefixedDay Whether or not to zero-prefix days.
     * @return This.
     * @see #setZeroPrefixedDay(boolean)
     */
    public SimpleDatePattern withZeroPrefixedDay(boolean zeroPrefixedDay) {
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
    }

    /**
     * Chains {@link #setZeroPrefixedMonth(boolean)} and returns itself.
     * @param zeroPrefixedMonth Whether or not to zero-prefix months.
     * @return This.
     * @see #setZeroPrefixedMonth(boolean)
     */
    public SimpleDatePattern withZeroPrefixedMonth(boolean zeroPrefixedMonth) {
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
    public SimpleDatePattern withShortYear(boolean shortYear) {
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
    public SimpleDatePattern withBaseCentury(int baseCentury) {
        this.setBaseCentury(baseCentury);
        return this;
    }

    /**
     * Returns current display order.
     * @return A display {@link DisplayOrder}. Defaults to {@link DisplayOrder#YEAR_MONTH_DAY}.
     */
    public DisplayOrder getDisplayOrder() {
        return displayOrder;
    }

    /**
     * Sets new display order.
     * @param displayOrder A display {@link DisplayOrder}.
     */
    public void setDisplayOrder(DisplayOrder displayOrder) {
        this.displayOrder = displayOrder;
    }

    /**
     * Chains {@link #setDisplayOrder(DisplayOrder)} and returns itself.
     * @param displayOrder Display {@link DisplayOrder} to use.
     * @return This.
     */
    public SimpleDatePattern withDisplayOrder(DisplayOrder displayOrder) {
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
    public SimpleDatePattern withCenturyBoundaryYear(int centuryBoundaryYear) {
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
    public SimpleDatePattern withPreviousCenturyBelowBoundary(boolean belowBoundaryIsPreviousCentury) {
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
        SimpleDatePattern pattern = (SimpleDatePattern) o;
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
