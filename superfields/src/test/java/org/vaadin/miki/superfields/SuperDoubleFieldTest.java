package org.vaadin.miki.superfields;

import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertEquals(1.234, this.getField().getValue(), 0);
    }

}