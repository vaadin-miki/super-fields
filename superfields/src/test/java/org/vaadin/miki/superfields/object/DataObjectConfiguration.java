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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DataObjectConfiguration {

    public static final Map<String, Class<? extends Component>> EXPECTED_FIELDS = buildExpectedFields();

    public static final Map<String, String> EXPECTED_CAPTIONS = buildExpectedCaptions();

    private static Map<String, Class<? extends Component>> buildExpectedFields() {
        final Map<String, Class<? extends Component>> result = new HashMap<>();
        result.put("text", SuperTextField.class);
        result.put("description", SuperTextArea.class);
        result.put("check", Checkbox.class);
        result.put("currency", SuperBigDecimalField.class);
        result.put("timestamp", SuperDateTimePicker.class);
        result.put("date", SuperDatePicker.class);
        result.put("number", SuperIntegerField.class);
        result.put("fixed", SuperLongField.class);
        return Collections.unmodifiableMap(result);
    }

    private static Map<String, String> buildExpectedCaptions() {
        final Map<String, String> result = new HashMap<>();
        result.put("text", "Text");
        result.put("description", "Description");
        result.put("check", "Check");
        result.put("currency", "Currency");
        result.put("timestamp", "Date and time");
        result.put("date", "Date");
        result.put("number", "Amount");
        result.put("fixed", "Internal information");
        return Collections.unmodifiableMap(result);
    }

    private DataObjectConfiguration() {
        // no instances allowed
    }

}
