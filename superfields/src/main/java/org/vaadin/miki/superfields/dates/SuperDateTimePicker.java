package org.vaadin.miki.superfields.dates;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import org.vaadin.miki.markers.HasLocale;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * An extension of {@link DateTimePicker} that handles I18N also on the client side.
 * @author miki
 * @since 2020-04-09
 */
public class SuperDateTimePicker extends DateTimePicker implements HasLocale {

    public SuperDateTimePicker() {
        this(Locale.getDefault());
    }

    public SuperDateTimePicker(Locale locale) {
        super();
        this.setLocale(locale);
    }

    public SuperDateTimePicker(String label) {
        super(label);
        this.setLocale(Locale.getDefault());
    }

    public SuperDateTimePicker(String label, LocalDateTime initialDateTime) {
        super(label, initialDateTime);
        this.setLocale(Locale.getDefault());
    }

    public SuperDateTimePicker(LocalDateTime initialDateTime) {
        super(initialDateTime);
        this.setLocale(Locale.getDefault());
    }

    public SuperDateTimePicker(ValueChangeListener<ComponentValueChangeEvent<DateTimePicker, LocalDateTime>> listener) {
        super(listener);
        this.setLocale(Locale.getDefault());
    }

    public SuperDateTimePicker(String label, ValueChangeListener<ComponentValueChangeEvent<DateTimePicker, LocalDateTime>> listener) {
        super(label, listener);
        this.setLocale(Locale.getDefault());
    }

    public SuperDateTimePicker(LocalDateTime initialDateTime, ValueChangeListener<ComponentValueChangeEvent<DateTimePicker, LocalDateTime>> listener) {
        super(initialDateTime, listener);
        this.setLocale(Locale.getDefault());
    }

    public SuperDateTimePicker(String label, LocalDateTime initialDateTime, ValueChangeListener<ComponentValueChangeEvent<DateTimePicker, LocalDateTime>> listener) {
        super(label, initialDateTime, listener);
        this.setLocale(Locale.getDefault());
    }

    public SuperDateTimePicker(LocalDateTime initialDateTime, Locale locale) {
        super(initialDateTime, locale);
    }

    @Override
    public void setLocale(Locale locale) {
        super.setLocale(locale);
        DatePicker.DatePickerI18n i18n = this.getDatePickerI18n();
        // new i18n object must be set, because the method that sends data to the client side is private and cannot be called directly
        if(i18n == null || (i18n instanceof SuperDatePickerI18n && !((SuperDatePickerI18n) i18n).getLocale().equals(locale)))
            this.setDatePickerI18n(new SuperDatePickerI18n(locale));
    }
}
