package org.vaadin.miki.superfields.numbers;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * A field for {@link Double} values that are properly formatted.
 *
 * @author miki
 * @since 2020-04-07
 */
@JsModule("./super-double-field.ts")
@Tag("super-double-field")
@SuppressWarnings("squid:S110") // yes, it has more than 5 superclasses, but what can I do?
public class SuperDoubleField extends AbstractSuperFloatingPointField<Double, SuperDoubleField> {

    /**
     * Constructs the field with an empty label, zero as default value and with default {@link Locale}.
     */
    public SuperDoubleField() {
        this("");
    }

    /**
     * Constructs the field with an empty label and zero as default value.
     * @param locale Locale to use for formatting.
     */
    public SuperDoubleField(Locale locale) {
        this("", locale);
    }

    /**
     * Constructs the field with an empty label and zero as default value.
     * @param locale Locale to use for formatting.
     * @param maxFractionDigits Maximum number of fraction digits allowed (overwrites setting found in {@code locale}.
     */
    public SuperDoubleField(Locale locale, int maxFractionDigits) {
        this("", locale, maxFractionDigits);
    }

    /**
     * Constructs the field with default {@link Locale} and zero as default value.
     * @param label Label accompanying the field.
     */
    public SuperDoubleField(String label) {
        this(label, Locale.getDefault());
    }

    /**
     * Constructs the field with zero as the default value.
     * @param label Label accompanying the field.
     * @param locale Locale to use for formatting.
     */
    public SuperDoubleField(String label, Locale locale) {
        this(label, locale, -1);
    }

    /**
     * Constructs the field with given default value and label, and with default {@link Locale}.
     * @param defaultValue Default value.
     * @param label Label that accompanies the field.
     */
    public SuperDoubleField(Double defaultValue, String label) {
        this(defaultValue, label, Locale.getDefault(), -1);
    }

    /**
     * Constructs the field with zero as the default value..
     * @param label Label accompanying the field.
     * @param locale Locale to use for formatting.
     * @param maxFractionDigits Maximum number of fraction digits allowed (overwrites setting found in {@code locale}.
     */
    public SuperDoubleField(String label, Locale locale, int maxFractionDigits) {
        this(0.0d, label, locale, maxFractionDigits);
    }

    /**
     * Constructs the field.
     * @param defaultValue Default value.
     * @param label Label accompanying the field.
     * @param locale Locale to use for formatting.
     * @param maxFractionDigits Maximum number of fraction digits allowed (overwrites setting found in {@code locale}.
     */
    public SuperDoubleField(Double defaultValue, String label, Locale locale, int maxFractionDigits) {
        super(defaultValue, d -> d < 0.0d, Math::abs, label, locale, maxFractionDigits);
    }

    @Override
    protected Double parseRawValue(String rawValue, DecimalFormat format) throws ParseException {
        return format.parse(rawValue).doubleValue();
    }

}