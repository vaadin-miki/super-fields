package org.vaadin.miki.superfields.dates;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;

/**
 * Helper class that calls a method on a client side.
 * Internal use only.
 *
 * @author miki
 * @since 2020-05-02
 */
final class DatePatternHelper<C extends Component & HasDatePattern> {

    /**
     * Returns the pattern descriptor string expected by the client-side code.
     * The descriptor starts with a separator character, followed (in whatever order is decided) by
     * {@code 0d} or {@code _d} for (zero prefixed) day, {@code 0M} or {@code _M} for (zero prefixed) month,
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
            final String monthPart = pattern.isZeroPrefixedMonth() ? "0M" : "_M";
            final String yearPart = pattern.isShortYear() ? "0y" : "_y";

            StringBuilder builder = new StringBuilder();
            builder.append(pattern.getSeparator());
            switch (pattern.getDisplayOrder()) {
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
            if (pattern.isShortYear()) {
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
    DatePatternHelper(C source) {
        this.source = source;
        this.source.addAttachListener(this::onAttached);
    }

    /**
     * Class client-side method {@code initPatternSetting()} that, well, inits pattern setting.
     * In general, client-side code overrides a few methods to make sure pattern displaying and parsing works properly with custom patterns.
     */
    protected void initPatternSetting() {
        this.source.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this.source, context ->
                this.source.getElement().callJsFunction("initPatternSetting", this.source)
        ));
    }

    /**
     * Callback when onAttach() is called.
     * @param event An {@link AttachEvent}.
     */
    protected void onAttached(AttachEvent event) {
        this.initPatternSetting();
        this.updateClientSidePattern();
    }

    /**
     * Does magic and sets the display pattern on the client side.
     * Requires the client-side connector to have a {@code setDisplayPattern} method.
     */
    protected void updateClientSidePattern() {
        this.source.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this.source, context ->
                this.source.getElement().callJsFunction(
                        "setDisplayPattern",
                        this.source.getElement(), convertDatePatternToClientPattern(this.source.getDatePattern())
                )
        ));
    }

}
