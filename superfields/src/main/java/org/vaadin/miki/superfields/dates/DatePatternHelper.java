package org.vaadin.miki.superfields.dates;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Helper class that calls a method on a client side.
 * Internal use only.
 *
 * @author miki
 * @since 2020-05-02
 */
final class DatePatternHelper<C extends Component & HasDatePattern> implements Serializable {

    /**
     * Pattern keyword meaning that the client-side code must call server-side code to do formatting and parsing.
     */
    public static final String SERVER_SIDE_FORMATTING_REQUIRED = "$server";

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

    private static String getClientPattern(DatePattern pattern) {
        if(pattern instanceof ClientSideSupportedDatePattern)
            return ((ClientSideSupportedDatePattern) pattern).getClientSidePattern();
        else if(pattern == null)
            return null;
        else return SERVER_SIDE_FORMATTING_REQUIRED;
    }

    /**
     * Does magic and sets the display pattern on the client side.
     * Requires the client-side connector to have a {@code setDisplayPattern} method.
     */
    protected void updateClientSidePattern() {
        this.source.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this.source, context ->
                this.source.getElement().callJsFunction(
                        "setDisplayPattern",
                        this.source.getElement(),
                        getClientPattern(this.source.getDatePattern())
                )
        ));
    }

    /**
     * Formats the date.
     * @param year Year of the date.
     * @param month Month number, with {@code 1} being January and {@code 12} being December.
     * @param day Day number.
     * @return A string with the date formatted.
     * @throws IllegalStateException when {@link HasDatePattern#getDatePattern()} is {@code null}.
     */
    protected String formatDate(int year, int month, int day) {
        if(this.source.getDatePattern() == null)
            throw new IllegalStateException("formatDate() called when there is no date pattern set");
        return this.source.getDatePattern().formatDate(LocalDate.of(year, month, day));
    }

    /**
     * Parses the date.
     * @param formattedDate The date in whatever format was output by {@link #formatDate(int, int, int)} or entered by the user.
     * @return String formatted as {@code YYYY-MM-DD}, using exactly four digits for year, exactly two digits for month ({@code 1} for January, {@code 12} for December) and exactly two digits for day; or an empty string if parsing resulted in an error.
     * @throws IllegalStateException when {@link HasDatePattern#getDatePattern()} is {@code null}.
     */
    protected String parseDate(String formattedDate) {
        if(this.source.getDatePattern() == null)
            throw new IllegalStateException("parseDate() called when there is no date pattern set");
        final LocalDate result = this.source.getDatePattern().parseDate(formattedDate);
        if(result == null)
            return "";
        else return String.format("%04d-%02d-%02d", result.getYear(), result.getMonthValue(), result.getDayOfMonth());
    }

}
