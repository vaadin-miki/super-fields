package org.vaadin.miki.superfields.numbers;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SuperBigDecimalFieldTest extends BaseTestsForFloatingPointNumbers<BigDecimal> {

    public static final BigDecimal TEN_DIGITS_PLUS_TWO = BigDecimal.valueOf(123456789012L, 2);
    public static final String FORMATTED_TEN_DIGITS_PLUS_TWO = "1 234 567 890,12"; // there are NBSPs in this string
    public static final String NO_GROUPS_FORMATTED_TEN_DIGITS_PLUS_TWO = "1234567890,12";

    private static final Map<String, BigDecimal> SCI_NOTATION = Map.of(
            "2E2", BigDecimal.valueOf(200),
            "2,3e-2", BigDecimal.valueOf(23, 3),
            "-0,4e2", BigDecimal.valueOf(-40)
    );

    public SuperBigDecimalFieldTest() {
        super(
                ()->new SuperBigDecimalField(new Locale("pl", "PL"), 5),
                TEN_DIGITS_PLUS_TWO, TEN_DIGITS_PLUS_TWO.negate(), FORMATTED_TEN_DIGITS_PLUS_TWO, NO_GROUPS_FORMATTED_TEN_DIGITS_PLUS_TWO, BigDecimal.ZERO
        );
        this.invalidInputs("E", "-e", "31,e", "a", "a1", "1:a", "1:E2", "1-e-3", "1,e2-");
    }

    @Test
    public void testMinimumFractionDigits() {
        this.getField().setValue(TEN_DIGITS_PLUS_TWO);
        this.getField().setMinimumFractionDigits(6);
        Assert.assertEquals(FORMATTED_TEN_DIGITS_PLUS_TWO+"0000", this.getField().getRawValue());
        Assert.assertEquals(TEN_DIGITS_PLUS_TWO, this.getField().getValue());
    }

    @Test
    public void testMaximumFractionDigits() {
        this.getField().setValue(TEN_DIGITS_PLUS_TWO);
        this.getField().setMaximumFractionDigits(1);
        Assert.assertEquals("1 234 567 890,1", this.getField().getRawValue());
        Assert.assertEquals(TEN_DIGITS_PLUS_TWO, this.getField().getValue());
        this.getField().setMaximumFractionDigits(2);
        Assert.assertEquals(FORMATTED_TEN_DIGITS_PLUS_TWO, this.getField().getRawValue());
        Assert.assertEquals(TEN_DIGITS_PLUS_TWO, this.getField().getValue());
    }

    @Override
    protected SuperBigDecimalField getField() {
        return (SuperBigDecimalField) super.getField();
    }

    @Test
    public void testInputsWithScientificNotationTurnedOn() {
        Assert.assertFalse(this.getField().isScientificNotationEnabled());
        this.getField().setMaximumExponentDigits(3);
        Assert.assertTrue(this.getField().isScientificNotationEnabled());
        this.testInvalidOutOfTheBoxInputs();
        this.testValidOutOfTheBoxInputs();
        this.testValidLimitedInputs();
    }

    @Test
    public void testScientificNotationSupported() throws ParseException {
        this.getField().setMaximumExponentDigits(3);
        for(Map.Entry<String, BigDecimal> entry: SCI_NOTATION.entrySet()) {
            for(int zmp1 = 0; zmp1<entry.getKey().length(); zmp1++) {
                final String sub = entry.getKey().substring(0, zmp1);
                Assert.assertTrue("scientific string " + entry.getKey() + " should be accepted as " + sub + " in step " + zmp1 + " with regexp " + this.getField().getRegexp(), sub.matches(this.getField().getRegexp()));
                if(!sub.isEmpty())
                    Assert.assertNotNull("non-empty entry "+sub+" must parse as non-null", this.getField().parseRawValue(sub));
            }
            Assert.assertEquals("string "+entry.getKey()+" should be parsed as "+entry.getValue().toEngineeringString(), 0, entry.getValue().compareTo(this.getField().parseRawValue(entry.getKey())));
            Assert.assertNotNull("string "+entry.getKey()+" must parse as proper value", this.getField().parseRawValue(entry.getKey()));
        }
    }

    @Test
    public void testScientificNotationConstraints() {
        // constraints are so that the significand digits are not ever larger than the regular digit's constraints
        this.getField().setMaximumIntegerDigits(4);
        this.getField().setMaximumFractionDigits(8);
        this.getField().setMaximumSignificandIntegerDigits(6);
        this.getField().setMaximumSignificandFractionDigits(11);
        Assert.assertEquals(4, this.getField().getMaximumSignificandIntegerDigits());
        Assert.assertEquals(8, this.getField().getMaximumSignificandFractionDigits());
        this.getField().setMaximumSignificandFractionDigits(6);
        Assert.assertEquals(6, this.getField().getMaximumSignificandFractionDigits());
        this.getField().setMaximumFractionDigits(3);
        Assert.assertEquals(3, this.getField().getMaximumSignificandFractionDigits());
        this.getField().setMaximumFractionDigits(5);
        Assert.assertEquals(5, this.getField().getMaximumSignificandFractionDigits());
        this.getField().setMaximumFractionDigits(11);
        Assert.assertEquals(6, this.getField().getMaximumSignificandFractionDigits());
        // exponent size is 0, so feature is disabled
        Assert.assertFalse(this.getField().isScientificNotationEnabled());
        this.getField().setMaximumExponentDigits(2);
        Assert.assertTrue(this.getField().isScientificNotationEnabled());
    }

    // reported in #358
    @Test
    public void testDecimalFormatError() {
        final DecimalFormat format = new DecimalFormat("0.####E0");
        // default format symbols are based on the system locale - so enforcing . as decimal separator here
        format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        this.getField().setDecimalFormat(format);
        final BigDecimal value = BigDecimal.valueOf(123400);
        this.getField().setValue(value);
        // need to compare values as they are (123400 is different that 1.234E+5)
        Assert.assertEquals(value.intValue(), this.getField().getValue().intValue());
        Assert.assertEquals("1.234E5", this.getField().getRawValue());
        Assert.assertTrue(this.getField().isScientificNotationEnabled());
    }

    // reported in #369
    @Test
    public void testNoDecimalFormatMoreExponentDigits() {
        final BigDecimal value = BigDecimal.valueOf(1, 15);
        this.getField().setMaximumFractionDigits(20);
        this.getField().setValue(value);
        Assert.assertEquals("0,000000000000001", this.getField().getRawValue());
    }

    // reported in #369
    @Test
    public void testDecimalFormatMoreExponentDigits() {
        final BigDecimal value = BigDecimal.valueOf(13, 12);
        final DecimalFormat format = new DecimalFormat("0.0##E0");
        format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        this.getField().setDecimalFormat(format);
        this.getField().setValue(value);
        Assert.assertEquals("1.3E-11", this.getField().getRawValue());
    }

    @Test
    public void testAlternativeSeparators() throws ParseException {
        this.getField().setLocale(Locale.FRANCE);
        this.getField().setGroupingSeparatorAlternatives(Set.of('_'));
        this.getField().setDecimalSeparatorAlternatives(Set.of('|'));
        for(String s: new String[]{"123_456|78", "12_34_56|78", "12345_6|78", "_123_456|78", "_123456_|78"}) {
            final BigDecimal value = this.getField().parseRawValue(s);
            Assert.assertEquals(BigDecimal.valueOf(123456.78), value);
        }
    }

    @Test
    public void testAlternativeSeparatorsWithNegativeSign() throws ParseException {
        this.getField().setLocale(Locale.GERMANY);
        this.getField().setNegativeSignAlternatives(Set.of('^', '%'));
        this.getField().setDecimalSeparatorAlternatives(Set.of('_'));
        for(String s: new String[]{"^123456_78", "%123456_78", "-123456_78"}) {
            final BigDecimal value = this.getField().parseRawValue(s);
            Assert.assertEquals(BigDecimal.valueOf(-123456.78), value);
        }
    }

    @Test
    public void testScientificWithAlternativeSeparatorsWithNegativeSign() throws ParseException {
        this.getField().withLocale(Locale.GERMANY)
            .withExponentSeparator('e')
            .withMaximumExponentDigits(3)
            .withMaximumSignificandIntegerDigits(2)
            .withMaximumSignificandFractionDigits(2)
            .withNegativeSignAlternatives(Set.of('^', '%'))
            .setDecimalSeparatorAlternatives(Set.of('_'));
        for(String s: new String[]{"^2_3e%3", "%2_3E%3", "-2_3e^3"}) {
            final BigDecimal value = this.getField().parseRawValue(s);
            Assert.assertEquals(BigDecimal.valueOf(-0.0023), value);
        }
    }

}