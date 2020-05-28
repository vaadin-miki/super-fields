package org.vaadin.miki.superfields.dates;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClientCallable;
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

    /**
     * Formats the date.
     * @param year Year of the date.
     * @param month Month number, with {@code 1} being January and {@code 12} being December.
     * @param day Day number.
     * @return A string with the date formatted.
     * @throws IllegalStateException when {@link #getDatePattern()} is {@code null}.
     */
    @ClientCallable
    protected String formatDate(int year, int month, int day) {
        if(this.getDatePattern() == null)
            throw new IllegalStateException("formatDate() called when there is no date pattern set");
        return this.getDatePattern().formatDate(LocalDate.of(year, month, day));
    }

    /**
     * Parses the date.
     * @param formattedDate The date in whatever format was output by {@link #formatDate(int, int, int)} or entered by the user.
     * @return String formatted as {@code YYYY-MM-DD}, using exactly four digits for year, exactly two digits for month ({@code 1} for January, {@code 12} for December) and exactly two digits for day; or an empty string if parsing resulted in an error.
     * @throws IllegalStateException when {@link #getDatePattern()} is {@code null}.
     */
    @ClientCallable
    protected String parseDate(String formattedDate) {
        if(this.getDatePattern() == null)
            throw new IllegalStateException("parseDate() called when there is no date pattern set");
        final LocalDate result = this.getDatePattern().parseDate(formattedDate);
        if(result == null)
            return "";
        else return String.format("%04d-%02d-%02d", result.getYear(), result.getMonthValue(), result.getDayOfMonth());
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
