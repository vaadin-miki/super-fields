package org.vaadin.miki.demo.providers;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.numbers.SuperDoubleField;

/**
 * Provider for {@link SuperDoubleField}.
 * @author miki
 * @since 2020-11-17
 */
@Order(30)
public class SuperDoubleFieldProvider implements ComponentProvider<SuperDoubleField>, Validator<Double> {
    @Override
    public SuperDoubleField getComponent() {
        return new SuperDoubleField(null, "Double:").withMaximumIntegerDigits(8).withMaximumFractionDigits(4).withHelperText("(8 + 4 digits)");
    }

    @Override
    public ValidationResult apply(Double number, ValueContext valueContext) {
        return number != null && number > 50 ? ValidationResult.ok() : ValidationResult.error("only even above 50 are accepted");
    }
}
