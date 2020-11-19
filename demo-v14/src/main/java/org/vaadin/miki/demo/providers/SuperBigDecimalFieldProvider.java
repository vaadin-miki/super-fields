package org.vaadin.miki.demo.providers;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.superfields.numbers.SuperBigDecimalField;

import java.math.BigDecimal;

/**
 * Provider for {@link SuperBigDecimalField}.
 * @author miki
 * @since 2020-11-17
 */
public class SuperBigDecimalFieldProvider implements ComponentProvider<SuperBigDecimalField>, Validator<BigDecimal> {

    @Override
    public SuperBigDecimalField getComponent() {
        return new SuperBigDecimalField(null, "Big decimal:").withMaximumIntegerDigits(12).withMaximumFractionDigits(3).withMinimumFractionDigits(1).withId("big-decimal").withHelperText("(12 + 3 digits)");
    }

    @Override
    public ValidationResult apply(BigDecimal bigDecimal, ValueContext valueContext) {
        return bigDecimal.longValue() % 2 == 0 ? ValidationResult.ok() : ValidationResult.error("only values with even integer part are allowed");
    }
}
