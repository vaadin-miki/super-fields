package org.vaadin.miki.demo.providers;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.dates.SuperDatePicker;
import org.vaadin.miki.superfields.numbers.SuperIntegerField;
import org.vaadin.miki.superfields.text.LabelField;
import org.vaadin.miki.superfields.text.SuperTextField;
import org.vaadin.miki.superfields.variant.TypedFieldProvider;
import org.vaadin.miki.superfields.variant.VariantField;

import java.time.LocalDate;

/**
 * Provides a {@link VariantField}.
 *
 * @author miki
 * @since 2022-04-11
 */
@Order(85)
public class VariantFieldProvider implements ComponentProvider<VariantField>, Validator<Object> {

    @Override
    public ValidationResult apply(Object o, ValueContext valueContext) {
        return o != null && o.toString().length() > 5 ? ValidationResult.ok() : ValidationResult.error("(toString() on the value must be longer than 5 characters)");
    }

    @Override
    public VariantField getComponent() {
        return new VariantField()
                .withTypedFieldProvider(TypedFieldProvider.of(Integer.class, SuperIntegerField::new),
                        TypedFieldProvider.of(String.class, SuperTextField::new),
                        TypedFieldProvider.of(LocalDate.class, SuperDatePicker::new))
                .withNullComponentProvider(() -> new LabelField<>().withNullRepresentation("(no value specified)"))
                .withHelperText("(this is a CustomField<Object>)")
                .withLabel("Choose a value type below, then edit it here:");
    }
}
