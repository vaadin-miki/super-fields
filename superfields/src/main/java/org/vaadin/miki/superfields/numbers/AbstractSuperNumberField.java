package org.vaadin.miki.superfields.numbers;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.FocusNotifier;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.HasPrefixAndSuffix;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.shared.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.miki.events.text.TextSelectionEvent;
import org.vaadin.miki.events.text.TextSelectionListener;
import org.vaadin.miki.events.text.TextSelectionNotifier;
import org.vaadin.miki.markers.CanReceiveSelectionEventsFromClient;
import org.vaadin.miki.markers.CanSelectText;
import org.vaadin.miki.markers.WithClearButtonMixin;
import org.vaadin.miki.markers.WithHelperMixin;
import org.vaadin.miki.markers.WithHelperPositionableMixin;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithLabelPositionableMixin;
import org.vaadin.miki.markers.WithLocaleMixin;
import org.vaadin.miki.markers.WithNullValueOptionallyAllowedMixin;
import org.vaadin.miki.markers.WithPlaceholderMixin;
import org.vaadin.miki.markers.WithReceivingSelectionEventsFromClientMixin;
import org.vaadin.miki.markers.WithRequiredMixin;
import org.vaadin.miki.markers.WithTooltipMixin;
import org.vaadin.miki.markers.WithValueMixin;
import org.vaadin.miki.shared.labels.LabelPosition;
import org.vaadin.miki.superfields.text.SuperTextField;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Base class for super number fields.
 *
 * @param <T> Type of number supported by the field.
 * @author miki
 * @since 2020-04-07
 */
