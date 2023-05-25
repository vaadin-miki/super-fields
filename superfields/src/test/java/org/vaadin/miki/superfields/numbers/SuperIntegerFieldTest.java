package org.vaadin.miki.superfields.numbers;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Locale;
import java.util.Set;

public class SuperIntegerFieldTest extends BaseTestsForIntegerNumbers<Integer> {

    public static final Integer EIGHT_DIGITS = 12345678;
    public static final String FORMATTED_EIGHT_DIGITS = "12 345 678"; // there are NBSPs in this string
    public static final String NO_GROUPS_FORMATTED_EIGHT_DIGITS = "12345678";

    public SuperIntegerFieldTest() {
        // this locale uses NBSP ( ) as grouping separator, "," as decimal separator and grouping size of 3
        super(()->new SuperIntegerField(new Locale("pl", "PL")), EIGHT_DIGITS, -EIGHT_DIGITS, FORMATTED_EIGHT_DIGITS, NO_GROUPS_FORMATTED_EIGHT_DIGITS, 0);
        this.invalidInputs("0,", "0,1", "0,12", "0,123", "0,1234", "0,12345",
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
        );
        this.invalidInputs(5,
                "1 234 5", "-1 234 5",
                "1 234 56", "1 234 567", "-1 234 56", "-1 234 567", "123 456", "-123456",
                "1,", "1 2,", "12,", "123,", "1234,", "12345,",
                "123456,", "123456,7", "123456,78", "123456,789", "123456,7890", "123456,78901",
                "123 456,", "123 456,7", "123 456,78", "123 456,789", "123 456,7890", "123 456,78901",
                "-123456,", "-123456,7", "-123456,78", "-123456,789", "-123456,7890", "-123456,78901",
                "-123 456,", "-123 456,7", "-123 456,78", "-123 456,789", "-123 456,7890", "-123 456,78901",
                "a", "1a", "a1", "a 2", "1 2 3", "1 23 4", "1 23 45", "12 34 56", "12 345 67 89"
        );
    }

    @Test
    public void testAlternativeGroupingSeparators() throws ParseException {
        this.getField().setLocale(Locale.FRANCE);
        this.getField().setGroupingSeparatorAlternatives(Set.of('_'));
        for(String s: new String[]{"123_456", "12_34_56", "12345_6", "_123_456", "_123456_"}) {
            final Integer value = this.getField().parseRawValue(s);
            Assert.assertEquals(Integer.valueOf(123456), value);
        }
    }

    @Test
    public void testAlternativeNegativeSign() throws ParseException {
        this.getField().setLocale(Locale.GERMANY);
        this.getField().setNegativeSignAlternatives(Set.of('^', '%'));
        for(String s: new String[]{"^123456", "%123456", "-123456"}) {
            final Integer value = this.getField().parseRawValue(s);
            Assert.assertEquals(Integer.valueOf(-123456), value);
        }
    }

}