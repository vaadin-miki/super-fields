package org.vaadin.miki.superfields;

import java.util.Locale;

public class SuperLongFieldTest extends BaseTestsForIntegerNumbers<Long> {

    public static final Long TWELVE_DIGITS = 123456789012L;
    public static final String FORMATTED_TWELVE_DIGITS = "123 456 789 012"; // there are NBSPs in this string
    public static final String NO_GROUPS_FORMATTED_TWELVE_DIGITS = "123456789012";

    public SuperLongFieldTest() {
        super(
                ()->new SuperLongField(new Locale("pl", "PL")),
                TWELVE_DIGITS, -TWELVE_DIGITS, FORMATTED_TWELVE_DIGITS, NO_GROUPS_FORMATTED_TWELVE_DIGITS, 0L
        );

        this.validInputs(
                "1", "1 ", "1 2", "1 23", "1 234",
                "1 234 5", "1 234 56", "1 234 567",
                "-1 ", "-1 2", "-1 23", "-1 234",
                "-1 234 5", "-1 234 56", "-1 234 567",
                "0", "1", "12", "123", "1234", "1 234", "12 345", "123 456", "12345", "123456",
                "-",
                "-0", "-1", "-12", "-123", "-1234", "-1 234", "-12 345", "-123 456", "-12345", "-123456"
        );
        this.validInputs(5,
                "1", "1 ", "1 2", "1 23", "1 234",
                "12 345",
                "-1 ", "-1 2", "-1 23", "-1 234",
                "-12 345",
                "0", "1", "12", "123", "1234", "1 234", "12 345", "12345",
                "-",
                "-0", "-1", "-12", "-123", "-1234", "-1 234", "-12 345", "-12345"
        );
        this.invalidInputs(
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

}
