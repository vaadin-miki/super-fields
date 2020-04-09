package org.vaadin.miki.superfields.dates;

import com.vaadin.flow.component.datepicker.DatePicker;
import org.vaadin.miki.markers.HasLocale;

import java.time.LocalDate;
import java.util.Locale;

/**
 * An extension of {@link DatePicker} that handles I18N also on the client side.
 * @author miki
 * @since 2020-04-09
 */
public class SuperDatePicker extends DatePicker implements HasLocale {

    public SuperDatePicker() {
        this(Locale.getDefault());
    }

    public SuperDatePicker(Locale locale) {
        super();
        this.setLocale(locale);
    }

    public SuperDatePicker(LocalDate initialDate) {
        super(initialDate);
        this.setLocale(Locale.getDefault());
    }

    public SuperDatePicker(String label) {
        super(label);
        this.setLocale(Locale.getDefault());
    }

    public SuperDatePicker(String label, LocalDate initialDate) {
        super(label, initialDate);
        this.setLocale(Locale.getDefault());
    }

    public SuperDatePicker(ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
        super(listener);
        this.setLocale(Locale.getDefault());
    }

    public SuperDatePicker(String label, ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
        super(label, listener);
        this.setLocale(Locale.getDefault());
    }

    public SuperDatePicker(LocalDate initialDate, ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
        super(initialDate, listener);
        this.setLocale(Locale.getDefault());
    }

    public SuperDatePicker(String label, LocalDate initialDate, ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
        super(label, initialDate, listener);
        this.setLocale(Locale.getDefault());
    }

    public SuperDatePicker(LocalDate initialDate, Locale locale) {
        super(initialDate, locale);
    }

    @Override
    public final void setLocale(Locale locale) {
        super.setLocale(locale);
        DatePickerI18n i18n = this.getI18n();
        // new i18n object must be set, because the method that sends data to the client side is private and cannot be called directly
        if(i18n == null || (i18n instanceof SuperDatePickerI18n && !((SuperDatePickerI18n) i18n).getLocale().equals(locale)))
            this.setI18n(new SuperDatePickerI18n(locale));
    }
}
