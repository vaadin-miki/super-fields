package org.vaadin.miki.superfields.numbers;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

public class SuperDoubleFieldTest extends BaseTestsForFloatingPointNumbers<Double> {

    public static final double SIX_DIGITS_PLUS_ONE = 123456.7d;
    public static final String FORMATTED_SIX_DIGITS_PLUS_ONE = "123 456,7"; // there is a NBSP in this string
    public static final String NO_GROUPS_FORMATTED_SIX_DIGITS_PLUS_ONE = "123456,7";

    public SuperDoubleFieldTest() {
        super( ()->new SuperDoubleField(new Locale("pl", "PL"), 5),
                SIX_DIGITS_PLUS_ONE, -SIX_DIGITS_PLUS_ONE, FORMATTED_SIX_DIGITS_PLUS_ONE, NO_GROUPS_FORMATTED_SIX_DIGITS_PLUS_ONE, 0.0d
        );
        this.invalidInputs("12345,123456"); // there is a limit of 5 fraction digits
        this.validInputs(6, 3, 5, FORMATTED_SIX_DIGITS_PLUS_ONE+"00");
        this.invalidInputs(6, 1, 1, FORMATTED_SIX_DIGITS_PLUS_ONE+"2");
    }

    @Test
    public void testMinimumFractionDigits() {
        this.getField().setValue(SIX_DIGITS_PLUS_ONE);
        this.getField().setMinimumFractionDigits(3);
        Assert.assertEquals(FORMATTED_SIX_DIGITS_PLUS_ONE+"00", this.getField().getRawValue());
        Assert.assertEquals(SIX_DIGITS_PLUS_ONE, this.getField().getValue(), 0.0001d);
    }

    @Test
    public void testMaximumFractionDigits() {
        this.getField().setValue(1.234d);
        this.getField().setMaximumFractionDigits(1);
        Assert.assertEquals("1,2", this.getField().getRawValue());
        Assert.assertEquals(1.234d, this.getField().getValue(), 0);
    }

    @Test
    public void testAlternativeSeparators() throws ParseException {
        this.getField().setLocale(Locale.FRANCE);
        this.getField().setGroupingSeparatorAlternatives(Collections.singleton('_'));
        this.getField().setDecimalSeparatorAlternatives(Collections.singleton('|'));
        for(String s: new String[]{"123_456|78", "12_34_56|78", "12345_6|78", "_123_456|78", "_123456_|78"}) {
            final Double value = this.getField().parseRawValue(s);
            Assert.assertEquals(Double.valueOf(123456.78), value);
        }
    }

    @Test
    public void testAlternativeSeparatorsWithNegativeSign() throws ParseException {
        this.getField().setLocale(Locale.GERMANY);
        this.getField().setNegativeSignAlternatives(new HashSet<>(Arrays.asList('^', '%')));
        this.getField().setDecimalSeparatorAlternatives(Collections.singleton('_'));
        for(String s: new String[]{"^123456_78", "%123456_78", "-123456_78"}) {
            final Double value = this.getField().parseRawValue(s);
            Assert.assertEquals(Double.valueOf(-123456.78), value);
        }
    }

    @Test
    public void testOverlappingAlternatives() throws ParseException {
        this.getField().withLocale(Locale.GERMANY)
            .withOverlappingAlternatives()
            .withDecimalSeparatorAlternatives('.');
        Assert.assertTrue(this.getField().getDecimalSeparatorAlternatives().contains('.'));
        final double value = this.getField().parseRawValue("12345.67");
        Assert.assertEquals(12345.67, value, 0.00001);
    }

    @Test
    public void testWithoutOverlappingAlternatives() throws ParseException {
        this.getField().withLocale(Locale.GERMANY)
            .withDecimalSeparatorAlternatives('.');
        // without explicitly allowing symbols to overlap, the . is ignored and treated as a grouping symbol
        Assert.assertTrue(this.getField().getDecimalSeparatorAlternatives().isEmpty());
        final double value = this.getField().parseRawValue("12345.67");
        Assert.assertEquals(1234567, value, 0.00001);
    }


}