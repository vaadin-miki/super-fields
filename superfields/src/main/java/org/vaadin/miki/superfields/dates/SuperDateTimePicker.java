package org.vaadin.miki.superfields.dates;

import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dependency.JsModule;
import org.vaadin.miki.markers.HasLabel;
import org.vaadin.miki.markers.HasLocale;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithLocaleMixin;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * An extension of {@link DateTimePicker} that handles I18N also on the client side.
 * @author miki
 * @since 2020-04-09
 */
@JsModule("./super-date-picker-workaround.js")
public class SuperDateTimePicker extends DateTimePicker
        implements HasLocale, HasLabel, HasDatePattern,
                   WithLocaleMixin<SuperDateTimePicker>, WithLabelMixin<SuperDateTimePicker>, WithDatePatternMixin<SuperDateTimePicker> {

    private DatePattern datePattern;

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
        SuperDatePickerI18nHelper.updateI18N(locale, this::getDatePickerI18n, this::setDatePickerI18n);
        super.setLocale(locale);
    }

    @Override
    public void setDatePattern(DatePattern pattern) {
        this.datePattern = pattern;
        DatePatternHelper.setClientSidePattern("querySelector('vaadin-date-time-picker-date-picker').", this, pattern, this.getLocale(), this::setLocale);
    }

    @Override
    public DatePattern getDatePattern() {
        return this.datePattern;
    }
}
