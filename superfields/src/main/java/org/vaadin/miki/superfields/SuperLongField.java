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

    public SuperLongField() {
        this("");
    }

    public SuperLongField(Locale locale) {
        this("", locale);
    }

    public SuperLongField(String label) {
        this(label, Locale.getDefault());
    }

    public SuperLongField(String label, Locale locale) {
        this(0, label, locale);
    }

    public SuperLongField(long defaultValue, String label, Locale locale) {
        super(defaultValue, d -> d < 0, Math::abs, label, locale, 0);
    }

    @Override
    protected Long parseRawValue(String rawValue, DecimalFormat format) throws ParseException {
        return format.parse(rawValue).longValue();
    }
}
