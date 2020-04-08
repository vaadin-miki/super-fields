package org.vaadin.miki.superfields;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

public class SuperIntegerFieldTest {

    public static final Integer EIGHT_DIGITS = 12345678;
    public static final String FORMATTED_EIGHT_DIGITS = "12 345 678"; // there are NBSPs in this string
    public static final String NO_GROUPS_FORMATTED_EIGHT_DIGITS = "12345678";

    private SuperIntegerField field;

    @Before
    public void setUp() {
        MockVaadin.setup();
        // this locale uses NBSP ( ) as grouping separator, "," as decimal separator and grouping size of 3
        this.field = new SuperIntegerField(new Locale("pl", "PL"));
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
                "-",
                "-0", "-1", "-12", "-123", "-1234", "-1 234", "-12 345", "-123 456", "-12345", "-123456",
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
                "-",
                "-0", "-1", "-12", "-123", "-1234", "-1 234", "-12 345", "-12345",
        })
            Assert.assertTrue(String.format("<%s> does not match: /%s/", s, regexp), s.matches(regexp));
    }

    @Test
    public void testInvalidRegexp() {
        String regexp = this.field.getRegexp();
        // again, these are regular spaces, because of testing user input
        for(String s: new String[]{
                "0,", "0,1", "0,12", "0,123", "0,1234", "0,12345",
                "12,", "12,3", "12,34", "12,345", "12,3456", "12,34567",
                "123,", "123,4", "123,45", "123,456", "123,4567", "123,45678",
                "1234,", "1234,5", "1234,56", "1234,567", "1234,5678", "1234,56789",
                "1 234,", "1 234,5", "1 234,56", "1 234,567", "1 234,5678", "1 234,56789",
                "12 345,", "12 345,6", "12 345,67", "12 345,678", "12 345,6789", "12 345,67890",
                "12345,", "12345,6", "12345,67", "12345,678", "12345,6789", "12345,67890",
                "123456,", "123456,7", "123456,78", "123456,789", "123456,7890", "123456,78901",
                "123 456,", "123 456,7", "123 456,78", "123 456,789", "123 456,7890", "123 456,78901",
                "-0,", "-0,1", "-0,12", "-0,123", "-0,1234", "-0,12345",
                "-12,", "-12,3", "-12,34", "-12,345", "-12,3456", "-12,34567",
                "-123,", "-123,4", "-123,45", "-123,456", "-123,4567", "-123,45678",
                "-1234,", "-1234,5", "-1234,56", "-1234,567", "-1234,5678", "-1234,56789",
                "-1 234,", "-1 234,5", "-1 234,56", "-1 234,567", "-1 234,5678", "-1 234,56789",
                "-12 345,", "-12 345,6", "-12 345,67", "-12 345,678", "-12 345,6789", "-12 345,67890",
                "-12345,", "-12345,6", "-12345,67", "-12345,678", "-12345,6789", "-12345,67890",
                "-123456,", "-123456,7", "-123456,78", "-123456,789", "-123456,7890", "-123456,78901",
                "-123 456,", "-123 456,7", "-123 456,78", "-123 456,789", "-123 456,7890", "-123 456,78901",
                "a", "1a", "a1", "a 2", "1 2 3", "1 23 4", "1 23 45", "12 34 56", "12 345 67 89",
                "12345,123456"
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
                "1,", "1 2,", "12,", "123,", "1234,", "12345,",
                "123456,", "123456,7", "123456,78", "123456,789", "123456,7890", "123456,78901",
                "123 456,", "123 456,7", "123 456,78", "123 456,789", "123 456,7890", "123 456,78901",
                "-123456,", "-123456,7", "-123456,78", "-123456,789", "-123456,7890", "-123456,78901",
                "-123 456,", "-123 456,7", "-123 456,78", "-123 456,789", "-123 456,7890", "-123 456,78901",
                "a", "1a", "a1", "a 2", "1 2 3", "1 23 4", "1 23 45", "12 34 56", "12 345 67 89",
        })
            Assert.assertFalse(String.format("<%s> should not match: /%s/", s, regexp), s.matches(regexp));
    }

    @Test
    public void testInitiallyZero() {
        Assert.assertEquals(Integer.valueOf(0), this.field.getValue());
        Assert.assertEquals("0", this.field.getRawValue());
    }

    @Test
    public void testFormattingInput() {
        this.field.setValue(EIGHT_DIGITS);
        Assert.assertEquals(FORMATTED_EIGHT_DIGITS, this.field.getRawValue());
    }

    @Test
    public void testRemovingGroupingSeparatorOnFocus() {
        this.field.setGroupingSeparatorHiddenOnFocus(true);
        this.field.setValue(EIGHT_DIGITS);
        this.field.simulateFocus();
        Assert.assertEquals(NO_GROUPS_FORMATTED_EIGHT_DIGITS, this.field.getRawValue());
        this.field.simulateBlur();
        Assert.assertEquals(FORMATTED_EIGHT_DIGITS, this.field.getRawValue());
        this.field.setGroupingSeparatorHiddenOnFocus(false);
        this.field.simulateFocus();
        Assert.assertEquals(FORMATTED_EIGHT_DIGITS, this.field.getRawValue());
    }

    @Test
    public void testAllowingNegativeValueAbsValue() {
        this.field.setValue(-EIGHT_DIGITS);
        Assert.assertEquals("-"+ FORMATTED_EIGHT_DIGITS, this.field.getRawValue());
        this.field.setNegativeValueAllowed(false);
        Assert.assertEquals(EIGHT_DIGITS, this.field.getValue());
        Assert.assertEquals(FORMATTED_EIGHT_DIGITS, this.field.getRawValue());
    }

    @Test
    public void testAllowingNegativeValueNoEffectOnPositive() {
        this.field.setValue(EIGHT_DIGITS);
        Assert.assertEquals(FORMATTED_EIGHT_DIGITS, this.field.getRawValue());
        this.field.setNegativeValueAllowed(false);
        Assert.assertEquals(EIGHT_DIGITS, this.field.getValue());
        Assert.assertEquals(FORMATTED_EIGHT_DIGITS, this.field.getRawValue());
    }

}