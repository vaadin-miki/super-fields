package org.vaadin.miki.superfields.numbers;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;

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
    }

    @Override
    protected SuperBigDecimalField getField() {
        return (SuperBigDecimalField) super.getField();
    }

    @Test
    public void testInputsWithScientificNotationTurnedOn() {
        this.getField().setMaximumExponentDigits(3);
        this.testInvalidOutOfTheBoxInputs();
        this.testValidOutOfTheBoxInputs();
        this.testValidLimitedInputs();
    }

    @Test
    public void testScientificNotationSupported() throws ParseException {
        this.getField().setMaximumExponentDigits(3);
        for(Map.Entry<String, BigDecimal> entry: SCI_NOTATION.entrySet()) {
            for(int zmp1 = 0; zmp1<entry.getKey().length(); zmp1++)
                Assert.assertTrue("scientific string "+entry.getKey()+" should properly parse as input in step "+zmp1+" of "+entry.getKey().length(), entry.getKey().substring(0, zmp1).matches(this.getField().getRegexp()));
            Assert.assertEquals("string "+entry.getKey()+" should be parsed as "+entry.getValue().toEngineeringString(), 0, entry.getValue().compareTo(this.getField().parseRawValue(entry.getKey())));
        }
    }

}