package org.vaadin.miki.superfields.numbers;

import com.vaadin.flow.component.Tag;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * A field for {@link BigDecimal} values that are properly formatted.
 * In addition it supports entering precise numbers through scientific notation.
 * Note: the value entered through scientific notation will be formatted according to the settings
 * and as such the displayed value may differ from the actual value.
 *
 * @author miki
 * @since 2020-04-08
 */
@Tag("super-big-decimal-field")
@SuppressWarnings("squid:S110") // yes, it has more than 5 superclasses, but what can I do?
public class SuperBigDecimalField extends AbstractSuperFloatingPointField<BigDecimal, SuperBigDecimalField> {

    private int maximumSignificandIntegerDigits = -1;
    private int maximumSignificandFractionDigits = -1;
    private int maximumExponentDigits = 0;
    private boolean negativeExponentAllowed = true;
    private char exponentSeparator = 'e';

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
     * Constructs the field with given default value and label, and with default {@link Locale}.
     * @param defaultValue Default value.
     * @param label Label that accompanies the field.
     */
    public SuperBigDecimalField(BigDecimal defaultValue, String label) {
        this(defaultValue, label, Locale.getDefault(), -1);
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
    protected StringBuilder buildRegularExpression(StringBuilder builder, DecimalFormat format) {
        builder = super.buildRegularExpression(builder.append("("), format).append(")");
        // adds scientific notation support
        if(this.getMaximumExponentDigits() > 0) {
            // it is supported in addition to the original formatting
            builder.append("|(^");
            if(this.isNegativeValueAllowed())
                builder.append("-?");
            // max significand integer digits cannot go below 1 (will be converted to 1 anyway)
            builder.append("\\d{1,").append(this.getMaximumSignificandIntegerDigits()).append("}");
            // significand fraction part can be disabled, though
            if(this.getMaximumSignificandFractionDigits() > 0)
                builder.append("(")
                       .append(escapeDot(format.getDecimalFormatSymbols().getDecimalSeparator()))
                       .append("\\d+)?");
            builder.append("((").append(String.valueOf(this.getExponentSeparator()).toUpperCase(this.getLocale()))
                   .append("|")
                   .append(String.valueOf(this.getExponentSeparator()).toLowerCase(this.getLocale()))
                   .append(")");
            if(this.isNegativeExponentAllowed())
                builder.append("(\\+|-)?");
            else builder.append("\\+?");
            builder.append("\\d{0,").append(this.getMaximumExponentDigits()).append("})").append("$)");
        }
        return builder;
    }

    public final boolean isScientificNotationSupported() {
        return this.getExponentSeparator() > 0;
    }

    @Override
    protected BigDecimal parseRawValue(String rawValue, DecimalFormat format) throws ParseException {
        // using scientific notation typing allows for some weird situations, e.g. when the last character can be E or - or + - in general, something outside of 0 to 9
        // and yes, Character.isDigit actually supports more than 0 to 9, let's hope this omission does not bite back
        if(!Character.isDigit(rawValue.charAt(rawValue.length()-1)))
            rawValue = rawValue + "0";

        if(this.isScientificNotationSupported() && rawValue.toUpperCase(this.getLocale()).contains(String.valueOf(this.getExponentSeparator()).toUpperCase(this.getLocale()))) {
            return new BigDecimal(rawValue.replace(format.getDecimalFormatSymbols().getDecimalSeparator(), '.'));
        }
        else {
            // no idea how decimal formats work, but just in case the instance is shared across many objects...
            boolean oldParse = format.isParseBigDecimal();
            format.setParseBigDecimal(true);
            BigDecimal result = (BigDecimal) format.parse(rawValue);
            // ...here it returns to the prior state after it was used
            format.setParseBigDecimal(oldParse);
            return result;
        }
    }

    public void setMaximumExponentDigits(int maximumExponentDigits) {
        this.maximumExponentDigits = maximumExponentDigits;
        this.updateRegularExpression();
    }

    public int getMaximumExponentDigits() {
        return this.maximumExponentDigits;
    }

    public SuperBigDecimalField withMaximumExponentDigits(int maximumExponentDigits) {
        this.setMaximumExponentDigits(maximumExponentDigits);
        return this;
    }

    public int getMaximumSignificandIntegerDigits() {
        if(this.maximumSignificandIntegerDigits < 0)
            return this.getMaximumIntegerDigits();
        else
            return Math.max(1, Math.min(this.maximumSignificandIntegerDigits, this.getMaximumIntegerDigits()));
    }

    public void setMaximumSignificandIntegerDigits(int maximumSignificandIntegerDigits) {
        this.maximumSignificandIntegerDigits = maximumSignificandIntegerDigits;
        this.updateRegularExpression();
    }

    public SuperBigDecimalField withMaximumSignificandIntegerDigits(int maximumSignificandIntegerDigits) {
        this.setMaximumSignificandIntegerDigits(maximumSignificandIntegerDigits);
        return this;
    }

    public int getMaximumSignificandFractionDigits() {
        if(this.maximumSignificandFractionDigits < 0)
            return this.getMaximumFractionDigits();
        else
            return Math.max(0, Math.min(this.maximumSignificandFractionDigits, this.getMaximumFractionDigits()));
    }

    public void setMaximumSignificandFractionDigits(int maximumSignificandFractionDigits) {
        this.maximumSignificandFractionDigits = maximumSignificandFractionDigits;
        this.updateRegularExpression();
    }

    public SuperBigDecimalField withMaximumSignificandFractionDigits(int maximumSignificandFractionDigits) {
        this.setMaximumSignificandFractionDigits(maximumSignificandFractionDigits);
        return this;
    }

    public char getExponentSeparator() {
        return exponentSeparator;
    }

    public void setExponentSeparator(char exponentSeparator) {
        this.exponentSeparator = exponentSeparator;
        this.updateRegularExpression();
    }

    public SuperBigDecimalField withExponentSeparator(char exponentSeparator) {
        this.setExponentSeparator(exponentSeparator);
        return this;
    }

    public boolean isNegativeExponentAllowed() {
        return negativeExponentAllowed;
    }

    public void setNegativeExponentAllowed(boolean negativeExponentAllowed) {
        this.negativeExponentAllowed = negativeExponentAllowed;
        this.updateRegularExpression();
    }

    public SuperBigDecimalField withNegativeExponentAllowed(boolean negativeExponentAllowed) {
        this.setNegativeExponentAllowed(negativeExponentAllowed);
        return this;
    }
}
