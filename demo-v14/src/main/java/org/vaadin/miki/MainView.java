package org.vaadin.miki;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.router.Route;
import org.vaadin.miki.markers.HasLocale;
import org.vaadin.miki.superfields.dates.SuperDatePicker;
import org.vaadin.miki.superfields.dates.SuperDateTimePicker;
import org.vaadin.miki.superfields.numbers.AbstractSuperNumberField;
import org.vaadin.miki.superfields.numbers.SuperBigDecimalField;
import org.vaadin.miki.superfields.numbers.SuperDoubleField;
import org.vaadin.miki.superfields.numbers.SuperIntegerField;
import org.vaadin.miki.superfields.numbers.SuperLongField;
import org.vaadin.miki.superfields.tabs.SuperTabs;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Demo app for various SuperFields and other components.
 * @author miki
 * @since 2020-04-07
 */
@Route
public class MainView extends VerticalLayout {

    private final Map<Class<?>, Component> components = new LinkedHashMap<>();

    private Component buildContentsFor(Class<?> type) {
        return this.components.get(type);
    }

    public MainView() {
        this.components.put(SuperIntegerField.class, new SuperIntegerField("Integer (6 digits):"));

        final SuperTabs<Class<?>> tabs = new SuperTabs<>(
                type -> new Tab(type.getSimpleName()),
                this::buildContentsFor,
                this.components.keySet().toArray(new Class<?>[0])
        );

        this.add(tabs);

        final SuperDoubleField doubleField = new SuperDoubleField("Double (8 + 4 digits):");
        doubleField.setMaximumIntegerDigits(8);
        doubleField.setMaximumFractionDigits(4);
        doubleField.addValueChangeListener(event -> Notification.show("Double value changed: "+event.getValue()));

        final SuperIntegerField integerField = new SuperIntegerField("Integer (6 digits):");
        integerField.setMaximumIntegerDigits(6);
        integerField.addValueChangeListener(event -> Notification.show("Integer value changed: "+event.getValue()));

        final SuperLongField longField = new SuperLongField("Long (11 digits):");
        longField.setMaximumIntegerDigits(11);
        longField.addValueChangeListener(event -> Notification.show("Long value changed: "+event.getValue()));

        final SuperBigDecimalField bigDecimalField = new SuperBigDecimalField("Big decimal (12 + 3 digits):");
        bigDecimalField.setMaximumIntegerDigits(12);
        bigDecimalField.setMaximumFractionDigits(3);
        bigDecimalField.addValueChangeListener(event -> Notification.show("Big decimal value changed: "+event.getValue()));

        final SuperDatePicker datePicker = new SuperDatePicker("Pick a date:");
        datePicker.addValueChangeListener(event -> Notification.show("Date changed: "+event.getValue()));

        final SuperDateTimePicker dateTimePicker = new SuperDateTimePicker("Pick time and date:");
        dateTimePicker.addValueChangeListener(event -> Notification.show("Date time changed: "+event.getValue()));

        final List<AbstractSuperNumberField<?>> numberFields = Arrays.asList(doubleField, bigDecimalField, integerField, longField);
        final List<HasLocale> localeFields = Arrays.asList(doubleField, bigDecimalField, integerField, longField, datePicker, dateTimePicker);

        final Checkbox autoselect = new Checkbox("Select automatically on focus?");
        autoselect.addValueChangeListener(event -> numberFields.forEach(f -> f.setAutoselect(event.getValue())));

        final Checkbox separatorHidden = new Checkbox("Hide grouping separator on focus?");
        separatorHidden.addValueChangeListener(event -> numberFields.forEach(f -> f.setGroupingSeparatorHiddenOnFocus(event.getValue())));

        final Checkbox prefix = new Checkbox("Show prefix component?");
        prefix.addValueChangeListener(event -> numberFields.forEach(f -> f.setPrefixComponent(
                event.getValue() ? new Span(">") : null
        )));

        final Checkbox suffix = new Checkbox("Show suffix component?");
        suffix.addValueChangeListener(event -> numberFields.forEach(f -> f.setSuffixComponent(
                event.getValue() ? new Span("€") : null
        )));

        final Checkbox alignRight = new Checkbox("Align text to the right?");
        alignRight.addValueChangeListener(event -> numberFields.forEach(f -> {
            if(event.getValue())
                            f.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
            else
                            f.removeThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
                }
        ));

        final ComboBox<Locale> locales = new ComboBox<>("Select locale:", new Locale("pl", "PL"), Locale.UK, Locale.FRANCE, Locale.GERMANY, Locale.CHINA);
        locales.setItemLabelGenerator(locale -> locale.getDisplayCountry() + " / "+locale.getDisplayLanguage());
        locales.setAllowCustomValue(false);
        locales.addValueChangeListener(event -> {
            localeFields.forEach(f -> f.setLocale(event.getValue()));
            // changing locale resets fraction and integer digits, so they need to be set again
            doubleField.setMaximumFractionDigits(4);
            doubleField.setMaximumIntegerDigits(8);
            integerField.setMaximumIntegerDigits(6);
            bigDecimalField.setMaximumIntegerDigits(12);
            bigDecimalField.setMaximumFractionDigits(3);
            longField.setMaximumIntegerDigits(11);
        });

        this.add(autoselect, separatorHidden, prefix, suffix, alignRight, locales, doubleField, bigDecimalField, integerField, longField, datePicker, dateTimePicker);
    }
}
