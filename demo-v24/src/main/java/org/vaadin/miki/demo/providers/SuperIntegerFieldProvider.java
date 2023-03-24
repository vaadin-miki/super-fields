package org.vaadin.miki.demo.providers;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.numbers.SuperIntegerField;

/**
 * Provider for {@link SuperIntegerField}.
 * @author miki
 * @since 2020-11-17
 */
@Order(10)
public class SuperIntegerFieldProvider implements ComponentProvider<SuperIntegerField>, Validator<Integer> {

    @Override
    public SuperIntegerField getComponent() {
        return new SuperIntegerField(0, "Integer:").withMaximumIntegerDigits(6).withHelperText("(6 digits; default value is 0)");
    }

    @Override
    public ValidationResult apply(Integer integer, ValueContext valueContext) {
        return integer != null && integer % 2 == 0 ? ValidationResult.ok() : ValidationResult.error("only even values are allowed");
    }
}