@CssImport("./styles/form-layout-number-field-styles.css")
//@CssImport(value = "./styles/label-positions.css", themeFor = "super-text-field")
@SuppressWarnings("squid:S119") // SELF is a perfectly fine generic name that indicates its purpose
public abstract class AbstractSuperNumberField<T extends Number, SELF extends AbstractSuperNumberField<T, SELF>>
        extends CustomField<T>
        implements CanSelectText, CanReceiveSelectionEventsFromClient, WithReceivingSelectionEventsFromClientMixin<SELF>,
                   TextSelectionNotifier<SELF>, HasPrefixAndSuffix, HasValueChangeMode, HasStyle,
                   WithLocaleMixin<SELF>, WithLabelMixin<SELF>, WithPlaceholderMixin<SELF>,
                   WithValueMixin<AbstractField.ComponentValueChangeEvent<CustomField<T>, T>, T, SELF>,
                   WithIdMixin<SELF>, WithNullValueOptionallyAllowedMixin<SELF, AbstractField.ComponentValueChangeEvent<CustomField<T>, T>, T>,
                   WithHelperMixin<SELF>, WithHelperPositionableMixin<SELF>, WithClearButtonMixin<SELF>,
                   WithRequiredMixin<SELF>, WithLabelPositionableMixin<SELF>, WithTooltipMixin<SELF> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSuperNumberField.class);

    /**
     * Predefined class/id prefix for the inner text field.
     */
    private static final String TEXT_FIELD_STYLE_PREFIX = "belongs-to-";

    /**
     * Start of a regular expression to match at least one digit.
     */
    private static final String REGEXP_START_AT_LEAST_ONE_DIGIT = "\\d{1,";

    /**
     * Start of a regular expression to match any number of digits.
     */
    private static final String REGEXP_START_ANY_DIGITS = "\\d{0,";

    /**
     * Some grouping separators are non-breaking spaces - impossible to type.
     */
    private static final char NON_BREAKING_SPACE = 160;

    /**
     * Regular space.
     */
    private static final char SPACE = ' ';

    /**
     * Dot. Needs to be escaped in regular expressions.
     */
    private static final char DOT = '.';

    /**
     * Underlying text field.
     */
    private final SuperTextField field = new SuperTextField();

    /**
     * A predicate for negativity of numbers.
     */
    private final SerializablePredicate<T> negativityPredicate;

    /**
     * An operator to turn negative number into positive.
     */
    private final SerializableFunction<T, T> turnToPositiveOperator;

    /**
     * Formatting information.
     */
    private DecimalFormat format;

    /**
     * Regular expression to catch input as it is typed.
     */
    private String regexp;

    private boolean autoselect;

    private boolean groupingSeparatorHiddenOnFocus;

    private boolean negativeValueAllowed = true;

    private boolean nullValueAllowed = false;

    private Locale locale;

    private boolean integerPartOptional = false;

    private boolean focused = false;

    private Registration innerFieldValueChangeRegistration;

    /**
     * Creates the field.
     * @param defaultValue Default value to use on startup and when there are errors.
     * @param negativityPredicate Check for whether or not given value is negative.
     * @param turnToPositiveOperator Operation to turn number into a positive one.
     * @param label Label of the field.
     * @param locale Locale to use.
     * @param maxFractionDigits Max number of fraction digits. Overwrites the settings in format obtained based on {@code locale}. Negative value means leaving whatever is supported by the format.
     */
    protected AbstractSuperNumberField(T defaultValue, SerializablePredicate<T> negativityPredicate, SerializableFunction<T, T> turnToPositiveOperator, String label, Locale locale, int maxFractionDigits) {
        super(defaultValue);

        this.negativityPredicate = negativityPredicate;
        this.turnToPositiveOperator = turnToPositiveOperator;

        if(defaultValue == null)
            this.setNullValueAllowed(true);

        this.locale = locale;
        this.format = this.getFormat(locale);
        if(maxFractionDigits >= 0)
            this.format.setMaximumFractionDigits(maxFractionDigits);
        this.updateRegularExpression();

        this.field.addClassName(TEXT_FIELD_STYLE_PREFIX +this.getClass().getSimpleName().toLowerCase());
        this.add(this.field);

        this.field.setLabel(label);
        this.field.setPreventInvalidInput(true);
        this.field.setWidthFull();

        this.field.addFocusListener(this::onFieldSelected);
        this.field.addBlurListener(this::onFieldBlurred);
        this.field.addTextSelectionListener(this::onTextSelected);
        // the following line allows for ValueChangeMode to be effective (#337)
        // at the same time, it makes setting fraction/integer digits destructive (see #339)
        // (because without it the value would be updated on blur, and not on every change)
        this.listenToValueChangesFromInnerField();
    }

    private void listenToValueChangesFromInnerField() {
        if(this.innerFieldValueChangeRegistration == null)
            this.innerFieldValueChangeRegistration = this.field.addValueChangeListener(this::onFieldChanged);
    }

    /**
     * Runs the given consumer and ignores any value change effects that happen during that time.
     * @param consumer An action to perform. Will receive {@code this} as a parameter.
     */
    private void ignoreValueChangesFromInnerField(Consumer<AbstractSuperNumberField<T, SELF>> consumer) {
        final boolean restore = this.innerFieldValueChangeRegistration != null;
        if(restore)
            this.innerFieldValueChangeRegistration.remove();
        this.innerFieldValueChangeRegistration = null;
        consumer.accept(this);
        if(restore)
            this.listenToValueChangesFromInnerField();
    }

    private void onFieldChanged(HasValue.ValueChangeEvent<String> event) {
        this.updateValue();
    }

    /**
     * Escapes the {@link #DOT} for regular expression, if needed.
     * @param character Character to escape.
     * @return Dot escaped or the passed character.
     */
    protected static String escapeDot(char character) {
        return character == DOT ? "\\." : String.valueOf(character);
    }

    private DecimalFormat getFormat(Locale locale) {
        return (DecimalFormat)NumberFormat.getInstance(Optional.ofNullable(locale).orElse(Locale.getDefault()));
    }

    /**
     * Sets the locale of the component.
     * The locale (or more precisely, its corresponding {@link NumberFormat}) is used to format how the number is displayed.
     * Contrary to {@link #setDecimalFormat(DecimalFormat)} this method preserves the precision of the number.
     * @param locale {@link Locale} to use. When {@code null}, {@link Locale#getDefault()} will be used.
     * @see #setDecimalFormat(DecimalFormat)
     */
    @Override
    public void setLocale(Locale locale) {
        // preserve information about precision
        final int maxFraction = this.format.getMaximumFractionDigits();
        final int minFraction = this.format.getMinimumFractionDigits();
        final int maxInteger = this.format.getMaximumIntegerDigits();

        final DecimalFormat newFormat = this.getFormat(locale);
        newFormat.setMaximumFractionDigits(maxFraction);
        newFormat.setMinimumFractionDigits(minFraction);
        newFormat.setMaximumIntegerDigits(maxInteger);

        // set locale and format
        this.locale = locale;
        this.setDecimalFormat(newFormat);
    }

    @Override
    public Locale getLocale() {
        return this.locale;
    }

    /**
     * Sets the format definition used to displaying the value.
     * Note: subclasses may overwrite the data in the format to make sure it follows type-specific constraints.
     * Also note: changes to the format object may result in unpredictable behaviour of this component.
     * @param format {@link DecimalFormat} to use. When {@code null}, {@link NumberFormat#getNumberInstance()} will be used.
     */
    public void setDecimalFormat(DecimalFormat format) {
        this.format = Optional.ofNullable(format).orElse((DecimalFormat)NumberFormat.getNumberInstance());
        this.updateRegularExpression();
    }

    /**
     * Checks whether the integer part of a floating-point number is optional.
     * @return Whether the integer part is optional ({@code false} by default - integer part is required).
     */
    protected boolean isIntegerPartOptional() {
        return this.integerPartOptional;
    }

    /**
     * Sets whether the integer part of a floating-point number is optional.
     * If it is set as optional, numbers can be entered without the integer part, which will be defaulted to zero.
     * @param optional Whether the integer part is optional.
     */
    protected void setIntegerPartOptional(boolean optional) {
        this.integerPartOptional = optional;
        this.updateRegularExpression();
    }

    /**
     * Sets the minimum number of fraction digits displayed. Overwrites the value in the underlying {@link DecimalFormat}.
     * Will be overwritten by calls to {@link #setDecimalFormat(DecimalFormat)}. Calls to {@link #setLocale(Locale)} will preserve this value.
     * Note: this is non-destructive, the underlying value of the field will not change (even though the representation will).
     * @param digits Number of digits to use.
     */
    protected void setMinimumFractionDigits(int digits) {
        this.format.setMinimumFractionDigits(digits);
        this.updateRegularExpression(true);
    }

    /**
     * Sets the maximum number of fraction digits displayed and allowed. Overwrites the value in the underlying {@link DecimalFormat}.
     * Will be overwritten by calls to {@link #setDecimalFormat(DecimalFormat)}. Calls to {@link #setLocale(Locale)} will preserve this value.
     * Note: this is non-destructive, the underlying value of the field will not change (even though the representation will).
     * @param digits Number of digits to use.
     */
    protected void setMaximumFractionDigits(int digits) {
        this.format.setMaximumFractionDigits(digits);
        this.updateRegularExpression(true);
    }

    /**
     * Sets the maximum number of integer digits (before decimal point) displayed and allowed. Overwrites the value in the underlying {@link DecimalFormat}.
     * Will be overwritten by calls to {@link #setDecimalFormat(DecimalFormat)}. Calls to {@link #setLocale(Locale)} will preserve this value.
     * Note: this is non-destructive, the underlying value of the field will not change (even though the representation will).
     * @param digits Number of digits to use.
     */
    public void setMaximumIntegerDigits(int digits) {
        this.format.setMaximumIntegerDigits(digits);
        this.updateRegularExpression(true);
    }

    /**
     * Chains {@link #setMaximumIntegerDigits(int)} and returns itself.
     * @param digits Maximum number of integer digits allowed.
     * @return This.
     * @see #setMaximumIntegerDigits(int)
     */
    @SuppressWarnings("unchecked")
    public final SELF withMaximumIntegerDigits(int digits) {
        this.setMaximumIntegerDigits(digits);
        return (SELF)this;
    }

    /**
     * Builds the regular expression and optionally ignores value change events from the underlying field.
     * Basically allows the representation of the value to be changed, but not the value itself.
     * @param ignoreValueChangeFromField Whether to ignore value change events coming from the underlying field.
     */
    protected final void updateRegularExpression(boolean ignoreValueChangeFromField) {
        // hopefully fixes #339
        if(ignoreValueChangeFromField)
            this.ignoreValueChangesFromInnerField(AbstractSuperNumberField::updateRegularExpression);
        else this.updateRegularExpression();
    }

    /**
     * Builds the regular expression for matching the input.
     */
    protected final void updateRegularExpression() {
        // updating the expression may change formatting
        final T value = this.getValue();

        this.regexp = this.buildRegularExpression(new StringBuilder(), this.format).toString();

        this.field.setPattern(this.regexp);

        LOGGER.debug("pattern updated to {}", this.regexp);
        if(!this.isNegativeValueAllowed() && value != null && this.negativityPredicate.test(value)) {
            LOGGER.debug("negative values are not allowed, so turning into positive value {}", value);
            this.setValue(this.turnToPositiveOperator.apply(value));
        }
        else
            this.setPresentationValue(value);
    }

    /**
     * Builds regular expression that allows neat typing of the number already formatted.
     * Overwrite with care.
     * @param builder Builder, initially empty.
     * @param format Information about the format.
     * @return Builder with the regular expression.
     */
    protected StringBuilder buildRegularExpression(StringBuilder builder, DecimalFormat format) {
        final String groupSeparatorRegexp =
                format.getDecimalFormatSymbols().getGroupingSeparator() == NON_BREAKING_SPACE
                        ? "[ "+format.getDecimalFormatSymbols().getGroupingSeparator()+"]"
                        : escapeDot(format.getDecimalFormatSymbols().getGroupingSeparator());

        builder.append("^");

        if(this.isNegativeValueAllowed())
            builder.append(format.getDecimalFormatSymbols().getMinusSign()).append("?");

        // everything after the negative sign can be optional, meaning that empty string is ok
        builder.append("(");

        final String startingGroup = this.isIntegerPartOptional() ? REGEXP_START_ANY_DIGITS : REGEXP_START_AT_LEAST_ONE_DIGIT;

        // fixes #358 - manually created DecimalFormat may have 0 in both methods
        final int groupingSize = Math.max(1, format.getGroupingSize());
        final int maxIntegerDigits = format.getMaximumIntegerDigits();

        // if the maximum number of digits allowed is less than a single group:
        if(maxIntegerDigits <= groupingSize)
            builder.append(startingGroup).append(maxIntegerDigits).append("}");
            // or, there will be at least one group of digits in the formatted number
        else {

            int leftmostGroupMaxSize = maxIntegerDigits % groupingSize;
            int middleGroupCount = maxIntegerDigits / groupingSize - 1;
            if (leftmostGroupMaxSize == 0) {
                leftmostGroupMaxSize = groupingSize;
                middleGroupCount-=1; // the left-most group is full size, so there will be one less middle group; fixes https://github.com/vaadin-miki/super-fields/issues/10
            }

            // if there are no middle groups, things are simple
            if(middleGroupCount == 0) {
                builder.append(startingGroup).append(leftmostGroupMaxSize).append("}")
                        .append(groupSeparatorRegexp).append("?").append(REGEXP_START_ANY_DIGITS).append(groupingSize).append("}");
            }
            else {
                builder.append("(");
                // two cases to check against, if middle groups are present,
                builder.append("(");
                // case 1. the leftmost group is present...
                builder.append(startingGroup).append(leftmostGroupMaxSize).append("}");
                //         ...followed by (optionally) separated middle groups
                builder.append("(").append(groupSeparatorRegexp).append("?\\d{").append(groupingSize).append("}){0,").append(middleGroupCount).append("}");
                //         ...followed by (optionally) separated last group
                builder.append("(").append(groupSeparatorRegexp).append("?").append(REGEXP_START_ANY_DIGITS).append(groupingSize).append("}").append(")?");
                builder.append(")|(");
                // case 2. the number is less than maximum allowed, so it starts with full size or less than full size group...
                builder.append(startingGroup).append(groupingSize).append("}");
                //         ...followed by (optionally) separated one less middle groups, if any
                if (middleGroupCount > 1)
                    builder.append("(").append(groupSeparatorRegexp).append("?\\d{").append(groupingSize).append("}){0,").append(middleGroupCount - 1).append("}");
                //         ...followed by (optionally) separated last group
                builder.append("(").append(groupSeparatorRegexp).append("?").append(REGEXP_START_ANY_DIGITS).append(groupingSize).append("}").append(")?");
                builder.append(")");
                builder.append(")");
            }
        }

        if(this.format.getMaximumFractionDigits() > 0)
            builder.append("(").append(escapeDot(format.getDecimalFormatSymbols().getDecimalSeparator()))
                    .append(REGEXP_START_ANY_DIGITS).append(format.getMaximumFractionDigits()).append("})?");

        builder.append(")?$");
        return builder;
    }

    /**
     * This method is called when the field loses its focus.
     * Do not overwrite it without a very good reason.
     */
    protected void updateFieldValue() {
        this.setPresentationValue(this.getValue());
    }

    private void onFieldBlurred(BlurNotifier.BlurEvent<TextField> event) {
        this.focused = false;
        this.updateFieldValue();
        // fire event
        this.getEventBus().fireEvent(new BlurEvent<>(this, event.isFromClient()));
    }

    private void onFieldSelected(FocusNotifier.FocusEvent<TextField> event) {
        this.focused = true;
        if(this.isGroupingSeparatorHiddenOnFocus()) {
            String withThousandsRemoved = this.field.getValue().replace(String.valueOf(this.format.getDecimalFormatSymbols().getGroupingSeparator()), "");
            LOGGER.debug("selected field with value {}, setting to {}", this.field.getValue(), withThousandsRemoved);
            this.field.setValue(withThousandsRemoved);
        }
        // workaround for https://github.com/vaadin/vaadin-text-field-flow/issues/65
        if(this.isAutoselect())
            this.field.selectAll();
        // fire event
        this.getEventBus().fireEvent(new FocusEvent<>(this, event.isFromClient()));
    }

    /**
     * Returns the regular expression that matches the numbers as they are typed.
     * @return A string with the regular expression.
     */
    public String getRegexp() {
        return regexp;
    }

    @Override
    protected final void setPresentationValue(T number) {
        if(number == null && !this.isNullValueAllowed())
            throw new IllegalArgumentException("null value is not allowed");

        if(!this.isFocused()) {
            final String formatted = number == null ? "" : this.format.format(number);
            LOGGER.debug("value {} to be presented as {} with {} decimal digits", number, formatted, this.format.getMaximumFractionDigits());
            this.field.setValue(formatted);
            // fixes #241 caused by a Vaadin bug https://github.com/vaadin/vaadin-text-field/issues/547
            this.field.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this.field, context ->
                    this.field.getElement().setProperty("invalid", super.isInvalid())
            ));
        }
    }

    /**
     * Parses (somewhat) raw value from the text field into a proper typed value.
     * @param rawValue Value from text field.
     * @param format Format to use.
     * @return Parsed value.
     * @throws ParseException When parsing goes wrong.
     * @throws NullPointerException This will also be caught by the calling function, so beware.
     */
    protected abstract T parseRawValue(String rawValue, DecimalFormat format) throws ParseException;

    @Override
    protected T generateModelValue() {
        try {
            String fromEvent = this.field.getValue();
            T value = this.parseRawValue(fromEvent);
            LOGGER.debug("received raw value {}, matching? {} - parsed as {}", fromEvent, fromEvent.matches(this.regexp), value);
            return value;
        } catch (ParseException | NullPointerException e) {
            // npe is thrown then there is no format present (which happens in constructor)
            return this.getEmptyValue();
        }
    }

    /**
     * Whether or not the component is auto-selecting upon focus.
     * Defaults to {@code false}.
     * @return {@code true} when the component will automatically select its text upon receiving focus, {@code false} otherwise.
     */
    public boolean isAutoselect() {
        return autoselect;
    }

    /**
     * Changes whether or not the component will be auto-selecting upon focus.
     * @param autoselect {@code true} when the component should automatically select its text upon receiving focus, {@code false} otherwise.
     */
    public void setAutoselect(boolean autoselect) {
        this.autoselect = autoselect;
    }

    /**
     * Chains {@link #setAutoselect(boolean)} and returns itself.
     * @param autoselect Autoselection value.
     * @return This.
     * @see #setAutoselect(boolean)
     */
    @SuppressWarnings("unchecked")
    public final SELF withAutoselect(boolean autoselect) {
        this.setAutoselect(autoselect);
        return (SELF)this;
    }

    /**
     * Whether or not grouping separator (used typically for thousands) should be hidden when the component gets focused.
     * Grouping separators are always shown when the component is not focused.
     * Defaults to {@code false}.
     * @return {@code true} when the value is shown without grouping separator upon focusing, {@code false} otherwise.
     */
    public boolean isGroupingSeparatorHiddenOnFocus() {
        return groupingSeparatorHiddenOnFocus;
    }

    /**
     * Changes whether or not grouping separator (used typically for thousands) should be hidden when the component gets focused.
     * Grouping separators are always shown when the component is not focused.
     * @param groupingSeparatorHiddenOnFocus {@code true} when the value should be shown without grouping separator upon focusing, {@code false} otherwise.
     */
    public void setGroupingSeparatorHiddenOnFocus(boolean groupingSeparatorHiddenOnFocus) {
        this.groupingSeparatorHiddenOnFocus = groupingSeparatorHiddenOnFocus;
    }

    /**
     * Chains {@link #setGroupingSeparatorHiddenOnFocus(boolean)} and returns itself.
     * @param groupingSeparatorHiddenOnFocus Whether or not to hide grouping separator on component focus.
     * @return This.
     * @see #setGroupingSeparatorHiddenOnFocus(boolean)
     */
    @SuppressWarnings("unchecked")
    public final SELF withGroupingSeparatorHiddenOnFocus(boolean groupingSeparatorHiddenOnFocus) {
        this.setGroupingSeparatorHiddenOnFocus(groupingSeparatorHiddenOnFocus);
        return (SELF)this;
    }

    /**
     * Whether or not negative values are allowed.
     * Defaults to {@code true}.
     * @return {@code true} when negative values are allowed, {@code false} when not.
     */
    public boolean isNegativeValueAllowed() {
        return negativeValueAllowed;
    }

    /**
     * Changes whether or not negative values are allowed.
     * If this is changed while the value entered is negative, it will be switched to positive.
     * @param negativeValueAllowed {@code true} when negative values should be allowed, {@code false} when not.
     */
    public void setNegativeValueAllowed(boolean negativeValueAllowed) {
        this.negativeValueAllowed = negativeValueAllowed;
        this.updateRegularExpression();
    }

    /**
     * Chains {@link #setNegativeValueAllowed(boolean)} and returns itself.
     * @param negativeValueAllowed Whether or not to allow negative values.
     * @return This.
     * @see #setNegativeValueAllowed(boolean)
     */
    @SuppressWarnings("unchecked")
    public final SELF withNegativeValueAllowed(boolean negativeValueAllowed) {
        this.setNegativeValueAllowed(negativeValueAllowed);
        return (SELF)this;
    }

    @Override
    public void setPrefixComponent(Component component) {
        this.field.setPrefixComponent(component);
    }

    @Override
    public Component getPrefixComponent() {
        return this.field.getPrefixComponent();
    }

    @Override
    public void setSuffixComponent(Component component) {
        this.field.setSuffixComponent(component);
    }

    @Override
    public Component getSuffixComponent() {
        return this.field.getSuffixComponent();
    }

    @Override
    public void setLabel(String label) {
        this.field.setLabel(label);
    }

    @Override
    public String getLabel() {
        return this.field.getLabel();
    }

    @Override
    public void setPlaceholder(String placeholder) {
        this.field.setPlaceholder(placeholder);
    }

    @Override
    public String getPlaceholder() {
        return this.field.getPlaceholder();
    }

    @Override
    public void setId(String id) {
        super.setId(id);
        this.field.setId(id == null ? null : TEXT_FIELD_STYLE_PREFIX +id);
    }

    /**
     * Returns the raw value, as currently displayed in the underlying text field.
     * This may depend on whether the component has focus, what locale is used, etc.
     * @return Raw value currently displayed in the underlying text field.
     */
    public String getRawValue() {
        return this.field.getValue();
    }

    /**
     * Allows adding theme variants to the underlying text field.
     * @param variants Theme variants to add.
     * @see TextField#addThemeVariants(TextFieldVariant...)
     */
    public void addThemeVariants(TextFieldVariant... variants) {
        this.field.addThemeVariants(variants);
    }

    /**
     * Allows removing theme variants from the underlying text field.
     * @param variants Theme variants to remove.
     * @see TextField#removeThemeVariants(TextFieldVariant...)
     */
    public void removeThemeVariants(TextFieldVariant... variants) {
        this.field.removeThemeVariants(variants);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.field.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() {
        return this.field.isReadOnly();
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        this.field.setRequiredIndicatorVisible(requiredIndicatorVisible);
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return this.field.isRequiredIndicatorVisible();
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        this.field.setErrorMessage(errorMessage);
    }

    @Override
    public String getErrorMessage() {
        return this.field.getErrorMessage();
    }

    @Override
    public void setInvalid(boolean invalid) {
        super.setInvalid(invalid);
        this.field.setInvalid(invalid);
    }

    @Override
    public boolean isInvalid() {
        return this.field.isInvalid();
    }

    @Override
    public void select(int from, int to) {
        this.field.select(from, to);
    }

    @Override
    public void selectAll() {
        this.field.selectAll();
    }

    @Override
    public void selectNone() {
        this.field.selectNone();
    }

    @SuppressWarnings("unchecked")
    private void onTextSelected(TextSelectionEvent<SuperTextField> event) {
        final TextSelectionEvent<SELF> selfEvent = new TextSelectionEvent<>((SELF)this, event.isFromClient(), event.getSelectionStart(), event.getSelectionEnd(), event.getSelectedText());
        this.getEventBus().fireEvent(selfEvent);
    }

    @Override
    public void setReceivingSelectionEventsFromClient(boolean receivingSelectionEventsFromClient) {
        this.field.setReceivingSelectionEventsFromClient(receivingSelectionEventsFromClient);
    }

    @Override
    public boolean isReceivingSelectionEventsFromClient() {
        return this.field.isReceivingSelectionEventsFromClient();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Registration addTextSelectionListener(TextSelectionListener<SELF> listener) {
        return this.getEventBus().addListener((Class<TextSelectionEvent<SELF>>)(Class<?>)TextSelectionEvent.class, listener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SELF withReceivingSelectionEventsFromClient(boolean receivingSelectionEventsFromClient) {
        this.setReceivingSelectionEventsFromClient(receivingSelectionEventsFromClient);
        return (SELF)this;
    }

    @Override
    public final void setNullValueAllowed(boolean allowingNullValue) {
        this.nullValueAllowed = allowingNullValue;
        if(!allowingNullValue && this.getRawValue().isEmpty())
            this.setValue(this.getEmptyValue());
    }

    @Override
    public boolean isNullValueAllowed() {
        return this.nullValueAllowed;
    }

    @Override
    public void setHelperComponent(Component component) {
        this.field.setHelperComponent(component);
    }

    @Override
    public void setHelperText(String helperText) {
        this.field.setHelperText(helperText);
    }

    @Override
    public Component getHelperComponent() {
        return this.field.getHelperComponent();
    }

    @Override
    public String getHelperText() {
        return this.field.getHelperText();
    }

    @Override
    public void setHelperAbove() {
        this.field.setHelperAbove();
    }

    @Override
    public void setHelperBelow() {
        this.field.setHelperBelow();
    }

    @Override
    public void setHelperAbove(boolean above) {
        this.field.setHelperAbove(above);
    }

    @Override
    public boolean isHelperAbove() {
        return this.field.isHelperAbove();
    }

    @Override
    public Tooltip setTooltipText(String text) {
        return this.field.setTooltipText(text);
    }

    @Override
    public Tooltip getTooltip() {
        return this.field.getTooltip();
    }
    @Override
    public void setValueChangeMode(ValueChangeMode valueChangeMode) {
        this.field.setValueChangeMode(valueChangeMode);
    }

    @Override
    public ValueChangeMode getValueChangeMode() {
        return this.field.getValueChangeMode();
    }

    @Override
    public void setValueChangeTimeout(int valueChangeTimeout) {
        this.field.setValueChangeTimeout(valueChangeTimeout);
    }

    @Override
    public int getValueChangeTimeout() {
        return this.field.getValueChangeTimeout();
    }

    /**
     * Checks if the field is currently focused (underlying field received a focus event).
     * Blurring the field causes it to lose focus.
     * @return {@code true} when the field has focus.
     */
    protected final boolean isFocused() {
        return this.focused;
    }

    @Override
    public void focus() {
        this.field.focus();
    }

    @Override
    public void blur() {
        this.field.blur();
    }

    @Override
    public void setClearButtonVisible(boolean state) {
        this.field.setClearButtonVisible(state);
    }

    @Override
    public boolean isClearButtonVisible() {
        return this.field.isClearButtonVisible();
    }

    @Override
    public void setRequired(boolean required) {
        this.field.setRequired(required);
    }

    @Override
    public boolean isRequired() {
        return this.field.isRequired();
    }

    @Override
    public void setLabelPosition(LabelPosition position) {
        this.field.setLabelPosition(position);
    }

    @Override
    public LabelPosition getLabelPosition() {
        return this.field.getLabelPosition();
    }

    /**
     * Explicitly invokes code that would otherwise be called when the component receives focus.
     * For testing purposes only.
     */
    final void simulateFocus() {
        this.onFieldSelected(new FocusNotifier.FocusEvent<>(this.field, false));
    }

    /**
     * Explicitly invokes code that would otherwise be called when the component loses focus.
     * For testing purposes only.
     */
    final void simulateBlur() {
        this.onFieldBlurred(new BlurNotifier.BlurEvent<>(this.field, false));
    }

    /**
     * Returns the maximum number of fraction digits allowed by this component.
     * For testing purposes mostly.
     * @return The maximum number of fraction digits defined in this component's {@link DecimalFormat}.
     */
    final int getMaximumFractionDigits() {
        return this.format.getMaximumFractionDigits();
    }

    /**
     * Returns the minimum number of fraction digits allowed by this component.
     * For testing purposes mostly.
     * @return The minimum number of fraction digits defined in this component's {@link DecimalFormat}.
     */
    final int getMinimumFractionDigits() {
        return this.format.getMinimumFractionDigits();
    }

    /**
     * Returns the maximum number of integer digits allowed by this component.
     * For testing purposes mostly.
     * @return The maximum number of integer digits defined in this component's {@link DecimalFormat}.
     */
    final int getMaximumIntegerDigits() {
        return this.format.getMaximumIntegerDigits();
    }

    /**
     * Calls {@link #parseRawValue(String, DecimalFormat)} using this object's format.
     * Does some mandatory checks as well (i.e. empty vs null value allowed).
     * This method is called by {@link #generateModelValue()} with the String value currently in the field.
     * @param rawValue Raw value to parse.
     * @return Parsed number.
     * @throws ParseException when parsing goes wrong.
     */
    final T parseRawValue(String rawValue) throws ParseException {
        if (rawValue == null)
            rawValue = "";
        if (rawValue.isEmpty()) {
            if (this.isNullValueAllowed())
                return null;
            else return this.getEmptyValue();
        }
        if (this.format.getDecimalFormatSymbols().getGroupingSeparator() == NON_BREAKING_SPACE)
            rawValue = rawValue.replace(SPACE, NON_BREAKING_SPACE);
        return this.parseRawValue(rawValue, this.format);
    }

}