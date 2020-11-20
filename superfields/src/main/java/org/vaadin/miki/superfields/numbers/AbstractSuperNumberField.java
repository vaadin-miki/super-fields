package org.vaadin.miki.superfields.numbers;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.FocusNotifier;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.textfield.HasPrefixAndSuffix;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
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
import org.vaadin.miki.markers.WithHelper;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithLocaleMixin;
import org.vaadin.miki.markers.WithNullValueOptionallyAllowed;
import org.vaadin.miki.markers.WithPlaceholderMixin;
import org.vaadin.miki.markers.WithReceivingSelectionEventsFromClientMixin;
import org.vaadin.miki.markers.WithTitleMixin;
import org.vaadin.miki.markers.WithValueMixin;
import org.vaadin.miki.superfields.text.SuperTextField;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Optional;

/**
 * Base class for super number fields.
 *
 * @param <T> Type of number supported by the field.
 * @author miki
 * @since 2020-04-07
 */
public abstract class AbstractSuperNumberField<T extends Number, SELF extends AbstractSuperNumberField<T, SELF>>
        extends CustomField<T>
        implements CanSelectText, CanReceiveSelectionEventsFromClient, WithReceivingSelectionEventsFromClientMixin<SELF>,
                   TextSelectionNotifier<SELF>, HasPrefixAndSuffix,
                   WithLocaleMixin<SELF>, WithLabelMixin<SELF>, WithPlaceholderMixin<SELF>, WithTitleMixin<SELF>,
                   WithValueMixin<AbstractField.ComponentValueChangeEvent<CustomField<T>, T>, T, SELF>,
                   WithIdMixin<SELF>, WithNullValueOptionallyAllowed<SELF, AbstractField.ComponentValueChangeEvent<CustomField<T>, T>, T>,
                   WithHelper<SELF> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSuperNumberField.class);

    /**
     * Predefined class/id prefix for the inner text field.
     */
    private static final String TEXT_FIELD_STYLE_PREFIX = "belongs-to-";

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
    }

    private static String escapeDot(char character) {
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
     * Sets the minimum number of fraction digits displayed. Overwrites the value in the underlying {@link DecimalFormat}.
     * Will be overwritten by calls to {@link #setDecimalFormat(DecimalFormat)}. Calls to {@link #setLocale(Locale)} will preserve this value.
     * @param digits Number of digits to use.
     */
    protected void setMinimumFractionDigits(int digits) {
        this.format.setMinimumFractionDigits(digits);
        this.updateRegularExpression();
    }

    /**
     * Sets the maximum number of fraction digits displayed and allowed. Overwrites the value in the underlying {@link DecimalFormat}.
     * Will be overwritten by calls to {@link #setDecimalFormat(DecimalFormat)}. Calls to {@link #setLocale(Locale)} will preserve this value.
     * Note: this has no effect on {@link #setValue(Object)}. If the value is set with more digits, it will stay there until input changes, even though the component shows less digits.
     * @param digits Number of digits to use.
     */
    protected void setMaximumFractionDigits(int digits) {
        this.format.setMaximumFractionDigits(digits);
        this.updateRegularExpression();
    }

    /**
     * Sets the maximum number of integer digits (before decimal point) displayed and allowed. Overwrites the value in the underlying {@link DecimalFormat}.
     * Will be overwritten by calls to {@link #setDecimalFormat(DecimalFormat)}. Calls to {@link #setLocale(Locale)} will preserve this value.
     * Note: this has no effect on {@link #setValue(Object)}. If the value is set with more digits, it will stay there until input changes, even though the component shows less digits.
     * @param digits Number of digits to use.
     */
    public void setMaximumIntegerDigits(int digits) {
        this.format.setMaximumIntegerDigits(digits);
        this.updateRegularExpression();
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
     * Builds the regular expression for matching the input.
     */
    private void updateRegularExpression() {
        // updating the expression may change formatting
        T value = this.getValue();

        final String groupSeparatorRegexp =
                this.format.getDecimalFormatSymbols().getGroupingSeparator() == NON_BREAKING_SPACE
                        ? "[ "+this.format.getDecimalFormatSymbols().getGroupingSeparator()+"]"
                        : escapeDot(this.format.getDecimalFormatSymbols().getGroupingSeparator());

        StringBuilder builder = new StringBuilder("^");

        if(this.isNegativeValueAllowed())
            builder.append(this.format.getDecimalFormatSymbols().getMinusSign()).append("?");

        // everything after the negative sign can be optional, meaning that empty string is ok
        builder.append("(");

        // if the maximum number of digits allowed is less than a single group:
        if(this.format.getMaximumIntegerDigits() <= this.format.getGroupingSize())
            builder.append("\\d{0,").append(this.format.getMaximumIntegerDigits()).append("}");
        // or, there will be at least one group of digits in the formatted number
        else {
            int leftmostGroupMaxSize = this.format.getMaximumIntegerDigits() % this.format.getGroupingSize();
            int middleGroupCount = this.format.getMaximumIntegerDigits() / this.format.getGroupingSize() - 1;
            if (leftmostGroupMaxSize == 0) {
                leftmostGroupMaxSize = this.format.getGroupingSize();
                middleGroupCount-=1; // the left-most group is full size, so there will be one less middle group; fixes https://github.com/vaadin-miki/super-fields/issues/10
            }

            // if there are no middle groups, things are simple
            if(middleGroupCount == 0) {
                builder.append("\\d{0,").append(leftmostGroupMaxSize).append("}")
                        .append(groupSeparatorRegexp).append("?\\d{0,").append(this.format.getGroupingSize()).append("}");
            }
            else {
                builder.append("(");
                // two cases to check against, if middle groups are present,
                builder.append("(");
                // case 1. the leftmost group is present...
                builder.append("\\d{1,").append(leftmostGroupMaxSize).append("}");
                //         ...followed by (optionally) separated middle groups
                builder.append("(").append(groupSeparatorRegexp).append("?\\d{").append(this.format.getGroupingSize()).append("}){0,").append(middleGroupCount).append("}");
                //         ...followed by (optionally) separated last group
                builder.append("(").append(groupSeparatorRegexp).append("?\\d{0,").append(this.format.getGroupingSize()).append("}").append(")?");
                builder.append(")|(");
                // case 2. the number is less than maximum allowed, so it starts with full size or less than full size group...
                builder.append("\\d{1,").append(this.format.getGroupingSize()).append("}");
                //         ...followed by (optionally) separated one less middle groups, if any
                if (middleGroupCount > 1)
                    builder.append("(").append(groupSeparatorRegexp).append("?\\d{").append(this.format.getGroupingSize()).append("}){0,").append(middleGroupCount - 1).append("}");
                //         ...followed by (optionally) separated last group
                builder.append("(").append(groupSeparatorRegexp).append("?\\d{0,").append(this.format.getGroupingSize()).append("}").append(")?");
                builder.append(")");
                builder.append(")");
            }
        }

        if(this.format.getMaximumFractionDigits() > 0)
            builder.append("(").append(escapeDot(this.format.getDecimalFormatSymbols().getDecimalSeparator()))
            .append("\\d{0,").append(this.format.getMaximumFractionDigits()).append("})?");

        builder.append(")?$");

        this.regexp = builder.toString();

        this.field.setPattern(this.regexp);

        LOGGER.debug("pattern updated to {}", this.regexp);
        if(!this.isNegativeValueAllowed() && value != null && this.negativityPredicate.test(value)) {
            LOGGER.debug("negative values are not allowed, so turning into positive value {}", value);
            this.setValue(this.turnToPositiveOperator.apply(value));
        }
        else
            this.setPresentationValue(value);
    }

    private void onFieldBlurred(BlurNotifier.BlurEvent<TextField> event) {
        this.setPresentationValue(this.getValue());
        // fire event
        this.getEventBus().fireEvent(new BlurEvent<>(this, event.isFromClient()));
    }

    private void onFieldSelected(FocusNotifier.FocusEvent<TextField> event) {
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
    protected void setPresentationValue(T number) {
        if(number == null && !this.isNullValueAllowed())
            throw new IllegalArgumentException("null value is not allowed");
        String formatted = number == null ? "" : this.format.format(number);
        LOGGER.debug("value {} to be presented as {} with {} decimal digits", number, formatted, this.format.getMaximumFractionDigits());
        this.field.setValue(formatted);
        // fixes #241 caused by a Vaadin bug https://github.com/vaadin/vaadin-text-field/issues/547
        this.field.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this.field, context ->
                this.field.getElement().setProperty("invalid", super.isInvalid())
        ));
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
            if (fromEvent == null)
                fromEvent = "";
            if (fromEvent.isEmpty() && this.isNullValueAllowed())
                return null;
            if (this.format.getDecimalFormatSymbols().getGroupingSeparator() == NON_BREAKING_SPACE)
                fromEvent = fromEvent.replace(SPACE, NON_BREAKING_SPACE);
            T value = this.parseRawValue(fromEvent, this.format);
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
    public void setTitle(String title) {
        this.field.setTitle(title);
    }

    @Override
    public String getTitle() {
        return this.field.getTitle();
    }

    @Override
    public void setId(String id) {
        super.setId(id);
        this.field.setId(id == null ? null : TEXT_FIELD_STYLE_PREFIX +id);
    }

    /**
     * Returns the raw value, as currently displayed in the underlying text field.
     * This may depend on whether or not the component has focus, what locale is used, etc.
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

}