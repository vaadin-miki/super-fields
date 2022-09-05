package org.vaadin.miki.superfields.variant;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import org.vaadin.miki.superfields.dates.SuperDatePicker;
import org.vaadin.miki.superfields.dates.SuperDateTimePicker;
import org.vaadin.miki.superfields.numbers.SuperBigDecimalField;
import org.vaadin.miki.superfields.numbers.SuperDoubleField;
import org.vaadin.miki.superfields.numbers.SuperIntegerField;
import org.vaadin.miki.superfields.numbers.SuperLongField;
import org.vaadin.miki.superfields.text.SuperTextArea;
import org.vaadin.miki.superfields.text.SuperTextField;
import org.vaadin.miki.superfields.variant.builder.SimplePropertyComponentFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DataObjectConfiguration {

    public static final String MULTILINE_METADATA_PROPERTY = "multiline";
    public static final String GROUP_METADATA_PROPERTY = "group";
    public static final String ORDER_METADATA_PROPERTY = "order";
    public static final String CAPTION_METADATA_PROPERTY = "caption";
    public static final String READ_ONLY_METADATA_PROPERTY = "read-only";

    private static final Set<Class<?>> EXPECTED_BOOLEAN_TYPES = Set.of(Boolean.class, boolean.class);

    public static final SimplePropertyComponentFactory SUPERFIELDS_DEFAULT_FACTORY = new SimplePropertyComponentFactory()
            .withRegisteredType(Boolean.class, Checkbox::new)
            .withRegisteredType(boolean.class, Checkbox::new)
            .withRegisteredType(Integer.class, SuperIntegerField::new)
            .withRegisteredType(int.class, SuperIntegerField::new)
            .withRegisteredType(Long.class, SuperLongField::new)
            .withRegisteredType(long.class, SuperLongField::new)
            .withRegisteredType(Double.class, SuperDoubleField::new)
            .withRegisteredType(double.class, SuperDoubleField::new)
            .withRegisteredType(BigDecimal.class, SuperBigDecimalField::new)
            .withRegisteredType(LocalDate.class, SuperDatePicker::new)
            .withRegisteredType(LocalDateTime.class, SuperDateTimePicker::new)
            .withRegisteredBuilder(String.class, def -> def.getMetadata().containsKey(MULTILINE_METADATA_PROPERTY)
                    && EXPECTED_BOOLEAN_TYPES.contains(def.getMetadata().get(MULTILINE_METADATA_PROPERTY).getValueType())
                    && Objects.equals(Boolean.TRUE, def.getMetadata().get(MULTILINE_METADATA_PROPERTY).getValue()) ?
                    new SuperTextArea() :
                    new SuperTextField())
            ;

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
