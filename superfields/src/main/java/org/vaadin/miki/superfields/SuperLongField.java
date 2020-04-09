package org.vaadin.miki.superfields;

import com.vaadin.flow.component.Tag;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * A field for {@link Long} values that are properly formatted.
 *
 * @author miki
 * @since 2020-04-07
 */
@Tag("super-long-field")
public class SuperLongField extends AbstractSuperNumberField<Long> {

    /**
     * Constructs the field with zero as default value, default {@link Locale} and an empty label.
     */
    public SuperLongField() {
        this("");
    }

    /**
     * Constructs the field with zero as default value and an empty label.
     * @param locale Locale to use for formatting.
     */
    public SuperLongField(Locale locale) {
        this("", locale);
    }

    /**
     * Constructs the field with zero as default value and default {@link Locale}.
     * @param label Label that accompanies the field.
     */
    public SuperLongField(String label) {
        this(label, Locale.getDefault());
    }

    /**
     * Constructs the field with zero as default value.
     * @param label Label that accompanies the field.
     * @param locale Locale to use for formatting.
     */
    public SuperLongField(String label, Locale locale) {
        this(0, label, locale);
    }

    /**
     * Constructs the field.
     * @param defaultValue Default value.
     * @param label Label that accompanies the field.
     * @param locale Locale to use for formatting.
     */
    public SuperLongField(long defaultValue, String label, Locale locale) {
        super(defaultValue, d -> d < 0, Math::abs, label, locale, 0);
    }

    @Override
    protected Long parseRawValue(String rawValue, DecimalFormat format) throws ParseException {
        return format.parse(rawValue).longValue();
    }
}
