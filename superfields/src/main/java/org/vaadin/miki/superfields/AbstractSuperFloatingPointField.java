package org.vaadin.miki.superfields;

import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.SerializablePredicate;

import java.util.Locale;

/**
 * Base class for implementations of {@link AbstractSuperNumberField} that allow modifications to minimum and maximum number of fraction digits.
 * THose methods by default are hidden from public access.
 * @param <T> Type of number.
 * @author miki
 * @since 2020-04-08
 */
public abstract class AbstractSuperFloatingPointField<T extends Number> extends AbstractSuperNumberField<T> {

    /**
     * Creates the field.
     *
     * @param defaultValue           Default value to use on startup and when there are errors.
     * @param negativityPredicate    Check for whether or not given value is negative.
     * @param turnToPositiveOperator Operation to turn number into a positive one.
     * @param label                  Label of the field.
     * @param locale                 Locale to use.
     * @param maxFractionDigits      Max number of fraction digits. Overwrites the settings in format obtained based on {@code locale}.
     */
    protected AbstractSuperFloatingPointField(T defaultValue, SerializablePredicate<T> negativityPredicate, SerializableFunction<T, T> turnToPositiveOperator, String label, Locale locale, int maxFractionDigits) {
        super(defaultValue, negativityPredicate, turnToPositiveOperator, label, locale, maxFractionDigits);
    }

    @Override
    public void setMinimumFractionDigits(int digits) {
        super.setMinimumFractionDigits(digits);
    }

    @Override
    public void setMaximumFractionDigits(int digits) {
        super.setMaximumFractionDigits(digits);
    }
}
