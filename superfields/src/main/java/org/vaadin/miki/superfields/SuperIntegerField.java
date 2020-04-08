package org.vaadin.miki.superfields;

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
public class SuperIntegerField extends AbstractSuperNumberField<Integer> {

    public SuperIntegerField() {
        this("");
    }

    public SuperIntegerField(Locale locale) {
        this("", locale);
    }

    public SuperIntegerField(String label) {
        this(label, Locale.getDefault());
    }

    public SuperIntegerField(String label, Locale locale) {
        this(0, label, locale);
    }

    public SuperIntegerField(int defaultValue, String label, Locale locale) {
        super(defaultValue, d -> d < 0, Math::abs, label, locale, 0);
    }

    @Override
    protected Integer parseRawValue(String rawValue, DecimalFormat format) throws ParseException {
        return format.parse(rawValue).intValue();
    }
}
