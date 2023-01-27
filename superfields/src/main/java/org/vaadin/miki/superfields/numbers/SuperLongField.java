package org.vaadin.miki.superfields.numbers;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * A field for {@link Long} values that are properly formatted.
 *
 * @author miki
 * @since 2020-04-07
 */
@JsModule("./super-long-field.ts")
@Tag("super-long-field")
@SuppressWarnings("squid:S110") // yes, it has more than 5 superclasses, but what can I do?
public class SuperLongField extends AbstractSuperNumberField<Long, SuperLongField> {

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
        this(0L, label, locale);
    }

    /**
     * Constructs the field with given default value and label, and with default {@link Locale}.
     * @param defaultValue Default value.
     * @param label Label that accompanies the field.
     */
    public SuperLongField(Long defaultValue, String label) {
        this(defaultValue, label, Locale.getDefault());
    }

    /**
     * Constructs the field.
     * @param defaultValue Default value.
     * @param label Label that accompanies the field.
     * @param locale Locale to use for formatting.
     */
    public SuperLongField(Long defaultValue, String label, Locale locale) {
        super(defaultValue, d -> d < 0, Math::abs, label, locale, 0);
    }

    @Override
    protected Long parseRawValue(String rawValue, DecimalFormat format) throws ParseException {
        return format.parse(rawValue).longValue();
    }

    @Override
    public void setDecimalFormat(DecimalFormat format) {
        format.setMaximumFractionDigits(0);
        super.setDecimalFormat(format);
    }
}
