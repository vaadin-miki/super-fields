package org.vaadin.miki.superfields;

import com.vaadin.flow.component.Tag;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * A field for {@link Double} values that are properly formatted.
 *
 * @author miki
 * @since 2020-04-07
 */
@Tag("super-double-field")
public class SuperDoubleField extends AbstractSuperNumberField<Double> {

    public SuperDoubleField() {
        this("");
    }

    public SuperDoubleField(Locale locale) {
        this("", locale);
    }

    public SuperDoubleField(Locale locale, int decimalPrecision) {
        this("", locale, decimalPrecision);
    }

    public SuperDoubleField(String label) {
        this(label, Locale.getDefault());
    }

    public SuperDoubleField(String label, Locale locale) {
        this(label, locale, -1);
    }

    public SuperDoubleField(String label, Locale locale, int decimalPrecision) {
        this(0.0d, label, locale, decimalPrecision);
    }

    public SuperDoubleField(double defaultValue, String label, Locale locale, int decimalPrecision) {
        super(defaultValue, d -> d < 0.0d, Math::abs, label, locale, decimalPrecision);
    }

    @Override
    protected Double parseRawValue(String rawValue, DecimalFormat format) throws ParseException {
        return format.parse(rawValue).doubleValue();
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