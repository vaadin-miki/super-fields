package org.vaadin.miki.superfields.numbers;

import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.SerializablePredicate;

import java.util.Locale;

/**
 * Base class for implementations of {@link AbstractSuperNumberField} that allow modifications to minimum and maximum number of fraction digits.
 * Those methods by default are hidden from public access.
 *
 * @param <T> Type of number.
 * @param <SELF> Self type.
 *
 * @author miki
 * @since 2020-04-08
 */
@SuppressWarnings("squid:S119") // SELF is a fine generic name that is more descriptive than S
public abstract class AbstractSuperFloatingPointField<T extends Number, SELF extends AbstractSuperFloatingPointField<T, SELF>> extends AbstractSuperNumberField<T, SELF> {

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

    /**
     * Chains {@link #setMinimumFractionDigits(int)} and returns itself.
     * @param digits Minimum number of digits to set.
     * @return This.
     */
    @SuppressWarnings("unchecked")
    public final SELF withMinimumFractionDigits(int digits) {
        this.setMinimumFractionDigits(digits);
        return (SELF)this;
    }

    /**
     * Chains {@link #setMaximumFractionDigits(int)} and returns itself.
     * @param digits Maximum number of digits to set.
     * @return This.
     */
    @SuppressWarnings("unchecked")
    public final SELF withMaximumFractionDigits(int digits) {
        this.setMaximumFractionDigits(digits);
        return (SELF)this;
    }

    @Override
    public void setIntegerPartOptional(boolean required) {
        super.setIntegerPartOptional(required);
    }

    @Override
    public boolean isIntegerPartOptional() {
        return super.isIntegerPartOptional();
    }

    /**
     * Chains {@link #setIntegerPartOptional(boolean)} and returns itself.
     * @param optional Whether the required part is optional or not (default).
     * @return This.
     */
    @SuppressWarnings("unchecked")
    public final SELF withIntegerPartOptional(boolean optional) {
        this.setIntegerPartOptional(optional);
        return (SELF) this;
    }

    /**
     * Chains {@link #setIntegerPartOptional(boolean)} with {@code false} as parameter and returns itself.
     * @return This.
     */
    public final SELF withIntegerPartRequired() {
        return this.withIntegerPartOptional(false);
    }

    /**
     * Chains {@link #setIntegerPartOptional(boolean)} with {@code true} as parameter and returns itself.
     * @return This.
     */
    public final SELF withIntegerPartOptional() {
        return this.withIntegerPartOptional(true);
    }
}
