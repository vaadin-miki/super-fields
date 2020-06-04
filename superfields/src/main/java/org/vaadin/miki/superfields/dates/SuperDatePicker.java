package org.vaadin.miki.superfields.dates;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.shared.Registration;
import org.vaadin.miki.markers.HasDatePattern;
import org.vaadin.miki.markers.HasLabel;
import org.vaadin.miki.markers.HasLocale;
import org.vaadin.miki.markers.HasPlaceholder;
import org.vaadin.miki.markers.WithDatePatternMixin;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithLocaleMixin;
import org.vaadin.miki.markers.WithPlaceholderMixin;
import org.vaadin.miki.markers.WithValueMixin;
import org.vaadin.miki.markers.CanReceiveSelectionEventsFromClient;
import org.vaadin.miki.markers.CanSelectText;
import org.vaadin.miki.shared.text.TextSelectionDelegate;
import org.vaadin.miki.events.text.TextSelectionEvent;
import org.vaadin.miki.events.text.TextSelectionListener;
import org.vaadin.miki.events.text.TextSelectionNotifier;
import org.vaadin.miki.markers.WithReceivingSelectionEventsFromClientMixin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
                   CanSelectText, CanReceiveSelectionEventsFromClient, WithReceivingSelectionEventsFromClientMixin<SuperDatePicker>,
                   TextSelectionNotifier<SuperDatePicker>,
                   WithLocaleMixin<SuperDatePicker>, WithLabelMixin<SuperDatePicker>,
                   WithPlaceholderMixin<SuperDatePicker>, WithDatePatternMixin<SuperDatePicker>,
                   WithValueMixin<AbstractField.ComponentValueChangeEvent<DatePicker, LocalDate>, LocalDate, SuperDatePicker>,
                   WithIdMixin<SuperDatePicker> {

    private final DatePatternDelegate<SuperDatePicker> datePatternDelegate = new DatePatternDelegate<>(this);

    private final TextSelectionDelegate<SuperDatePicker> textSelectionDelegate = new TextSelectionDelegate<>(this);

    private boolean receivingClientSideSelectionEvent = false;

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
        if(this.datePatternDelegate != null) {
            this.datePatternDelegate.initPatternSetting();
            SuperDatePickerI18nHelper.updateI18N(locale, this::getI18n, this::setI18n);
        }
        super.setLocale(locale);
    }

    @Override
    public void setDatePattern(DatePattern datePattern) {
        this.datePattern = datePattern;
        this.datePatternDelegate.updateClientSidePattern();
    }

    @Override
    public DatePattern getDatePattern() {
        return this.datePattern;
    }

    @Override
    public void setValue(LocalDate value) {
        this.textSelectionDelegate.updateAttributeOnValueChange(this::getEventBus);
        super.setValue(value);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        this.textSelectionDelegate.informClientAboutSendingEvents(this.isReceivingSelectionEventsFromClient());
        super.onAttach(attachEvent);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // detaching means server should not be informed
        if(this.isReceivingSelectionEventsFromClient())
            this.textSelectionDelegate.informClientAboutSendingEvents(false);
        super.onDetach(detachEvent);
    }

    @ClientCallable
    private void selectionChanged(int start, int end, String selection) {
        TextSelectionEvent<SuperDatePicker> event = new TextSelectionEvent<>(this, true, start, end, selection);
        this.textSelectionDelegate.fireTextSelectionEvent(this.getEventBus(), event);
    }

    @Override
    public boolean isReceivingSelectionEventsFromClient() {
        return this.receivingClientSideSelectionEvent;
    }

    @Override
    public void setReceivingSelectionEventsFromClient(boolean receivingSelectionEventsFromClient) {
        this.receivingClientSideSelectionEvent = receivingSelectionEventsFromClient;
        this.textSelectionDelegate.informClientAboutSendingEvents(receivingSelectionEventsFromClient);
    }

    /**
     * Returns the current value formatted with current locale or pattern.
     * @return Current date, formatted. Will return {@code null} if the current date is {@code null}.
     */
    public String getFormattedValue() {
        final LocalDate value = this.getValue();
        if(value == null)
            return null;
        else if(this.getDatePattern() == null)
            return value.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(this.getLocale()));
        else
            return this.datePatternDelegate.formatDate(value);
    }

    @Override
    public void selectAll() {
        this.textSelectionDelegate.selectAll(this::getFormattedValue, this::getEventBus);
    }

    @Override
    public void selectNone() {
        this.textSelectionDelegate.selectNone(this::getEventBus);
    }

    @Override
    public void select(int from, int to) {
        this.textSelectionDelegate.select(this::getFormattedValue, this::getEventBus, from, to);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Registration addTextSelectionListener(TextSelectionListener<SuperDatePicker> listener) {
        return this.getEventBus().addListener((Class<TextSelectionEvent<SuperDatePicker>>)(Class<?>)TextSelectionEvent.class, listener);
    }
}
