package org.vaadin.miki.superfields.dates;

import org.vaadin.miki.shared.dates.DatePattern;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Utility class that generates JS code needed by {@code vaadin-date-picker} and {@code vaadin-date-time-picker}.
 * @author miki
 * @since 2020-04-24
 * @deprecated This class is not needed, there is dedicated client-side code now.
 */
@Deprecated // how come this is still around?
public class DatePatternJsGenerator {

    /**
     * Instance of the generator, if there is ever a need to use it elsewhere.
     */
    public static final DatePatternJsGenerator INSTANCE = new DatePatternJsGenerator();

    private final Map<Integer, Function<Supplier<Boolean>, String>> formats = new HashMap<>();

    private final Map<DatePattern.Order, String> orders = new EnumMap<>(DatePattern.Order.class);

    private DatePatternJsGenerator() {
        this.formats.put((int)'d', flag -> flag.get() ? "String(date.day).padStart(2, '0')" : "String(date.day)");
        this.formats.put((int)'M', flag -> flag.get() ? "String(date.month + 1).padStart(2, '0')" : "String(date.month + 1)");
        this.formats.put((int)'y', flag -> flag.get() ? "(date.year < 10 ? '0'+String(date.year) : String(date.year).substr(-2))" : "String(date.year)");

        this.orders.put(DatePattern.Order.DAY_MONTH_YEAR, "dMy");
        this.orders.put(DatePattern.Order.MONTH_DAY_YEAR, "Mdy");
        this.orders.put(DatePattern.Order.YEAR_MONTH_DAY, "yMd");
    }

    /**
     * Returns a routine for date formatting, based on given {@link DatePattern}.
     * @param pattern A {@link DatePattern}.
     * @return JavaScript function body, assuming {@code date} is a variable that holds, well, date.
     */
    public String formatDate(DatePattern pattern) {
        Map<Integer, Supplier<Boolean>> patternMethods = new HashMap<>();
        patternMethods.put((int)'d', pattern::isZeroPrefixedDay);
        patternMethods.put((int)'M', () -> pattern.getMonthDisplayMode() == DatePattern.MonthDisplayMode.ZERO_PREFIXED_NUMBER);
        patternMethods.put((int)'y', pattern::isShortYear);

        return this.orders.get(pattern.getDisplayOrder()).chars().
                mapToObj(c -> this.formats.get(c).apply(patternMethods.get(c))).
                collect(Collectors.joining(", ", "return [", "].join('"+pattern.getSeparator()+"');"));
        // should return "[field 1, field 2, field 3].join(delimiter)" in JS
    }

    /**
     * Returns a routine for date parsing, based on given {@link DatePattern}.
     * @param pattern A {@link DatePattern}.
     * @return JavaScript function body, assuming {@code text} holds text.
     */
    public String parseDate(DatePattern pattern) {
        final int dayPosition = this.orders.get(pattern.getDisplayOrder()).indexOf('d');
        final int monthPosition = this.orders.get(pattern.getDisplayOrder()).indexOf('M');

        final StringBuilder result = new StringBuilder();

        result.append("const parts = text.split('").append(pattern.getSeparator()).append("'); ");
        result.append("const today = new Date(); let date, month = today.getMonth(), year = today.getFullYear(); ");
        result.append("if(parts.length === 3) {");

        // order is defined in the pattern, all three parts are present
        result.append(" year = parseInt(parts[").append(this.orders.get(pattern.getDisplayOrder()).indexOf('y')).append("]); ");
        result.append(" month = parseInt(parts[").append(monthPosition).append("]) - 1; ");
        result.append(" date = parseInt(parts[").append(dayPosition).append("]); ");
        // if year is short, extra processing is needed
        if(pattern.isShortYear()) {
            result.append("if (year < ").append(pattern.getCenturyBoundaryYear()).append(") {");
            result.append(" year += ").append(pattern.isPreviousCenturyBelowBoundary() ? pattern.getBaseCentury()-2 : pattern.getBaseCentury()-1).append("00;");
            result.append("} else if (year < 100) {");
            result.append(" year += ").append(pattern.isPreviousCenturyBelowBoundary() ? pattern.getBaseCentury()-1 : pattern.getBaseCentury()-2).append("00;");
            result.append("} ");
        }

        result.append("} else if (parts.length === 2) {");
        // when two fields are given, year is omitted (always current year)
        // whether day is first, depends on the order
        if(dayPosition < monthPosition)
            result.append(" date = parseInt(parts[0]); month = parseInt(parts[1])-1; ");
        else result.append(" date = parseInt(parts[1]); month = parseInt(parts[0])-1; ");

        result.append("} else if (parts.length === 1) {");
        result.append(" date = parseInt(parts[0]); ");
        result.append("} ");
        result.append("if (date !== undefined) {return {day: date, month, year};}");

        return result.toString();
    }

    /**
     * Helper method to construct date formatting JavaScript function.
     * @param pattern A {@link DatePattern}.
     * @return JavaScript function body.
     * @see #formatDate(DatePattern)
     */
    public static String buildFormatDateFunction(DatePattern pattern) {
        return INSTANCE.formatDate(pattern);
    }

    /**
     * Helper method to construct date parsing JavaScript function.
     * @param pattern A {@link DatePattern}.
     * @return JavaScript function body.
     * @see #parseDate(DatePattern)
     */
    public static String buildParseDateFunction(DatePattern pattern) {
        return INSTANCE.parseDate(pattern);
    }

}
