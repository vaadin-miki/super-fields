package org.vaadin.miki.superfields.numbers;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Locale;

public class SuperBigDecimalFieldTest extends BaseTestsForFloatingPointNumbers<BigDecimal> {

    public static final BigDecimal TEN_DIGITS_PLUS_TWO = BigDecimal.valueOf(123456789012L, 2);
    public static final String FORMATTED_TEN_DIGITS_PLUS_TWO = "1 234 567 890,12"; // there are NBSPs in this string
    public static final String NO_GROUPS_FORMATTED_TEN_DIGITS_PLUS_TWO = "1234567890,12";

    public SuperBigDecimalFieldTest() {
        super(
                ()->new SuperBigDecimalField(new Locale("pl", "PL"), 5),
                TEN_DIGITS_PLUS_TWO, TEN_DIGITS_PLUS_TWO.negate(), FORMATTED_TEN_DIGITS_PLUS_TWO, NO_GROUPS_FORMATTED_TEN_DIGITS_PLUS_TWO, BigDecimal.ZERO
        );
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
    }

}