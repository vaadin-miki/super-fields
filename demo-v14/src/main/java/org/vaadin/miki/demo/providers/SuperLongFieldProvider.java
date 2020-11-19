package org.vaadin.miki.demo.providers;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.superfields.numbers.SuperLongField;

/**
 * Provider for {@link SuperLongField}.
 * @author miki
 * @since 2020-11-17
 */
public class SuperLongFieldProvider implements ComponentProvider<SuperLongField>, Validator<Long> {

    @Override
    public SuperLongField getComponent() {
        return new SuperLongField(null, "Long:").withMaximumIntegerDigits(11).withId("long").withHelperText("(11 digits)");
    }

    @Override
    public ValidationResult apply(Long integer, ValueContext valueContext) {
        return integer != null && integer % 2 == 0 ? ValidationResult.ok() : ValidationResult.error("only even values are allowed");
    }
}
