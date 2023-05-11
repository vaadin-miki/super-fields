package org.vaadin.miki.superfields.numbers;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Locale;
import java.util.Set;

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
        this.getField().setGroupingSeparatorAlternatives(Set.of('_'));
        this.getField().setDecimalSeparatorAlternatives(Set.of('|'));
        for(String s: new String[]{"123_456|78", "12_34_56|78", "12345_6|78", "_123_456|78", "_123456_|78"}) {
            final Double value = this.getField().parseRawValue(s);
            Assert.assertEquals(Double.valueOf(123456.78), value);
        }
    }

    @Test
    public void testAlternativeSeparatorsWithNegativeSign() throws ParseException {
        this.getField().setLocale(Locale.GERMANY);
        this.getField().setNegativeSignAlternatives(Set.of('^', '%'));
        this.getField().setDecimalSeparatorAlternatives(Set.of('_'));
        for(String s: new String[]{"^123456_78", "%123456_78", "-123456_78"}) {
            final Double value = this.getField().parseRawValue(s);
            Assert.assertEquals(Double.valueOf(-123456.78), value);
        }
    }

}