package org.vaadin.miki.superfields.dates;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import org.vaadin.miki.markers.HasLabel;
import org.vaadin.miki.markers.HasLocale;
import org.vaadin.miki.markers.HasPlaceholder;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithLocaleMixin;
import org.vaadin.miki.markers.WithPlaceholderMixin;
import org.vaadin.miki.markers.WithValueMixin;

import java.time.LocalDate;
import java.util.Locale;

/**
 * An extension of {@link DatePicker} that handles I18N also on the client side.
 * @author miki
 * @since 2020-04-09
 */
@JsModule("./super-date-picker.js")
@Tag("super-date-picker")
@SuppressWarnings("squid:S110") // there is no way to reduce the number of parent classes
public class SuperDatePicker extends DatePicker
        implements HasLocale, HasLabel, HasPlaceholder, HasDatePattern,
                   WithLocaleMixin<SuperDatePicker>, WithLabelMixin<SuperDatePicker>,
                   WithPlaceholderMixin<SuperDatePicker>, WithDatePatternMixin<SuperDatePicker>,
                   WithValueMixin<AbstractField.ComponentValueChangeEvent<DatePicker, LocalDate>, LocalDate, SuperDatePicker>,
                   WithIdMixin<SuperDatePicker> {

    private final DatePatternHelper<SuperDatePicker> delegate = new DatePatternHelper<>(this);

    private DatePattern datePattern;

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
        super(initialDate);
        this.setLocale(locale);
    }

    @Override
    public final void setLocale(Locale locale) {
        // there is a call for setting locale from the superclass' constructor
        // and when that happens, the field is not yet initialised
        if(this.delegate != null) {
            this.delegate.initPatternSetting();
            SuperDatePickerI18nHelper.updateI18N(locale, this::getI18n, this::setI18n);
        }
        super.setLocale(locale);
    }

    @Override
    public void setDatePattern(DatePattern datePattern) {
        this.datePattern = datePattern;
        this.delegate.updateClientSidePattern();
    }

    @Override
    public DatePattern getDatePattern() {
        return this.datePattern;
    }
}
