package org.vaadin.miki.superfields.object;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import org.vaadin.miki.superfields.dates.SuperDatePicker;
import org.vaadin.miki.superfields.dates.SuperDateTimePicker;
import org.vaadin.miki.superfields.numbers.SuperBigDecimalField;
import org.vaadin.miki.superfields.numbers.SuperIntegerField;
import org.vaadin.miki.superfields.numbers.SuperLongField;
import org.vaadin.miki.superfields.text.SuperTextArea;
import org.vaadin.miki.superfields.text.SuperTextField;

import java.util.Map;

public class DataObjectConfiguration {

    public static final Map<String, Class<? extends Component>> EXPECTED_FIELDS = Map.of(
            "text", SuperTextField.class,
            "description", SuperTextArea.class,
            "check", Checkbox.class,
            "currency", SuperBigDecimalField.class,
            "timestamp", SuperDateTimePicker.class,
            "date", SuperDatePicker.class,
            "number", SuperIntegerField.class,
            "fixed", SuperLongField.class
    );

    public static final Map<String, String> EXPECTED_CAPTIONS = Map.of(
            "text", "Text",
            "description", "Description",
            "check", "Check",
            "currency", "Currency",
            "timestamp", "Date and time",
            "date", "Date",
            "number", "Amount",
            "fixed", "Internal information"
    );

    private DataObjectConfiguration() {
        // no instances allowed
    }

}
