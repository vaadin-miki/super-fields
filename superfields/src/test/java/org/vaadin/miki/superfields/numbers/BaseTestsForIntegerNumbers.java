package org.vaadin.miki.superfields.numbers;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.shared.Registration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Base class containing tests for integer parts of number fields.
 * This is not a test class, rather a superclass for test classes.
 */
@SuppressWarnings("squid:S3577") // this is ok, the tests are inherited
class BaseTestsForIntegerNumbers<T extends Number> {

    public static final class NumberWrapper<X extends Number> {
        private X number;

        public X getNumber() {
            return number;
        }

        public void setNumber(X number) {
            this.number = number;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NumberWrapper<?> that = (NumberWrapper<?>) o;
            return Objects.equals(getNumber(), that.getNumber());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getNumber());
        }
    }

    public static final class NumberValidator<X extends Number> implements Validator<X> {

        private X referenceValue;

        @Override
        public ValidationResult apply(X x, ValueContext valueContext) {
            return Objects.equals(x, this.referenceValue) ? ValidationResult.ok() : ValidationResult.error("nope");
        }

        public X getReferenceValue() {
            return referenceValue;
        }

        public void setReferenceValue(X referenceValue) {
            this.referenceValue = referenceValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NumberValidator<?> that = (NumberValidator<?>) o;
            return Objects.equals(getReferenceValue(), that.getReferenceValue());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getReferenceValue());
        }
    }

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

    protected AbstractSuperNumberField<T, ?> getField() {
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

    @Test
    public void testChangesInLocaleDoNotAffectPrecision() {
        this.field.setMaximumIntegerDigits(4);
        final int maxFraction = this.field.getMaximumFractionDigits();
        final int minFraction = this.field.getMinimumFractionDigits();
        final int maxDigits = this.field.getMaximumIntegerDigits();
        this.field.setLocale(Locale.GERMANY); // ticket #224 - any change in locale would trigger this
        Assert.assertEquals("max fraction digits must not change when changing locale", maxFraction, this.field.getMaximumFractionDigits());
        Assert.assertEquals("min fraction digits must not change when changing locale", minFraction, this.field.getMinimumFractionDigits());
        Assert.assertEquals("max integer digits must not change when changing locale", maxDigits, this.field.getMaximumIntegerDigits());
    }

    @Test
    public void testBinderAndValidation() {
        final NumberWrapper<T> wrapper = new NumberWrapper<>();
        @SuppressWarnings("unchecked")
        final Binder<NumberWrapper<T>> binder = new Binder<>((Class<NumberWrapper<T>>)(Class<?>)NumberWrapper.class);
        final NumberValidator<T> validator = new NumberValidator<>();
        validator.setReferenceValue(this.baseTestNumber);
        this.field.setNegativeValueAllowed(true);
        binder.forField(this.field)
                .withValidator(validator)
                .bind("number");
        binder.setBean(wrapper);
        Assert.assertFalse("validator must fail with no number", binder.isValid());
        wrapper.setNumber(this.baseTestNumber);
        binder.setBean(wrapper);
        Assert.assertTrue("validator must not fail with the correct number", binder.isValid());
        wrapper.setNumber(this.negativeTestNumber);
        binder.setBean(wrapper);
        Assert.assertFalse("validator must fail with wrong number", binder.isValid());

        this.field.setValue(this.baseTestNumber);
        Assert.assertTrue("validator must be ok after field was changed to good number", binder.isValid());

        validator.setReferenceValue(this.negativeTestNumber);
        Assert.assertFalse("validator must not be ok after it was changed", binder.isValid());
        validator.setReferenceValue(this.baseTestNumber);
        this.field.setValue(this.baseTestNumber);
        Assert.assertTrue("validator must be ok", binder.isValid());

    }

    // checks for regressions of #284
    @Test
    public void testEmptyStringParsesProperlyWhenNullAllowed() throws ParseException {
        this.field.setNullValueAllowed(true);
        final T value = this.field.parseRawValue("");
        Assert.assertNull(value);
    }

    @Test
    public void testEmptyStringParsesProperlyWhenNullNotAllowed() throws ParseException {
        this.field.setNullValueAllowed(false);
        final T value = this.field.parseRawValue("");
        Assert.assertNotNull(value);
    }

    // tests for #472
    @Test
    public void testNoMixingOfGroupingSymbolsAllowed() {
        this.field.setLocale(Locale.ENGLISH); // this uses . as decimal and , as grouping
        this.field.setGroupingSeparatorAlternatives(Set.of('.', '-')); // so setting . should fail (and - is minus, also fail)
        Assert.assertTrue(this.field.getGroupingSeparatorAlternatives().isEmpty());
        this.field.setLocale(new Locale("pl", "PL")); // this uses NBSP, so space must always be there
        this.field.setGroupingSeparatorAlternatives(Set.of('_'));
        Assert.assertEquals(2, this.field.getGroupingSeparatorAlternatives().size());
        Assert.assertTrue(this.field.getGroupingSeparatorAlternatives().containsAll(Set.of(' ', '_')));
    }

    @Test
    public void testNoMixingOfNegativeSignAllowed() {
        this.field.setLocale(Locale.ENGLISH);
        this.field.setNegativeSignAlternatives(Set.of('.', ','));
        Assert.assertTrue(this.field.getGroupingSeparatorAlternatives().isEmpty());
        this.field.setNegativeSignAlternatives(Set.of('%', '_', '-', '+')); // yes, + is ok for negatives :D
        Assert.assertEquals(3, this.field.getNegativeSignAlternatives().size());
        Assert.assertTrue(this.field.getNegativeSignAlternatives().containsAll(Set.of('+', '_', '%')));
    }

    @Test
    public void testAlternativesOverlappingDisallowed() {
        this.field.setLocale(Locale.ENGLISH);
        this.field.withOverlappingAlternatives()
            .withNegativeSignAlternatives('.');
        Assert.assertTrue(this.field.getNegativeSignAlternatives().contains('.'));
        // disallowing overlapping should remove overlaps
        this.field.setOverlappingAlternatives(false);
        Assert.assertTrue(this.field.getNegativeSignAlternatives().isEmpty());
    }

}
