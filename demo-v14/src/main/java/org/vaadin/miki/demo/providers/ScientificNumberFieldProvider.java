package org.vaadin.miki.demo.providers;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.numbers.ScientificNumberField;

import java.math.BigDecimal;
import java.util.Locale;

@Order(45)
public class ScientificNumberFieldProvider implements ComponentProvider<ScientificNumberField>, Validator<BigDecimal> {

    @Override
    public ScientificNumberField getComponent() {
        return new ScientificNumberField(Locale.getDefault());
    }

    @Override
    public ValidationResult apply(BigDecimal bigDecimal, ValueContext valueContext) {
        return bigDecimal.compareTo(BigDecimal.ONE) > 0 ? ValidationResult.ok() : ValidationResult.error("value must be greater than 1");
    }
}
