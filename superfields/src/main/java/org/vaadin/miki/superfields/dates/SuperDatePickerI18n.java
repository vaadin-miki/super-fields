package org.vaadin.miki.superfields.dates;

import com.vaadin.flow.component.datepicker.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.miki.markers.HasLocale;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A locale-powered {@link com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n}.
 * @author miki
 * @since 2020-04-09
 */
// this is a workaround for https://github.com/vaadin/vaadin-date-time-picker-flow/issues/26
public final class SuperDatePickerI18n extends DatePicker.DatePickerI18n implements HasLocale {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuperDatePickerI18n.class);

    private static final String RESOURCE_BUNDLE_NAME = SuperDatePickerI18n.class.getSimpleName().toLowerCase();

    private final Map<String, Function<String, DatePicker.DatePickerI18n>> keysToStringMethods = new HashMap<>();

    private final Map<String, Function<List<String>, DatePicker.DatePickerI18n>> keysToListStringMethods = new HashMap<>();

    private final List<String> displayMonthNames = new ArrayList<>();

    private Locale locale;

    /**
     * Creates the i18n data based on default {@link Locale}.
     */
    public SuperDatePickerI18n() {
        this(Locale.getDefault());
    }

    /**
     * Creates the i18n data based on given {@link Locale}.
     * @param locale Locale to use.
     */
    public SuperDatePickerI18n(Locale locale) {
        this.keysToStringMethods.put("calendar", this::setCalendar);
        this.keysToStringMethods.put("cancel", this::setCancel);
        this.keysToStringMethods.put("clear", this::setClear);
        this.keysToStringMethods.put("today", this::setToday);
        this.keysToStringMethods.put("week", this::setWeek);
        this.keysToListStringMethods.put("month-names", this::setMonthNames);
        this.keysToListStringMethods.put("weekdays", this::setWeekdays);
        this.keysToListStringMethods.put("weekdays-short", this::setWeekdaysShort);
        this.keysToListStringMethods.put("display-month-names", this::setDisplayMonthNames);
        // finally, set locale
        this.setLocale(locale);
    }

    /**
     * Controls the month names used for displaying and typing in by the user.
     * @param displayMonthNames Names of months. If empty or {@code null}, {@link #getMonthNames()} will be used.
     * @return This.
     */
    public DatePicker.DatePickerI18n setDisplayMonthNames(List<String> displayMonthNames) {
        this.displayMonthNames.clear();
        this.displayMonthNames.addAll(displayMonthNames == null || displayMonthNames.isEmpty() ?
                this.getMonthNames() : displayMonthNames);
        return this;
    }

    /**
     * Returns current month names for displaying as user formatted month name.
     * @return A list of 12 month names (from January to December).
     */
    public List<String> getDisplayMonthNames() {
        return Collections.unmodifiableList(this.displayMonthNames.isEmpty() ? this.getMonthNames() : this.displayMonthNames);
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale == null ? Locale.getDefault() : locale;
        DateFormatSymbols symbols = new DateFormatSymbols(locale);
        this.setMonthNames(Arrays.asList(symbols.getMonths()).subList(0, 12));
        this.setDisplayMonthNames(Arrays.asList(symbols.getMonths()).subList(0, 12));
        this.setFirstDayOfWeek(Calendar.getInstance(this.locale).getFirstDayOfWeek() == Calendar.MONDAY ? 1 : 0);
        this.setWeekdays(Arrays.stream(symbols.getWeekdays()).filter(s -> !s.isEmpty()).collect(Collectors.toList()));
        this.setWeekdaysShort(Arrays.stream(symbols.getShortWeekdays()).filter(s -> !s.isEmpty()).collect(Collectors.toList()));

        try {
            final ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, this.locale);

            // in the worst case, language must match - if it does not, ignore
            if(!Objects.equals(bundle.getLocale().getLanguage(), this.locale.getLanguage()))
                throw new MissingResourceException(RESOURCE_BUNDLE_NAME, this.getClass().getName(), null);

            final Set<String> bundleKeys = bundle.keySet();

            // filter out those required keys that are present
            this.keysToStringMethods.entrySet().stream()
                    .filter(entry -> bundleKeys.contains(entry.getKey()))
                    .forEach(entry -> entry.getValue().apply(bundle.getString(entry.getKey())));
            this.keysToListStringMethods.entrySet().stream()
                    .filter(entry -> bundleKeys.contains(entry.getKey()))
                    .forEach(entry -> entry.getValue().apply(Arrays.asList(bundle.getString(entry.getKey()).split("\\s*,\\s*"))));
            if(bundleKeys.contains("first-day-of-week"))
                this.setFirstDayOfWeek(Integer.parseInt(bundle.getString("first-day-of-week")));
            LOGGER.info("resource overwritten properties: {}", bundleKeys);
        }
        catch(MissingResourceException mre) {
            LOGGER.warn("resource bundle {} for locale {} not found, some texts may display incorrectly or not at all", RESOURCE_BUNDLE_NAME, locale);
            // do nothing, no resource - no text to display
        }
    }

    @Override
    public Locale getLocale() {
        return this.locale;
    }
}
