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
//@JsModule("./super-big-decimal-field.ts")
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

    // found during #358 - DecimalFormat may specify the exponent
    @Override
    public void setDecimalFormat(DecimalFormat format) {
        super.setDecimalFormat(format);
        if(format.toLocalizedPattern().indexOf('E') == -1)
            this.setExponentSeparator('\0');
        else {
            this.setExponentSeparator('e');
            this.setMaximumExponentDigits(format.getMaximumIntegerDigits());
        }
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

    /**
     * Checks whether scientific notation input is supported (by default it is not).
     *
     * @return {@code true} when {@link #getExponentSeparator()} is defined and {@link #getMaximumExponentDigits()} is greater than zero, {@code false} otherwise.
     */
    public final boolean isScientificNotationEnabled() {
        return this.getExponentSeparator() > 0 && this.getMaximumExponentDigits() > 0;
    }

    private boolean isValueInScientificNotation(String rawValue) {
        return this.isScientificNotationEnabled() && rawValue.toUpperCase(this.getLocale()).contains(String.valueOf(this.getExponentSeparator()).toUpperCase(this.getLocale()));
    }

    @Override
    protected BigDecimal parseRawValue(String rawValue, DecimalFormat format) throws ParseException {
        // using scientific notation typing allows for some weird situations, e.g. when the last character can be E or - or + - in general, something outside of 0 to 9
        // and yes, Character.isDigit actually supports more than 0 to 9, let's hope this omission does not bite back
        if(!Character.isDigit(rawValue.charAt(rawValue.length()-1)))
            rawValue = rawValue + "0";

        if(this.isValueInScientificNotation(rawValue)) {
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

    @Override
    protected void updateFieldValue() {
        // this is here to fix #268
        // not a nice-looking fix, but it seems to work nice
        if(this.isValueInScientificNotation(this.getRawValue())) {
            try {
                this.setValue(this.parseRawValue(this.getRawValue()));
            }
            catch (ParseException pe) {
                super.updateFieldValue();
            }
        }
        else super.updateFieldValue();
    }

    /**
     * Sets maximum allowed digits in exponent. If this number is greater than zero, it enables scientific notation input.
     * @param maximumExponentDigits Number of digits allowed in the exponent part of scientific notation.
     * @see #isScientificNotationEnabled()
     */
    public void setMaximumExponentDigits(int maximumExponentDigits) {
        this.maximumExponentDigits = maximumExponentDigits;
        this.updateRegularExpression();
    }

    /**
     * Returns the number of allowed digits in exponent.
     * @return Number of digits. Values of zero or less indicate that scientific notation input is not enabled.
     */
    public int getMaximumExponentDigits() {
        return this.maximumExponentDigits;
    }

    /**
     * Chains {@link #setMaximumExponentDigits(int)} and returns itself.
     * @param maximumExponentDigits Number of digits allowed in the exponent part of scientific notation.
     * @return This.
     * @see #setMaximumExponentDigits(int)
     */
    public final SuperBigDecimalField withMaximumExponentDigits(int maximumExponentDigits) {
        this.setMaximumExponentDigits(maximumExponentDigits);
        return this;
    }

    /**
     * Returns the maximum number of digits allowed in the integer part of the significand in the scientific notation.
     * @return The number of digits. It will never be more than {@link #getMaximumIntegerDigits()}.
     * @see #getMaximumIntegerDigits()
     */
    public int getMaximumSignificandIntegerDigits() {
        if(this.maximumSignificandIntegerDigits < 0)
            return this.getMaximumIntegerDigits();
        else
            return Math.max(1, Math.min(this.maximumSignificandIntegerDigits, this.getMaximumIntegerDigits()));
    }

    /**
     * Sets the number of digits allowed in the integer part of the significand in the scientific notation.
     * @param maximumSignificandIntegerDigits The number of digits. The lesser value of this number and {@link #getMaximumIntegerDigits()} will be used.
     */
    public void setMaximumSignificandIntegerDigits(int maximumSignificandIntegerDigits) {
        this.maximumSignificandIntegerDigits = maximumSignificandIntegerDigits;
        this.updateRegularExpression();
    }

    /**
     * Chains {@link #setMaximumSignificandIntegerDigits(int)} and returns itself.
     * @param maximumSignificandIntegerDigits The number of digits allowed in the integer part of the significand.
     * @return This.
     * @see #setMaximumSignificandIntegerDigits(int)
     */
    public final SuperBigDecimalField withMaximumSignificandIntegerDigits(int maximumSignificandIntegerDigits) {
        this.setMaximumSignificandIntegerDigits(maximumSignificandIntegerDigits);
        return this;
    }

    /**
     * Returns the maximum number of digits allowed in the fractional (decimal) part of the significand in the scientific notation.
     * @return The number of digits. It will never be more than {@link #getMaximumFractionDigits()}.
     * @see #getMaximumFractionDigits()
     */
    public int getMaximumSignificandFractionDigits() {
        if(this.maximumSignificandFractionDigits < 0)
            return this.getMaximumFractionDigits();
        else
            return Math.max(0, Math.min(this.maximumSignificandFractionDigits, this.getMaximumFractionDigits()));
    }

    /**
     * Sets the number of digits allowed in the fractional (decimal) part of the significand in the scientific notation.
     * @param maximumSignificandFractionDigits The number of digits. The lesser value of this number and {@link #getMaximumFractionDigits()} will be used.
     */
    public void setMaximumSignificandFractionDigits(int maximumSignificandFractionDigits) {
        this.maximumSignificandFractionDigits = maximumSignificandFractionDigits;
        this.updateRegularExpression();
    }

    /**
     * Chains {@link #setMaximumSignificandFractionDigits(int)} and returns itself.
     * @param maximumSignificandFractionDigits The number of digits allowed in the fraction (decimal) part of the significand.
     * @return This.
     * @see #setMaximumSignificandFractionDigits(int)
     */
    public final SuperBigDecimalField withMaximumSignificandFractionDigits(int maximumSignificandFractionDigits) {
        this.setMaximumSignificandFractionDigits(maximumSignificandFractionDigits);
        return this;
    }

    /**
     * Returns the current exponent separator in the scientific notation. Both cases (according to {@link #getLocale()} of this character are supported.
     * @return A character that serves as a separator in scientific notation.
     */
    public char getExponentSeparator() {
        return exponentSeparator;
    }

    /**
     * Sets the exponent separator. Both cases (according to {@link #getLocale()}) of the character will be supported.
     * @param exponentSeparator A character to use. Setting this to {@code 0} will disable scientific notation support.
     * @see #isScientificNotationEnabled()
     */
    public void setExponentSeparator(char exponentSeparator) {
        this.exponentSeparator = exponentSeparator;
        this.updateRegularExpression();
    }

    /**
     * Chains {@link #setExponentSeparator(char)} and returns itself.
     * @param exponentSeparator A character to use as exponent separator in scientific notation.
     * @return This.
     * @see #setExponentSeparator(char)
     */
    public final SuperBigDecimalField withExponentSeparator(char exponentSeparator) {
        this.setExponentSeparator(exponentSeparator);
        return this;
    }

    /**
     * Checks whether or not negative exponent is allowed when entering number in scientific notation.
     * @return {@code true} (default) when the exponent part can be negative number, {@code false} otherwise.
     */
    public boolean isNegativeExponentAllowed() {
        return negativeExponentAllowed;
    }

    /**
     * Allows or disallows the exponent in scientific notation to be negative.
     * @param negativeExponentAllowed Whether or not the exponent can be negative.
     */
    public void setNegativeExponentAllowed(boolean negativeExponentAllowed) {
        this.negativeExponentAllowed = negativeExponentAllowed;
        this.updateRegularExpression();
    }

    /**
     * Chains {@link #setNegativeExponentAllowed(boolean)} and returns itself.
     * @param negativeExponentAllowed Whether or not the exponent can be negative.
     * @return This.
     * @see #setNegativeExponentAllowed(boolean)
     */
    public final SuperBigDecimalField withNegativeExponentAllowed(boolean negativeExponentAllowed) {
        this.setNegativeExponentAllowed(negativeExponentAllowed);
        return this;
    }
}
