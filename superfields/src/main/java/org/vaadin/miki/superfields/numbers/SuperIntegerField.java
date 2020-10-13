package org.vaadin.miki.superfields.numbers;

import com.vaadin.flow.component.Tag;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * A field for {@link Integer} values that are properly formatted.
 *
 * @author miki
 * @since 2020-04-07
 */
@Tag("super-integer-field")
public class SuperIntegerField extends AbstractSuperNumberField<Integer, SuperIntegerField> {

    /**
     * Constructs the field with zero as default value, default {@link Locale} and an empty label.
     */
    public SuperIntegerField() {
        this("");
    }

    /**
     * Constructs the field with zero as default value and an empty label.
     * @param locale Locale to use for formatting.
     */
    public SuperIntegerField(Locale locale) {
        this("", locale);
    }

    /**
     * Constructs the field with zero as default value and default {@link Locale}.
     * @param label Label that accompanies the field.
     */
    public SuperIntegerField(String label) {
        this(label, Locale.getDefault());
    }

    /**
     * Constructs the field with zero as default value.
     * @param label Label that accompanies the field.
     * @param locale Locale to use for formatting.
     */
    public SuperIntegerField(String label, Locale locale) {
        this(0, label, locale);
    }

    /**
     * Constructs the field with given default value and label, and with default {@link Locale}.
     * @param defaultValue Default value.
     * @param label Label that accompanies the field.
     */
    public SuperIntegerField(Integer defaultValue, String label) {
        this(defaultValue, label, Locale.getDefault());
    }

    /**
     * Constructs the field.
     * @param defaultValue Default value.
     * @param label Label that accompanies the field.
     * @param locale Locale to use for formatting.
     */
    public SuperIntegerField(Integer defaultValue, String label, Locale locale) {
        super(defaultValue, d -> d < 0, Math::abs, label, locale, 0);
    }

    @Override
    protected Integer parseRawValue(String rawValue, DecimalFormat format) throws ParseException {
        return format.parse(rawValue).intValue();
    }

    @Override
    public void setDecimalFormat(DecimalFormat format) {
        format.setMaximumFractionDigits(0);
        super.setDecimalFormat(format);
    }
}
