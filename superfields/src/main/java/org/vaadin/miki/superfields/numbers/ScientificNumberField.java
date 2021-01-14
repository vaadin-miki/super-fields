package org.vaadin.miki.superfields.numbers;

import com.vaadin.flow.component.Tag;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

@Tag("scientific-number-field")
public class ScientificNumberField extends AbstractSuperNumberField<BigDecimal, ScientificNumberField> {

    /**
     * Creates the field.
     */
    public ScientificNumberField(Locale locale) {
        super(BigDecimal.ZERO, d -> d.compareTo(BigDecimal.ZERO) < 0, BigDecimal::abs, "", locale, -1);
    }

    @Override
    protected StringBuilder buildRegularExpression(StringBuilder builder, DecimalFormat format) {
        // do the original regexp, as the number may also be entered without scientific notation
        builder = super.buildRegularExpression(builder.append("("), format);
        // adds scientific notation support
        builder.append(")|(^-?\\d(").append(escapeDot(format.getDecimalFormatSymbols().getDecimalSeparator()))
                .append("\\d+)?((e|E)-?\\d*)?")
                .append("$)");
        return builder;
    }

    @Override
    protected BigDecimal parseRawValue(String rawValue, DecimalFormat format) throws ParseException {
        if(rawValue.toUpperCase(Locale.ROOT).contains("E"))
            return new BigDecimal(rawValue.replace(format.getDecimalFormatSymbols().getDecimalSeparator(), '.'));
        else return BigDecimalFormatParser.parseFromString(rawValue, format);
    }
}
