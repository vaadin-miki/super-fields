package org.vaadin.miki.superfields.dates;

import com.vaadin.flow.component.datepicker.DatePicker;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A helper class for setting locale and I18N.
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
        if(i18n == null || (i18n instanceof SuperDatePickerI18n && !((SuperDatePickerI18n) i18n).getLocale().equals(locale)))
            setter.accept(new SuperDatePickerI18n(locale));

    }

}
