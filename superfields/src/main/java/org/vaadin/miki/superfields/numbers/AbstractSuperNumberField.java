package org.vaadin.miki.superfields.numbers;

import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.FocusNotifier;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.textfield.HasPrefixAndSuffix;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.SerializablePredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.miki.markers.HasLabel;
import org.vaadin.miki.markers.HasLocale;
import org.vaadin.miki.markers.HasPlaceholder;
import org.vaadin.miki.markers.HasTitle;

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
public abstract class AbstractSuperNumberField<T extends Number> extends CustomField<T> implements HasPrefixAndSuffix, HasLabel, HasPlaceholder, HasTitle, HasLocale {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSuperNumberField.class);

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
    private final TextField field = new TextField();

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

        this.locale = locale;
        this.format = this.getFormat(locale);
        if(maxFractionDigits >= 0)
            this.format.setMaximumFractionDigits(maxFractionDigits);
        this.updateRegularExpression();

        this.add(this.field);

        this.field.setLabel(label);
        this.field.setPreventInvalidInput(true);

        this.field.addFocusListener(this::onFieldSelected);
        this.field.addBlurListener(this::onFieldBlurred);
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
     * @param locale {@link Locale} to use. When {@code null}, {@link Locale#getDefault()} will be used.
     * @see #setDecimalFormat(DecimalFormat)
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
        this.setDecimalFormat(this.getFormat(locale));
    }

    @Override
    public Locale getLocale() {
        return this.locale;
    }

    /**
     * Sets the format definition used to displaying the value.
     * @param format {@link DecimalFormat} to use. When {@code null}, {@link NumberFormat#getNumberInstance()} will be used.
     */
    public void setDecimalFormat(DecimalFormat format) {
        this.format = Optional.ofNullable(format).orElse((DecimalFormat)NumberFormat.getNumberInstance());
        this.updateRegularExpression();
    }

    /**
     * Sets the minimum number of fraction digits displayed. Overwrites the value in the underlying {@link DecimalFormat}.
     * Will be overwritten by calls to {@link #setLocale(Locale)} or {@link #setDecimalFormat(DecimalFormat)}.
     * @param digits Number of digits to use.
     */
    protected void setMinimumFractionDigits(int digits) {
        this.format.setMinimumFractionDigits(digits);
        this.updateRegularExpression();
    }

    /**
     * Sets the maximum number of fraction digits displayed and allowed. Overwrites the value in the underlying {@link DecimalFormat}.
     * Will be overwritten by calls to {@link #setLocale(Locale)} or {@link #setDecimalFormat(DecimalFormat)}.
     * Note: this has no effect on {@link #setValue(Object)}. If the value is set with more digits, it will stay there until input changes, even though the component shows less digits.
     * @param digits Number of digits to use.
     */
    protected void setMaximumFractionDigits(int digits) {
        this.format.setMaximumFractionDigits(digits);
        this.updateRegularExpression();
    }

    /**
     * Sets the maximum number of integer digits (before decimal point) displayed and allowed. Overwrites the value in the underlying {@link DecimalFormat}.
     * Will be overwritten by calls to {@link #setLocale(Locale)} or {@link #setDecimalFormat(DecimalFormat)}.
     * Note: this has no effect on {@link #setValue(Object)}. If the value is set with more digits, it will stay there until input changes, even though the component shows less digits.
     * @param digits Number of digits to use.
     */
    public void setMaximumIntegerDigits(int digits) {
        this.format.setMaximumIntegerDigits(digits);
        this.updateRegularExpression();
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
        if(!this.isNegativeValueAllowed() && this.negativityPredicate.test(value)) {
            LOGGER.debug("negative values are not allowed, so turning into positive value {}", value);
            this.setValue(this.turnToPositiveOperator.apply(value));
        }
        else
            this.setPresentationValue(value);
    }

    private void onFieldBlurred(BlurNotifier.BlurEvent<TextField> event) {
        this.setPresentationValue(this.getValue());
    }

    private void onFieldSelected(FocusNotifier.FocusEvent<TextField> event) {
        if(this.isGroupingSeparatorHiddenOnFocus()) {
            String withThousandsRemoved = this.field.getValue().replace(String.valueOf(this.format.getDecimalFormatSymbols().getGroupingSeparator()), "");
            LOGGER.debug("selected field with value {}, setting to {}", this.field.getValue(), withThousandsRemoved);
            this.field.setValue(withThousandsRemoved);
        }
        // workaround for https://github.com/vaadin/vaadin-text-field-flow/issues/65
        if(this.isAutoselect())
            this.field.getElement().executeJs("this.inputElement.select()");
    }

    /**
     * Returns the regular expression that matches the numbers as they are typed.
     * @return A string with the regular expression.
     */
    public String getRegexp() {
        return regexp;
    }

    @Override
    protected void setPresentationValue(T aDouble) {
        String formatted = this.format.format(aDouble);
        LOGGER.debug("value {} to be presented as {} with {} decimal digits", aDouble, formatted, this.format.getMaximumFractionDigits());
        this.field.setValue(formatted);
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
}