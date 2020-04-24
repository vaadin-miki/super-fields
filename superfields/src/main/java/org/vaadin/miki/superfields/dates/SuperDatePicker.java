package org.vaadin.miki.superfields.dates;

import com.vaadin.flow.component.datepicker.DatePicker;
import org.vaadin.miki.markers.HasLabel;
import org.vaadin.miki.markers.HasLocale;
import org.vaadin.miki.markers.HasPlaceholder;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithLocaleMixin;
import org.vaadin.miki.markers.WithPlaceholderMixin;

import java.time.LocalDate;
import java.util.Locale;

/**
 * An extension of {@link DatePicker} that handles I18N also on the client side.
 * @author miki
 * @since 2020-04-09
 */
public class SuperDatePicker extends DatePicker
        implements HasLocale, HasLabel, HasPlaceholder, HasDatePattern,
                   WithLocaleMixin<SuperDatePicker>, WithLabelMixin<SuperDatePicker>,
                   WithPlaceholderMixin<SuperDatePicker>, WithDatePatternMixin<SuperDatePicker> {

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
        super(initialDate, locale);
    }

    @Override
    public final void setLocale(Locale locale) {
        SuperDatePickerI18nHelper.updateI18N(locale, this::getI18n, this::setI18n);
        super.setLocale(locale);
    }

    @Override
    public void setDatePattern(DatePattern datePattern) {
        this.datePattern = datePattern;
        if(datePattern != null)
            this.getElement().executeJs("this.set('i18n.formatDate', date => {" +
                    DatePatternJsGenerator.buildFormatDateFunction(this.datePattern) +
                    "}); this.set('i18n.parseDate', text => {" +
                    DatePatternJsGenerator.buildParseDateFunction(this.datePattern) +
                    "});"
            );
        // this should in theory reset the pattern on the client side
        else this.setLocale(this.getLocale());
    }

    @Override
    public DatePattern getDatePattern() {
        return this.datePattern;
    }
}
