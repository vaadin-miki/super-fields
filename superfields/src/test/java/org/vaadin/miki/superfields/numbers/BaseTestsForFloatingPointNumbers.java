package org.vaadin.miki.superfields.numbers;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * This is not a test class, rather a base class containing tests for floating-point number tests.
 * All floating point tests include also integer tests.
 */
class BaseTestsForFloatingPointNumbers<T extends Number> extends BaseTestsForIntegerNumbers<T> {

    private final Map<Integer[], Set<String>> validLimitedInputs = new HashMap<>();
    private final Map<Integer[], Set<String>> invalidLimitedInputs = new HashMap<>();

    public BaseTestsForFloatingPointNumbers(Supplier<AbstractSuperNumberField<T, ?>> abstractSuperNumberFieldSupplier, T baseTestNumber, T negativeTestNumber, String numberWithGroups, String numberWithoutGroups, T zero) {
        super(abstractSuperNumberFieldSupplier, baseTestNumber, negativeTestNumber, numberWithGroups, numberWithoutGroups, zero);
        this.validInputs(
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
        );
        this.validInputs(5,
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
                "-12345,", "-12345,6", "-12345,67", "-12345,678", "-12345,6789", "-12345,67890"
        );
        this.validInputs(8,
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
                "-12345,", "-12345,6", "-12345,67", "-12345,678", "-12345,6789", "-12345,67890"
        );
        this.invalidInputs(
                "a", "1a", "a1", "a 2", "1 2 3", "1 23 4", "1 23 45", "12 34 56", "12 345 67 89"
        );
        this.invalidInputs(5,
                "1 234 5", "-1 234 5",
                "1 234 56", "1 234 567", "-1 234 56", "-1 234 567", "123 456", "-123456",
                "123456,", "123456,7", "123456,78", "123456,789", "123456,7890", "123456,78901",
                "123 456,", "123 456,7", "123 456,78", "123 456,789", "123 456,7890", "123 456,78901",
                "-123456,", "-123456,7", "-123456,78", "-123456,789", "-123456,7890", "-123456,78901",
                "-123 456,", "-123 456,7", "-123 456,78", "-123 456,789", "-123 456,7890", "-123 456,78901",
                "a", "1a", "a1", "a 2", "1 2 3", "1 23 4", "1 23 45", "12 34 56", "12 345 67 89",
                "12345,123456" // there is a limit of 5 fraction digits
        );
        this.validInputs(3, 0, 2, "1", "1,", "1,23", "12,", "12,3", "123,45");
        this.invalidInputs(3, 0, 0, "1234", "1,", "12,1");
    }

    protected final void validInputs(Integer integerLimit, Integer minDecimalLimit, Integer maxDecimalLimit, String... inputs) {
        Integer[] limits = new Integer[]{integerLimit, minDecimalLimit, maxDecimalLimit};
        if(!this.validLimitedInputs.containsKey(limits))
            this.validLimitedInputs.put(limits, new HashSet<>());
        this.validLimitedInputs.get(limits).addAll(Arrays.asList(inputs));
    }

    protected final void invalidInputs(Integer integerLimit, Integer minDecimalLimit, Integer maxDecimalLimit, String... inputs) {
        Integer[] limits = new Integer[]{integerLimit, minDecimalLimit, maxDecimalLimit};
        if(!this.invalidLimitedInputs.containsKey(limits))
            this.invalidLimitedInputs.put(limits, new HashSet<>());
        this.invalidLimitedInputs.get(limits).addAll(Arrays.asList(inputs));
    }

    @Test
    public void testValidLimitedFloatingPointInputs() {
        Assert.assertTrue("no testable limited length floating point inputs that are valid, cannot continue!", this.validLimitedInputs.size() > 0);
        this.validLimitedInputs.forEach((limits, inputs) -> {
            this.getField().setMaximumIntegerDigits(limits[0]);
            this.getField().setMinimumFractionDigits(limits[1]);
            this.getField().setMaximumFractionDigits(limits[2]);
            String regexp = this.getField().getRegexp();
            inputs.forEach(s -> Assert.assertTrue(String.format("input %s must match %s for %s with integer limit %d and decimal limits %d/%d", s, regexp, this.getField().getClass().getSimpleName(), limits[0], limits[1], limits[2]), s.matches(regexp)));
        });
    }

    @Test
    public void testInvalidLimitedFloatingPointInputs() {
        Assert.assertTrue("no testable limited length floating point inputs that are invalid, cannot continue!", this.invalidLimitedInputs.size() > 0);
        this.invalidLimitedInputs.forEach((limits, inputs) -> {
            this.getField().setMaximumIntegerDigits(limits[0]);
            this.getField().setMinimumFractionDigits(limits[1]);
            this.getField().setMaximumFractionDigits(limits[2]);
            String regexp = this.getField().getRegexp();
            inputs.forEach(s -> Assert.assertFalse(String.format("input %s must not match %s for %s with integer limit %d and decimal limits %d/%d", s, regexp, this.getField().getClass().getSimpleName(), limits[0], limits[1], limits[2]), s.matches(regexp)));
        });
    }

    @Test
    public void testIntegerPartRequired() {
        final String[] onlyWhenNotRequired = new String[] {
                ",", ",2", ",25", "-,", "-,2", "-,27"
        };
        this.getField().setMaximumFractionDigits(3);
        this.getField().setMinimumFractionDigits(0);
        Arrays.asList(2, 3, 5, 7, 9, 11).forEach(maxDigits -> {
                    this.getField().setMaximumIntegerDigits(maxDigits);
                    this.getField().setIntegerPartRequired(true);
                    // integer part is required by default, so ALL entries should be incorrect
                    final String regexp = this.getField().getRegexp();
                    for (String s : onlyWhenNotRequired)
                        Assert.assertFalse(String.format("input %s must not match %s when integer part (max size %d) is required", s, regexp, maxDigits), s.matches(regexp));

                    this.getField().setIntegerPartRequired(false);
                    for (String s : onlyWhenNotRequired)
                        Assert.assertTrue(String.format("input %s must match %s when integer part (max size %d) is optional", s, regexp, maxDigits), s.matches(regexp));
                }
        );
    }
}
