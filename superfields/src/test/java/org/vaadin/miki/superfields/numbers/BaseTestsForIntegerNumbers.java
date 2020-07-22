package org.vaadin.miki.superfields.numbers;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.shared.Registration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Base class containing tests for integer parts of number fields.
 * This is not a test class, rather a superclass for test classes.
 */
class BaseTestsForIntegerNumbers<T extends Number> {

    private final Supplier<AbstractSuperNumberField<T, ?>> fieldSupplier;
    private final T baseTestNumber;
    private final T negativeTestNumber;
    private final String numberWithGroups;
    private final String numberWithoutGroups;
    private final T zero;

    private final Set<String> validOutOfTheBoxInputs = new HashSet<>();
    private final Set<String> invalidOutOfTheBoxInputs = new HashSet<>();

    private final Map<Integer, Set<String>> validLimitedInputs = new HashMap<>();
    private final Map<Integer, Set<String>> invalidLimitedInputs = new HashMap<>();

    private AbstractSuperNumberField<T, ?> field;

    private boolean eventFlag = false;

    public BaseTestsForIntegerNumbers(Supplier<AbstractSuperNumberField<T, ?>> fieldSupplier, T baseTestNumber, T negativeTestNumber, String numberWithGroups, String numberWithoutGroups, T zero) {
        this.fieldSupplier = fieldSupplier;
        this.baseTestNumber = baseTestNumber;
        this.negativeTestNumber = negativeTestNumber;
        this.numberWithGroups = numberWithGroups;
        this.numberWithoutGroups = numberWithoutGroups;
        this.zero = zero;

        this.validInputs("1", "1 ", "1 2", "1 23", "1 234",
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
        this.validInputs(3, "1", "12", "123");
        this.invalidInputs(3, "1 ", "1 2", "1 23", "12 3", "1234");
    }

    protected final AbstractSuperNumberField<T, ?> getField() {
        return this.field;
    }

    protected final void validInputs(String... inputs) {
        this.validOutOfTheBoxInputs.addAll(Arrays.asList(inputs));
    }

    protected final void invalidInputs(String... inputs) {
        this.invalidOutOfTheBoxInputs.addAll(Arrays.asList(inputs));
    }

    protected final void validInputs(Integer maxDigitsLimit, String... inputs) {
        if(!this.validLimitedInputs.containsKey(maxDigitsLimit))
            this.validLimitedInputs.put(maxDigitsLimit, new HashSet<>());
        this.validLimitedInputs.get(maxDigitsLimit).addAll(Arrays.asList(inputs));
    }

    protected final void invalidInputs(Integer maxDigitsLimit, String... inputs) {
        if(!this.invalidLimitedInputs.containsKey(maxDigitsLimit))
            this.invalidLimitedInputs.put(maxDigitsLimit, new HashSet<>());
        this.invalidLimitedInputs.get(maxDigitsLimit).addAll(Arrays.asList(inputs));
    }

    @Before
    public void setUp() {
        MockVaadin.setup();
        this.field = this.fieldSupplier.get();
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void testValidOutOfTheBoxInputs() {
        Assert.assertTrue("no testable inputs that are valid, cannot continue!", this.validOutOfTheBoxInputs.size() > 0);
        String regexp = this.field.getRegexp();
        this.validOutOfTheBoxInputs.forEach(s -> Assert.assertTrue(String.format("input %s must match %s for %s", s, regexp, this.field.getClass().getSimpleName()), s.matches(regexp)));
    }

    @Test
    public void testInvalidOutOfTheBoxInputs() {
        Assert.assertTrue("no testable inputs that are invalid, cannot continue!", this.invalidOutOfTheBoxInputs.size() > 0);
        String regexp = this.field.getRegexp();
        this.invalidOutOfTheBoxInputs.forEach(s -> Assert.assertFalse(String.format("input %s must not match %s for %s", s, regexp, this.field.getClass().getSimpleName()), s.matches(regexp)));
    }

    @Test
    public void testValidLimitedInputs() {
        Assert.assertTrue("no testable limited length inputs that are valid, cannot continue!", this.validLimitedInputs.size() > 0);
        this.validLimitedInputs.forEach((limit, inputs) -> {
            this.field.setMaximumIntegerDigits(limit);
            String regexp = this.field.getRegexp();
            inputs.forEach(s -> Assert.assertTrue(String.format("input %s must match %s for %s with integer limit %d", s, regexp, this.field.getClass().getSimpleName(), limit), s.matches(regexp)));
        });
    }

    @Test
    public void testInvalidLimitedInputs() {
        Assert.assertTrue("no testable limited length inputs that are invalid, cannot continue!", this.invalidLimitedInputs.size() > 0);
        this.invalidLimitedInputs.forEach((limit, inputs) -> {
            this.field.withMaximumIntegerDigits(limit);
            String regexp = this.field.getRegexp();
            inputs.forEach(s -> Assert.assertFalse(String.format("input %s must not match %s for %s with integer limit %d", s, regexp, this.field.getClass().getSimpleName(), limit), s.matches(regexp)));
        });
    }

    @Test
    public void testInitiallyZero() {
        Assert.assertEquals(this.zero, this.field.getValue());
        Assert.assertEquals("0", this.field.getRawValue());
    }

    @Test
    public void testFormattingInput() {
        this.field.setValue(this.baseTestNumber);
        Assert.assertEquals(this.numberWithGroups, this.field.getRawValue());
    }

    @Test
    public void testRemovingGroupingSeparatorOnFocus() {
        this.field.setGroupingSeparatorHiddenOnFocus(true);
        this.field.setValue(this.baseTestNumber);
        this.field.simulateFocus();
        Assert.assertEquals(this.numberWithoutGroups, this.field.getRawValue());
        this.field.simulateBlur();
        Assert.assertEquals(this.numberWithGroups, this.field.getRawValue());
        this.field.setGroupingSeparatorHiddenOnFocus(false);
        this.field.simulateFocus();
        Assert.assertEquals(this.numberWithGroups, this.field.getRawValue());
    }

    @Test
    public void testAllowingNegativeValueAbsValue() {
        this.field.setValue(this.negativeTestNumber);
        Assert.assertEquals("-"+ this.numberWithGroups, this.field.getRawValue());
        this.field.setNegativeValueAllowed(false);
        Assert.assertEquals(this.baseTestNumber, this.field.getValue());
        Assert.assertEquals(this.numberWithGroups, this.field.getRawValue());
    }

    @Test
    public void testAllowingNegativeValueNoEffectOnPositive() {
        this.field.setValue(this.baseTestNumber);
        Assert.assertEquals(this.numberWithGroups, this.field.getRawValue());
        this.field.setNegativeValueAllowed(false);
        Assert.assertEquals(this.baseTestNumber, this.field.getValue());
        Assert.assertEquals(this.numberWithGroups, this.field.getRawValue());
    }

    // bug report: https://github.com/vaadin-miki/super-fields/issues/10
    @Test
    public void testIntegerLengthMultiplicationOfGroup() {
        this.getField().setMaximumIntegerDigits(9); // grouping size is 3, so 9 is a multiplication of it
        String regexp = this.getField().getRegexp();
        for(String s: new String[]{"1234567890", "12345678901", "123456789012", "123 456 789 0", "123 456 789 01", "123 456 789 012"})
            Assert.assertFalse(String.format("%s must not match %s (regression on bug #10)", regexp, s), s.matches(regexp));
    }

    @Test
    public void testNullWithNullAllowed() {
        this.getField().setNullValueAllowed(true);
        this.getField().setValue(null);
        T value = this.getField().getValue();
        Assert.assertNull("value was set to null and it must be null", value);
        Assert.assertTrue("null representation must be an empty string", this.getField().getRawValue().isEmpty());
    }

    @Test
    public void testNullWithNullAllowedAndNegativeNotAllowed() {
        this.getField().setNullValueAllowed(true);
        this.getField().setNegativeValueAllowed(false);
        this.getField().setValue(null);
        T value = this.getField().getValue();
        Assert.assertNull("value was set to null and it must be null", value);
        Assert.assertTrue("null representation must be an empty string", this.getField().getRawValue().isEmpty());
    }

    @Test
    public void testNullWithNullAllowedAndNegativeAllowed() {
        this.getField().setNullValueAllowed(true);
        this.getField().setNegativeValueAllowed(true);
        this.getField().setValue(null);
        T value = this.getField().getValue();
        Assert.assertNull("value was set to null and it must be null", value);
        Assert.assertTrue("null representation must be an empty string", this.getField().getRawValue().isEmpty());
    }

    @Test
    public void testNullAllowedSetNullThenChangeRegexp() {
        this.getField().setNullValueAllowed(true);
        this.getField().setValue(null);
        this.getField().setNegativeValueAllowed(!this.getField().isNegativeValueAllowed());
        T value = this.getField().getValue();
        Assert.assertNull("value was set to null and it must be null", value);
        Assert.assertTrue("null representation must be an empty string", this.getField().getRawValue().isEmpty());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testNullWithNoNullAllowedThrowsException() {
        // by default, there should be no allowance for null values
        this.getField().setValue(null);
    }

    private <E extends ComponentEvent<?>> void checkEventTriggered(Function<ComponentEventListener<E>, Registration> addEvent, Consumer<AbstractSuperNumberField<T, ?>> fireEvent) {
        this.eventFlag = false;
        Registration registration = addEvent.apply(event -> this.eventFlag = true);
        fireEvent.accept(this.getField());
        Assert.assertTrue(this.eventFlag);
        this.eventFlag = false;
        registration.remove();
        this.getField().simulateFocus();
        Assert.assertFalse(this.eventFlag);
    }

    @Test
    public void testFocus() {
        this.checkEventTriggered(this.getField()::addFocusListener, AbstractSuperNumberField::simulateFocus);
    }

    @Test
    public void testBlur() {
        this.checkEventTriggered(this.getField()::addBlurListener, AbstractSuperNumberField::simulateBlur);
    }

}
