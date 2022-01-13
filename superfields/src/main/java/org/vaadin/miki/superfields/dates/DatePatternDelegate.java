package org.vaadin.miki.superfields.dates;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import org.vaadin.miki.markers.HasDatePattern;
import org.vaadin.miki.shared.dates.DatePattern;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Helper class that contains code related handling date patterns.
 * Internal use only.
 *
 * @author miki
 * @since 2020-05-02
 */
final class DatePatternDelegate<C extends Component & HasDatePattern & HasSuperDatePickerI18N> implements Serializable {

    private static final long serialVersionUID = 20200506L;

    /**
     * Stores month display mode patterns that are understood by client side.
     */
    private static final Map<DatePattern.MonthDisplayMode, String> MONTH_DISPLAY_PATTERNS = initMonthPatterns();
    private static Map<DatePattern.MonthDisplayMode, String> initMonthPatterns() {
        final Map<DatePattern.MonthDisplayMode, String> result = new EnumMap<>(DatePattern.MonthDisplayMode.class);
        result.put(DatePattern.MonthDisplayMode.NAME, "mM");
        result.put(DatePattern.MonthDisplayMode.NUMBER, "_M");
        result.put(DatePattern.MonthDisplayMode.ZERO_PREFIXED_NUMBER, "0M");
        return result;
    }

    /**
     * Builds pattern according to the defined order.
     */
    private static final Map<DatePattern.Order, BiConsumer<StringBuilder, String[]>> ORDER_BUILDERS = initOrderBuilders();
    private static Map<DatePattern.Order, BiConsumer<StringBuilder, String[]>> initOrderBuilders() {
        final Map<DatePattern.Order, BiConsumer<StringBuilder, String[]>> result = new EnumMap<>(DatePattern.Order.class);
        result.put(DatePattern.Order.DAY_MONTH_YEAR, (builder, strings) -> builder.append(strings[2]).append(strings[1]).append(strings[0]));
        result.put(DatePattern.Order.MONTH_DAY_YEAR, (builder, strings) -> builder.append(strings[1]).append(strings[2]).append(strings[0]));
        result.put(DatePattern.Order.YEAR_MONTH_DAY, (builder, strings) -> builder.append(strings[0]).append(strings[1]).append(strings[2]));
        return result;
    }

    /**
     * Returns the pattern descriptor string expected by the client-side code.
     * The descriptor starts with a separator character, followed (in whatever order is decided) by
     * {@code 0d} or {@code _d} for (zero prefixed) day, {@code 0M} or {@code _M} for (zero prefixed) month
     * or {@code MM} for month name,
     * {@code 0y} or {@code _y} for short or full year. If short year is used, the resulting string ends
     * with {@code +} or {@code -} depending on {@link DatePattern#isPreviousCenturyBelowBoundary()}, followed by
     * two digits of the {@link DatePattern#getBaseCentury()} and two digits of {@link DatePattern#getCenturyBoundaryYear()}.
     *
     * @return Current representation of this object.
     */
    private static String convertDatePatternToClientPattern(DatePattern pattern) {
        if(pattern == null)
            return null;
        else {
            final String dayPart = pattern.isZeroPrefixedDay() ? "0d" : "_d";
            final String monthPart = MONTH_DISPLAY_PATTERNS.get(pattern.getMonthDisplayMode());
            final String yearPart = pattern.isShortYear() ? "0y" : "_y";

            StringBuilder builder = new StringBuilder();
            if(pattern.hasSeparator())
                builder.append(pattern.getSeparator());

            ORDER_BUILDERS.get(pattern.getDisplayOrder()).accept(builder, new String[]{yearPart, monthPart, dayPart});

            if (pattern.isShortYear() || pattern.isShortYearAlwaysAccepted()) {
                builder.append(pattern.isPreviousCenturyBelowBoundary() ? '+' : '-');
                builder.append(String.format("%02d", pattern.getBaseCentury() % 100));
                builder.append(String.format("%02d", pattern.getCenturyBoundaryYear() % 100));
            }

            return builder.toString();
        }
    }

    private final C source;

    /**
     * Creates a helper for a given source object.
     * The client-side representation should have mixed in methods from {@code date-pattern-mixin.js}.
     * @param source Source to use.
     */
    DatePatternDelegate(C source) {
        this.source = source;
        this.source.addAttachListener(this::onAttached);
    }

    /**
     * Class client-side method {@code initPatternSetting()} that, well, inits pattern setting.
     * In general, client-side code overrides a few methods to make sure pattern displaying and parsing works properly with custom patterns.
     */
    void initPatternSetting() {
        this.source.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this.source, context ->
                this.source.getElement().callJsFunction("initPatternSetting", this.source)
        ));
    }

    /**
     * Callback when onAttach() is called.
     * @param event An {@link AttachEvent}.
     */
    void onAttached(AttachEvent event) {
        this.initPatternSetting();
        this.updateClientSidePattern();
    }

    /**
     * Does magic and sets the display pattern on the client side.
     * Requires the client-side connector to have a {@code setDisplayPattern} method.
     */
    void updateClientSidePattern() {
        this.source.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this.source, context ->
                this.source.getElement().callJsFunction(
                        "setDisplayPattern",
                        this.source.getElement(), convertDatePatternToClientPattern(this.source.getDatePattern())
                )
        ));
    }

    private int[] getDayMonthYearPositions(DatePattern.Order order) {
        if(order == DatePattern.Order.DAY_MONTH_YEAR)
            return new int[]{0, 1, 2};
        else if(order == DatePattern.Order.MONTH_DAY_YEAR)
            return new int[]{1, 0, 2};
        else return new int[]{2, 1, 0};
    }

    /**
     * Formats the date according to the pattern present in the source.
     * @param date Date to format. Must not be {@code null}.
     * @return Formatted date.
     */
    String formatDate(LocalDate date) {
        final DatePattern pattern = this.source.getDatePattern();
        final String[] parts = new String[3];
        final int[] indices = this.getDayMonthYearPositions(pattern.getDisplayOrder());
        parts[indices[0]] = pattern.isZeroPrefixedDay() ? String.format("%02d", date.getDayOfMonth()) : String.valueOf(date.getDayOfMonth());
        switch (pattern.getMonthDisplayMode()) {
            case ZERO_PREFIXED_NUMBER:
                parts[indices[1]] = String.format("%02d", date.getMonthValue());
                break;
            case NAME:
                parts[indices[1]] = this.source.getSuperDatePickerI18n().getDisplayMonthNames().get(date.getMonthValue() - 1);
                break;
            default:
                parts[indices[1]] = String.valueOf(date.getMonthValue());
        }
        parts[indices[2]] = pattern.isShortYear() ? String.format("%02d", date.getYear() % 100) : String.valueOf(date.getYear());
        return String.join(String.valueOf(pattern.getSeparator()), parts);
    }

}
