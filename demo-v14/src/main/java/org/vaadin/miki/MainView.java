package org.vaadin.miki;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.router.Route;
import org.vaadin.miki.superfields.SuperDoubleField;

import java.util.Locale;

/**
 * Demo app for various SuperFields.
 * @author miki
 * @since 2020-04-07
 */
@Route
public class MainView extends VerticalLayout {

    public MainView() {
        final SuperDoubleField doubleField = new SuperDoubleField("Type a number (8 + 4 digits):");
        doubleField.setMaximumIntegerDigits(8);
        doubleField.setMaximumFractionDigits(4);
        doubleField.addValueChangeListener(event -> Notification.show("Value changed: "+event.getValue()));

        final Checkbox autoselect = new Checkbox("Select automatically on focus?");
        autoselect.addValueChangeListener(event -> doubleField.setAutoselect(event.getValue()));

        final Checkbox separatorHidden = new Checkbox("Hide grouping separator on focus?");
        separatorHidden.addValueChangeListener(event -> doubleField.setGroupingSeparatorHiddenOnFocus(event.getValue()));

        final Checkbox prefix = new Checkbox("Show prefix component?");
        prefix.addValueChangeListener(event -> doubleField.setPrefixComponent(
                event.getValue() ? new Span(">") : null
        ));

        final Checkbox suffix = new Checkbox("Show suffix component?");
        suffix.addValueChangeListener(event -> doubleField.setSuffixComponent(
                event.getValue() ? new Span("€") : null
        ));

        final Checkbox alignRight = new Checkbox("Align text to the right?");
        alignRight.addValueChangeListener(event -> {
            if(event.getValue())
                            doubleField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
            else
                            doubleField.removeThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
                }
        );

        final ComboBox<Locale> locales = new ComboBox<>("Select locale:", new Locale("pl", "PL"), Locale.UK, Locale.FRANCE, Locale.GERMANY, Locale.CHINA);
        locales.setItemLabelGenerator(locale -> locale.getDisplayCountry() + " / "+locale.getDisplayLanguage());
        locales.setAllowCustomValue(false);
        locales.addValueChangeListener(event -> {
            doubleField.setLocale(event.getValue());
            // changing locale resets fraction and integer digits, so they need to be set again
            doubleField.setMaximumFractionDigits(4);
            doubleField.setMaximumIntegerDigits(8);
        });

        this.add(autoselect, separatorHidden, prefix, suffix, alignRight, locales, doubleField);
    }
}
