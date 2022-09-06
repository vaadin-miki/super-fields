package org.vaadin.miki.superfields.variant;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import org.vaadin.miki.superfields.collections.CollectionController;
import org.vaadin.miki.superfields.collections.CollectionField;
import org.vaadin.miki.superfields.collections.CollectionValueComponentProvider;
import org.vaadin.miki.superfields.dates.SuperDatePicker;
import org.vaadin.miki.superfields.dates.SuperDateTimePicker;
import org.vaadin.miki.superfields.numbers.SuperBigDecimalField;
import org.vaadin.miki.superfields.numbers.SuperDoubleField;
import org.vaadin.miki.superfields.numbers.SuperIntegerField;
import org.vaadin.miki.superfields.numbers.SuperLongField;
import org.vaadin.miki.superfields.text.SuperTextArea;
import org.vaadin.miki.superfields.text.SuperTextField;
import org.vaadin.miki.superfields.variant.builder.SimplePropertyComponentFactory;
import org.vaadin.miki.superfields.variant.reflect.AnnotationMetadataProvider;
import org.vaadin.miki.superfields.variant.reflect.ReflectiveDefinitionProvider;
import org.vaadin.miki.superfields.variant.util.MetadataBasedGroupingProvider;
import org.vaadin.miki.util.ReflectTools;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
