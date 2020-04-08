package org.vaadin.miki.superfields;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

public class SuperDoubleFieldTest {

    public static final double SIX_DIGITS_PLUS_ONE = 123456.7d;
    public static final String FORMATTED_SIX_DIGITS_PLUS_ONE = "123 456,7"; // there is a NBSP in this string
    public static final String NO_GROUPS_FORMATTED_SIX_DIGITS_PLUS_ONE = "123456,7";

    private SuperDoubleField field;

    @Before
    public void setUp() {
        MockVaadin.setup();
        // this locale uses NBSP ( ) as grouping separator, "," as decimal separator and grouping size of 3
        this.field = new SuperDoubleField(new Locale("pl", "PL"), 5);
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void testValidRegexp() {
        String regexp = this.field.getRegexp();
        // note: the spaces here are actual spaces, because this simulates the input of the user
        for(String s: new String[]{
                "1", "1 ", "1 2", "1 23", "1 234",
                "1 234 5", "1 234 56", "1 234 567",
                "-1 ", "-1 2", "-1 23", "-1 234",
                "-1 234 5", "-1 234 56", "-1 234 567",
                "0", "1", "12", "123", "1234", "1 234", "12 345", "123 456", "12345", "123456",
                "0,", "0,1", "0,12", "0,123", "0,1234", "0,12345",
                "12,", "12,3", "12,34", "12,345", "12,3456", "12,34567",
                "123,", "123,4", "123,45", "123,456", "123,4567", "123,45678",
                "1234,", "1234,5", "1234,56", "1234,567", "1234,5678", "1234,56789",
                "1 234,", "1 234,5", "1 234,56", "1 234,567", "1 234,5678", "1 234,56789",
                "12 345,", "12 345,6", "12 345,67", "12 345,678", "12 345,6789", "12 345,67890",
                "12345,", "12345,6", "12345,67", "12345,678", "12345,6789", "12345,67890",
                "123456,", "123456,7", "123456,78", "123456,789", "123456,7890", "123456,78901",
                "123 456,", "123 456,7", "123 456,78", "123 456,789", "123 456,7890", "123 456,78901",
                "-",
                "-0", "-1", "-12", "-123", "-1234", "-1 234", "-12 345", "-123 456", "-12345", "-123456",
                "-0,", "-0,1", "-0,12", "-0,123", "-0,1234", "-0,12345",
                "-12,", "-12,3", "-12,34", "-12,345", "-12,3456", "-12,34567",
                "-123,", "-123,4", "-123,45", "-123,456", "-123,4567", "-123,45678",
                "-1234,", "-1234,5", "-1234,56", "-1234,567", "-1234,5678", "-1234,56789",
                "-1 234,", "-1 234,5", "-1 234,56", "-1 234,567", "-1 234,5678", "-1 234,56789",
                "-12 345,", "-12 345,6", "-12 345,67", "-12 345,678", "-12 345,6789", "-12 345,67890",
                "-12345,", "-12345,6", "-12345,67", "-12345,678", "-12345,6789", "-12345,67890",
                "-123456,", "-123456,7", "-123456,78", "-123456,789", "-123456,7890", "-123456,78901",
                "-123 456,", "-123 456,7", "-123 456,78", "-123 456,789", "-123 456,7890", "-123 456,78901"
        })
            Assert.assertTrue(String.format("<%s> does not match: /%s/", s, regexp), s.matches(regexp));
    }

    @Test
    public void testValidRegexpLimitTo5() {
        this.field.setMaximumIntegerDigits(5);
        String regexp = this.field.getRegexp();
        // note: the spaces here are actual spaces, because this simulates the input of the user
        for(String s: new String[]{
                "1", "1 ", "1 2", "1 23", "1 234",
                "12 345",
                "-1 ", "-1 2", "-1 23", "-1 234",
                "-12 345",
                "0", "1", "12", "123", "1234", "1 234", "12 345", "12345",
                "0,", "0,1", "0,12", "0,123", "0,1234", "0,12345",
                "12,", "12,3", "12,34", "12,345", "12,3456", "12,34567",
                "123,", "123,4", "123,45", "123,456", "123,4567", "123,45678",
                "1234,", "1234,5", "1234,56", "1234,567", "1234,5678", "1234,56789",
                "1 234,", "1 234,5", "1 234,56", "1 234,567", "1 234,5678", "1 234,56789",
                "12 345,", "12 345,6", "12 345,67", "12 345,678", "12 345,6789", "12 345,67890",
                "12345,", "12345,6", "12345,67", "12345,678", "12345,6789", "12345,67890",
                "-",
                "-0", "-1", "-12", "-123", "-1234", "-1 234", "-12 345", "-12345",
                "-0,", "-0,1", "-0,12", "-0,123", "-0,1234", "-0,12345",
                "-12,", "-12,3", "-12,34", "-12,345", "-12,3456", "-12,34567",
                "-123,", "-123,4", "-123,45", "-123,456", "-123,4567", "-123,45678",
                "-1234,", "-1234,5", "-1234,56", "-1234,567", "-1234,5678", "-1234,56789",
                "-1 234,", "-1 234,5", "-1 234,56", "-1 234,567", "-1 234,5678", "-1 234,56789",
                "-12 345,", "-12 345,6", "-12 345,67", "-12 345,678", "-12 345,6789", "-12 345,67890",
                "-12345,", "-12345,6", "-12345,67", "-12345,678", "-12345,6789", "-12345,67890",
        })
            Assert.assertTrue(String.format("<%s> does not match: /%s/", s, regexp), s.matches(regexp));
    }

    @Test
    public void testValidRegexpLimitTo8() {
        this.field.setMaximumIntegerDigits(8);
        String regexp = this.field.getRegexp();
        // note: the spaces here are actual spaces, because this simulates the input of the user
        for(String s: new String[]{
                "1", "1 ", "1 2", "1 23", "1 234", "1 234 5", "1 234 56", "1 234 567",
                "12 345", "12 345 6", "12 345 67", "12 345 678",
                "-1 ", "-1 2", "-1 23", "-1 234", "-1 234 5", "-1 234 56", "-1 234 567",
                "-12 345", "-12 345 6", "-12 345 67", "-12 345 678",
                "1234567", "12345678", "-1234567", "-12345678",
                "0", "1", "12", "123", "1234", "1 234", "12 345", "12345",
                "0,", "0,1", "0,12", "0,123", "0,1234", "0,12345",
                "12,", "12,3", "12,34", "12,345", "12,3456", "12,34567",
                "123,", "123,4", "123,45", "123,456", "123,4567", "123,45678",
                "1234,", "1234,5", "1234,56", "1234,567", "1234,5678", "1234,56789",
                "1 234,", "1 234,5", "1 234,56", "1 234,567", "1 234,5678", "1 234,56789",
                "12 345,", "12 345,6", "12 345,67", "12 345,678", "12 345,6789", "12 345,67890",
                "12345,", "12345,6", "12345,67", "12345,678", "12345,6789", "12345,67890",
                "-",
                "-0", "-1", "-12", "-123", "-1234", "-1 234", "-12 345", "-12345",
                "-0,", "-0,1", "-0,12", "-0,123", "-0,1234", "-0,12345",
                "-12,", "-12,3", "-12,34", "-12,345", "-12,3456", "-12,34567",
                "-123,", "-123,4", "-123,45", "-123,456", "-123,4567", "-123,45678",
                "-1234,", "-1234,5", "-1234,56", "-1234,567", "-1234,5678", "-1234,56789",
                "-1 234,", "-1 234,5", "-1 234,56", "-1 234,567", "-1 234,5678", "-1 234,56789",
                "-12 345,", "-12 345,6", "-12 345,67", "-12 345,678", "-12 345,6789", "-12 345,67890",
                "-12345,", "-12345,6", "-12345,67", "-12345,678", "-12345,6789", "-12345,67890",
        })
            Assert.assertTrue(String.format("<%s> does not match: /%s/", s, regexp), s.matches(regexp));
    }

    @Test
    public void testInvalidRegexp() {
        String regexp = this.field.getRegexp();
        // again, these are regular spaces, because of testing user input
        for(String s: new String[]{
                "a", "1a", "a1", "a 2", "1 2 3", "1 23 4", "1 23 45", "12 34 56", "12 345 67 89",
                "12345,123456" // there is a limit of 5 fraction digits
        })
            Assert.assertFalse(String.format("<%s> should not match: /%s/", s, regexp), s.matches(regexp));
    }

    @Test
    public void testInvalidRegexpLimitTo5() {
        this.field.setMaximumIntegerDigits(5);
        String regexp = this.field.getRegexp();
        // again, these are regular spaces, because of testing user input
        for(String s: new String[]{
                "1 234 5", "-1 234 5",
                "1 234 56", "1 234 567", "-1 234 56", "-1 234 567", "123 456", "-123456",
                "123456,", "123456,7", "123456,78", "123456,789", "123456,7890", "123456,78901",
                "123 456,", "123 456,7", "123 456,78", "123 456,789", "123 456,7890", "123 456,78901",
                "-123456,", "-123456,7", "-123456,78", "-123456,789", "-123456,7890", "-123456,78901",
                "-123 456,", "-123 456,7", "-123 456,78", "-123 456,789", "-123 456,7890", "-123 456,78901",
                "a", "1a", "a1", "a 2", "1 2 3", "1 23 4", "1 23 45", "12 34 56", "12 345 67 89",
                "12345,123456" // there is a limit of 5 fraction digits
        })
            Assert.assertFalse(String.format("<%s> should not match: /%s/", s, regexp), s.matches(regexp));
    }

    @Test
    public void testInitiallyZero() {
        Assert.assertEquals(0.0d, this.field.getValue(), 0);
        Assert.assertEquals("0", this.field.getRawValue());
    }

    @Test
    public void testFormattingInput() {
        this.field.setValue(SIX_DIGITS_PLUS_ONE);
        Assert.assertEquals(FORMATTED_SIX_DIGITS_PLUS_ONE, this.field.getRawValue());
    }

    @Test
    public void testRemovingGroupingSeparatorOnFocus() {
        this.field.setGroupingSeparatorHiddenOnFocus(true);
        this.field.setValue(SIX_DIGITS_PLUS_ONE);
        this.field.simulateFocus();
        Assert.assertEquals(NO_GROUPS_FORMATTED_SIX_DIGITS_PLUS_ONE, this.field.getRawValue());
        this.field.simulateBlur();
        Assert.assertEquals(FORMATTED_SIX_DIGITS_PLUS_ONE, this.field.getRawValue());
        this.field.setGroupingSeparatorHiddenOnFocus(false);
        this.field.simulateFocus();
        Assert.assertEquals(FORMATTED_SIX_DIGITS_PLUS_ONE, this.field.getRawValue());
    }

    @Test
    public void testAllowingNegativeValueAbsValue() {
        this.field.setValue(-SIX_DIGITS_PLUS_ONE);
        Assert.assertEquals("-"+FORMATTED_SIX_DIGITS_PLUS_ONE, this.field.getRawValue());
        this.field.setNegativeValueAllowed(false);
        Assert.assertEquals(SIX_DIGITS_PLUS_ONE, this.field.getValue(), 0.0001d);
        Assert.assertEquals(FORMATTED_SIX_DIGITS_PLUS_ONE, this.field.getRawValue());
    }

    @Test
    public void testAllowingNegativeValueNoEffectOnPositive() {
        this.field.setValue(SIX_DIGITS_PLUS_ONE);
        Assert.assertEquals(FORMATTED_SIX_DIGITS_PLUS_ONE, this.field.getRawValue());
        this.field.setNegativeValueAllowed(false);
        Assert.assertEquals(SIX_DIGITS_PLUS_ONE, this.field.getValue(), 0.0001d);
        Assert.assertEquals(FORMATTED_SIX_DIGITS_PLUS_ONE, this.field.getRawValue());
    }

    @Test
    public void testMinimumFractionDigits() {
        this.field.setValue(SIX_DIGITS_PLUS_ONE);
        this.field.setMinimumFractionDigits(3);
        Assert.assertEquals(FORMATTED_SIX_DIGITS_PLUS_ONE+"00", this.field.getRawValue());
        Assert.assertEquals(SIX_DIGITS_PLUS_ONE, this.field.getValue(), 0.0001d);
    }

    @Test
    public void testMaximumFractionDigits() {
        this.field.setValue(1.234d);
        this.field.setMaximumFractionDigits(1);
        Assert.assertEquals("1,2", this.field.getRawValue());
        Assert.assertEquals(1.234, this.field.getValue(), 0);
    }

    // bug report: https://github.com/vaadin-miki/super-fields/issues/10
    @Test
    public void testIntegerLengthMultiplicationOfGroup() {
        this.field.setMaximumIntegerDigits(9);
        String regexp = this.field.getRegexp();
        for(String s: new String[]{"1234567890", "12345678901", "123456789012", "123 456 789 0", "123 456 789 01", "123 456 789 012"})
            Assert.assertFalse(String.format("%s must not match %s", regexp, s), s.matches(regexp));
    }

}