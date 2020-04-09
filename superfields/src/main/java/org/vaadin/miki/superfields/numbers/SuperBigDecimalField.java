package org.vaadin.miki.superfields.numbers;

import com.vaadin.flow.component.Tag;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * A field for {@link BigDecimal} values that are properly formatted.
 *
 * @author miki
 * @since 2020-04-08
 */
@Tag("super-big-decimal-field")
public class SuperBigDecimalField extends AbstractSuperFloatingPointField<BigDecimal> {

    /**
     * Constructs the field with an empty label, {@link BigDecimal#ZERO} as default value and with default {@link Locale}.
     */
    public SuperBigDecimalField() {
        this("");
    }

    /**
     * Constructs the field with an empty label and {@link BigDecimal#ZERO} as default value.
     * @param locale Locale to use for formatting.
     */
    public SuperBigDecimalField(Locale locale) {
        this("", locale);
    }

    /**
     * Constructs the field with an empty label and {@link BigDecimal#ZERO} as default value.
     * @param locale Locale to use for formatting.
     * @param maxFractionDigits Maximum number of fraction digits allowed (overwrites setting found in {@code locale}.
     */
    public SuperBigDecimalField(Locale locale, int maxFractionDigits) {
        this("", locale, maxFractionDigits);
    }

    /**
     * Constructs the field with default {@link Locale} and {@link BigDecimal#ZERO} as default value.
     * @param label Label accompanying the field.
     */
    public SuperBigDecimalField(String label) {
        this(label, Locale.getDefault());
    }

    /**
     * Constructs the field with {@link BigDecimal#ZERO} as the default value.
     * @param label Label accompanying the field.
     * @param locale Locale to use for formatting.
     */
    public SuperBigDecimalField(String label, Locale locale) {
        this(label, locale, -1);
    }

    /**
     * Constructs the field with {@link BigDecimal#ZERO} as the default value..
     * @param label Label accompanying the field.
     * @param locale Locale to use for formatting.
     * @param maxFractionDigits Maximum number of fraction digits allowed (overwrites setting found in {@code locale}.
     */
    public SuperBigDecimalField(String label, Locale locale, int maxFractionDigits) {
        this(BigDecimal.ZERO, label, locale, maxFractionDigits);
    }

    /**
     * Constructs the field.
     * @param defaultValue Default value.
     * @param label Label accompanying the field.
     * @param locale Locale to use for formatting.
     * @param maxFractionDigits Maximum number of fraction digits allowed (overwrites setting found in {@code locale}.
     */
    public SuperBigDecimalField(BigDecimal defaultValue, String label, Locale locale, int maxFractionDigits) {
        super(defaultValue, d -> d.compareTo(BigDecimal.ZERO) < 0, BigDecimal::abs, label, locale, maxFractionDigits);
    }

    @Override
    protected BigDecimal parseRawValue(String rawValue, DecimalFormat format) throws ParseException {
        // no idea how decimal formats work, but just in case the instance is shared across many objects...
        boolean oldParse = format.isParseBigDecimal();
        format.setParseBigDecimal(true);
        BigDecimal result = (BigDecimal)format.parse(rawValue);
        // ...here it returns to the prior state after it was used
        format.setParseBigDecimal(oldParse);
        return result;
    }

}
