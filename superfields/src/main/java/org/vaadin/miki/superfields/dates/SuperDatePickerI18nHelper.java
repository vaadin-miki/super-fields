package org.vaadin.miki.superfields.dates;

import com.vaadin.flow.component.datepicker.DatePicker;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A helper class related to setting locale and super-I18N objects.
 * Internal use only.
 *
 * @author miki
 * @since 2020-04-10
 */
final class SuperDatePickerI18nHelper {

    private SuperDatePickerI18nHelper() {
        // no instances allowed
    }

    /**
     * Does the check and updates the {@link DatePicker.DatePickerI18n}.
     * @param locale Locale to set.
     * @param getter Getter for i18n data.
     * @param setter Setter for i18n data.
     */
    static void updateI18N(Locale locale, Supplier<DatePicker.DatePickerI18n> getter, Consumer<DatePicker.DatePickerI18n> setter) {
        DatePicker.DatePickerI18n i18n = getter.get();
        // new i18n object must be set, because the method that sends data to the client side is private and cannot be called directly
        if(i18n == null || (i18n instanceof SuperDatePickerI18n sdp && !(sdp.getLocale().equals(locale))))
            setter.accept(new SuperDatePickerI18n(locale));

    }

    /**
     * Helper method to process a property under a certain condition.
     * @param getter Method to get a value.
     * @param condition Condition on the value.
     * @param setter Setter to invoke when the condition is {@code true}.
     * @param <T> Type of value.
     */
    private static <T> void processProperty(Supplier<T> getter, Predicate<T> condition, Function<T, DatePicker.DatePickerI18n> setter) {
        final T value = getter.get();
        if(condition.test(value))
            setter.apply(value);
    }

    /**
     * Builds an instance of {@link SuperDatePickerI18n} from given {@link DatePicker.DatePickerI18n}.
     * @param i18n Simplified version to use.
     * @param locale A {@link Locale} to use.
     * @return A super version, with all defined (non-empty) properties copied from {@code i18n}.
     */
    public static SuperDatePickerI18n from(DatePicker.DatePickerI18n i18n, Locale locale) {
        SuperDatePickerI18n result = new SuperDatePickerI18n(locale);

        final Predicate<String> stringCheck = s -> s != null && !s.isEmpty();
        final Predicate<List<String>> listCheck = list -> list != null && !list.isEmpty();

        processProperty(i18n::getCancel, stringCheck, result::setCancel);
        processProperty(i18n::getToday, stringCheck, result::setToday);
        processProperty(i18n::getMonthNames, listCheck, result::setMonthNames);
        processProperty(i18n::getWeekdays, listCheck, result::setWeekdays);
        processProperty(i18n::getWeekdaysShort, listCheck, result::setWeekdaysShort);
        processProperty(i18n::getMonthNames, listCheck, result::setDisplayMonthNames);
        processProperty(i18n::getFirstDayOfWeek, x -> x > -1, result::setFirstDayOfWeek);

        return null;
    }
}
