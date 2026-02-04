package org.vaadin.miki.superfields.numbers;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.FocusNotifier;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.shared.HasPrefix;
import com.vaadin.flow.component.shared.HasSuffix;
import com.vaadin.flow.component.shared.ThemeVariant;
import com.vaadin.flow.component.shared.Tooltip;
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
import org.vaadin.miki.markers.WithInvalidInputPreventionMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithLabelPositionableMixin;
import org.vaadin.miki.markers.WithLocaleMixin;
import org.vaadin.miki.markers.WithNullValueOptionallyAllowedMixin;
import org.vaadin.miki.markers.WithPlaceholderMixin;
import org.vaadin.miki.markers.WithReceivingSelectionEventsFromClientMixin;
import org.vaadin.miki.markers.WithRequiredMixin;
import org.vaadin.miki.markers.WithTextInputModeMixin;
import org.vaadin.miki.markers.WithTooltipMixin;
import org.vaadin.miki.markers.WithValueMixin;
import org.vaadin.miki.shared.labels.LabelPosition;
import org.vaadin.miki.shared.text.TextInputMode;
import org.vaadin.miki.superfields.text.SuperTextField;
import org.vaadin.miki.util.RegexTools;
import org.vaadin.miki.util.StringTools;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Base class for super number fields.
 *
 * @param <T> Type of number supported by the field.
 * @author miki
 * @since 2020-04-07
 */
@CssImport("./styles/form-layout-number-field-styles.css")
@SuppressWarnings("squid:S119") // SELF is a perfectly fine generic name that indicates its purpose
public abstract class AbstractSuperNumberField<T extends Number, SELF extends AbstractSuperNumberField<T, SELF>>
    extends CustomField<T>
    implements CanSelectText, CanReceiveSelectionEventsFromClient,
    WithReceivingSelectionEventsFromClientMixin<SELF>,
    TextSelectionNotifier<SELF>, HasPrefix, HasSuffix, HasValueChangeMode,
    WithLocaleMixin<SELF>, WithLabelMixin<SELF>, WithPlaceholderMixin<SELF>,
    WithValueMixin<AbstractField.ComponentValueChangeEvent<CustomField<T>, T>, T, SELF>,
    WithIdMixin<SELF>,
    WithNullValueOptionallyAllowedMixin<SELF, AbstractField.ComponentValueChangeEvent<CustomField<T>, T>, T>,
    WithHelperMixin<SELF>, WithHelperPositionableMixin<SELF>, WithClearButtonMixin<SELF>,
    WithRequiredMixin<SELF>, WithLabelPositionableMixin<SELF>, WithTooltipMixin<SELF>,
    WithTextInputModeMixin<SELF>, WithInvalidInputPreventionMixin<SELF> {

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
   * Underlying text field.
   */
  private final SuperTextField field = new SuperTextField().withPreventingInvalidInput();

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

  private final Set<Character> groupingAlternatives = new LinkedHashSet<>();
  private final Set<Character> decimalSeparatorAlternatives = new LinkedHashSet<>();
  private final Set<Character> negativeSignAlternatives = new LinkedHashSet<>();
  private boolean groupingAlternativeAutomaticallyAdded = false;
  private boolean overlappingAlternatives = false;

  private final Set<Character> disallowedAlternativeChars = new HashSet<>();

  /**
   * Creates the field.
   *
   * @param defaultValue           Default value to use on startup and when there are errors.
   * @param negativityPredicate    Check for whether or not given value is negative.
   * @param turnToPositiveOperator Operation to turn number into a positive one.
   * @param label                  Label of the field.
   * @param locale                 Locale to use.
   * @param maxFractionDigits      Max number of fraction digits. Overwrites the settings in format obtained based on {@code locale}. Negative value means leaving whatever is supported by the format.
   */
  protected AbstractSuperNumberField(T defaultValue, SerializablePredicate<T> negativityPredicate, SerializableFunction<T, T> turnToPositiveOperator, String label, Locale locale, int maxFractionDigits) {
    super(defaultValue);

    this.negativityPredicate = negativityPredicate;
    this.turnToPositiveOperator = turnToPositiveOperator;

    if (defaultValue == null)
      this.setNullValueAllowed(true);

    this.locale = locale;
    this.format = this.getFormat(locale);
    if (maxFractionDigits >= 0)
      this.format.setMaximumFractionDigits(maxFractionDigits);
    this.updateRegularExpression();
    this.updateTextInputMode();

    this.field.addClassName(TEXT_FIELD_STYLE_PREFIX + this.getClass().getSimpleName().toLowerCase());
    this.add(this.field);

    this.field.setLabel(label);
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
    if (this.innerFieldValueChangeRegistration == null)
      this.innerFieldValueChangeRegistration = this.field.addValueChangeListener(this::onFieldChanged);
  }

  /**
   * Runs the given consumer and ignores any value change effects that happen during that time.
   *
   * @param consumer An action to perform. Will receive {@code this} as a parameter.
   */
  private void ignoreValueChangesFromInnerField(Consumer<AbstractSuperNumberField<T, SELF>> consumer) {
    final boolean restore = this.innerFieldValueChangeRegistration != null;
    if (restore)
      this.innerFieldValueChangeRegistration.remove();
    this.innerFieldValueChangeRegistration = null;
    consumer.accept(this);
    if (restore)
      this.listenToValueChangesFromInnerField();
  }

  private void onFieldChanged(HasValue.ValueChangeEvent<String> event) {
    this.updateValue();
  }

  private DecimalFormat getFormat(Locale locale) {
    return (DecimalFormat) NumberFormat.getInstance(Optional.ofNullable(locale).orElse(Locale.getDefault()));
  }

  /**
   * Sets the locale of the component.
   * The locale (or more precisely, its corresponding {@link NumberFormat}) is used to format how the number is displayed.
   * Contrary to {@link #setDecimalFormat(DecimalFormat)} this method preserves the precision of the number.
   *
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
   *
   * @param format {@link DecimalFormat} to use. When {@code null}, {@link NumberFormat#getNumberInstance()} will be used.
   */
  public void setDecimalFormat(DecimalFormat format) {
    this.format = Optional.ofNullable(format).orElse((DecimalFormat) NumberFormat.getNumberInstance());
    this.updateRegularExpression();
  }

  /**
   * Checks whether the integer part of a floating-point number is optional.
   *
   * @return Whether the integer part is optional ({@code false} by default - integer part is required).
   */
  protected boolean isIntegerPartOptional() {
    return this.integerPartOptional;
  }

  /**
   * Sets whether the integer part of a floating-point number is optional.
   * If it is set as optional, numbers can be entered without the integer part, which will be defaulted to zero.
   *
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
   *
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
   *
   * @param digits Number of digits to use.
   */
  protected void setMaximumFractionDigits(int digits) {
    this.format.setMaximumFractionDigits(digits);
    this.updateRegularExpression(true);
    this.updateTextInputMode();
  }

  /**
   * Sets the maximum number of integer digits (before decimal point) displayed and allowed. Overwrites the value in the underlying {@link DecimalFormat}.
   * Will be overwritten by calls to {@link #setDecimalFormat(DecimalFormat)}. Calls to {@link #setLocale(Locale)} will preserve this value.
   * Note: this is non-destructive, the underlying value of the field will not change (even though the representation will).
   *
   * @param digits Number of digits to use.
   */
  public void setMaximumIntegerDigits(int digits) {
    this.format.setMaximumIntegerDigits(digits);
    this.updateRegularExpression(true);
  }

  /**
   * Chains {@link #setMaximumIntegerDigits(int)} and returns itself.
   *
   * @param digits Maximum number of integer digits allowed.
   * @return This.
   * @see #setMaximumIntegerDigits(int)
   */
  @SuppressWarnings("unchecked")
  public final SELF withMaximumIntegerDigits(int digits) {
    this.setMaximumIntegerDigits(digits);
    return (SELF) this;
  }

  /**
   * Builds the regular expression and optionally ignores value change events from the underlying field.
   * Basically allows the representation of the value to be changed, but not the value itself.
   *
   * @param ignoreValueChangeFromField Whether to ignore value change events coming from the underlying field.
   */
  protected final void updateRegularExpression(boolean ignoreValueChangeFromField) {
    // hopefully fixes #339
    if (ignoreValueChangeFromField)
      this.ignoreValueChangesFromInnerField(AbstractSuperNumberField::updateRegularExpression);
    else this.updateRegularExpression();
  }

  private Stream<Character> getOnlyAllowedCharacters(Set<Character> set) {
    return set.stream()
        .filter(character -> !this.getKeyboardDisallowedAlternatives().contains(character));
  }

  /**
   * Specifies the allowed characters and prevents invalid input.
   *
   * @param builder Builder to be used. Note that the builder passed to it already starts with {@code [\d} and {@code ]} is added at the end.
   * @return The passed builder with added allowed characters.
   */
  // note that this still allows entering a sequence of valid characters that is invalid (does not match the pattern)
  // see #473
  protected StringBuilder buildAllowedCharPattern(StringBuilder builder) {
    builder.append(RegexTools.escaped(this.format.getDecimalFormatSymbols().getGroupingSeparator()));
    this.getOnlyAllowedCharacters(this.getGroupingSeparatorAlternatives())
        .forEach(character -> builder.append(RegexTools.escaped(character)));
    if (this.getMaximumFractionDigits() > 0) {
      builder.append(RegexTools.escaped(this.format.getDecimalFormatSymbols().getDecimalSeparator()));
      this.getOnlyAllowedCharacters(this.getDecimalSeparatorAlternatives())
          .forEach(character -> builder.append(RegexTools.escaped(character)));
    }
    if (this.isNegativeValueAllowed()) {
      builder.append(RegexTools.escaped(this.format.getDecimalFormatSymbols().getMinusSign()));
      this.getOnlyAllowedCharacters(this.getNegativeSignAlternatives())
          .forEach(character -> builder.append(RegexTools.escaped(character)));
    }
    return builder;
  }

  /**
   * Builds the regular expression for matching the input.
   */
  protected final void updateRegularExpression() {
    // updating the expression may change formatting
    final T value = this.getValue();

    this.regexp = this.buildRegularExpression(new StringBuilder(), this.format).toString();

    this.field.setPattern(this.regexp);

    final String allowedChars = this.buildAllowedCharPattern(new StringBuilder("[\\d")).append("]").toString();
    this.field.setAllowedCharPattern(allowedChars);

    LOGGER.debug("pattern updated to {}", this.regexp);
    if (!this.isNegativeValueAllowed() && value != null && this.negativityPredicate.test(value)) {
      LOGGER.debug("negative values are not allowed, so turning into positive value {}", value);
      this.setValue(this.turnToPositiveOperator.apply(value));
    } else
      this.setPresentationValue(value);
  }

  /**
   * Updates the underlying field's text input mode.
   * This shows a proper on-screen keyboard on devices that support it.
   */
  // fixes #471
  protected void updateTextInputMode() {
    // if there are no fraction digits allowed, use NUMERIC (as DECIMAL shows decimal characters)
    this.field.setTextInputMode(
        this.getMaximumFractionDigits() == 0 ? TextInputMode.NUMERIC : TextInputMode.DECIMAL
    );
  }

  private void ensureSpaceGroupingPossible() {
    // always make sure there is a space if the format has a non-breaking one
    if (this.format.getDecimalFormatSymbols().getGroupingSeparator() == NON_BREAKING_SPACE) {
      this.groupingAlternatives.add(SPACE);
      this.groupingAlternativeAutomaticallyAdded = true;
    } else if (this.groupingAlternativeAutomaticallyAdded && this.groupingAlternatives.contains(SPACE)) {
      this.groupingAlternatives.remove(SPACE);
      this.groupingAlternativeAutomaticallyAdded = false;
    }
  }

  /**
   * Builds regular expression that allows neat typing of the number already formatted.
   * Overwrite with care.
   *
   * @param builder Builder, initially empty.
   * @param format  Information about the format.
   * @return Builder with the regular expression.
   */
  protected StringBuilder buildRegularExpression(StringBuilder builder, DecimalFormat format) {
    this.ensureSpaceGroupingPossible();

    final String groupSeparatorRegexp = RegexTools.characterSelector(
        format.getDecimalFormatSymbols().getGroupingSeparator(),
        this.getGroupingSeparatorAlternatives()
    );

    builder.append("^");

    if (this.isNegativeValueAllowed())
      RegexTools.characterSelector(builder, format.getDecimalFormatSymbols().getMinusSign(), this.getNegativeSignAlternatives()).append("?");

    // everything after the negative sign can be optional, meaning that empty string is ok
    builder.append("(");

    final String startingGroup = this.isIntegerPartOptional() ? REGEXP_START_ANY_DIGITS : REGEXP_START_AT_LEAST_ONE_DIGIT;

    // fixes #358 - manually created DecimalFormat may have 0 in both methods
    final int groupingSize = Math.max(1, format.getGroupingSize());
    final int maxIntegerDigits = format.getMaximumIntegerDigits();

    // if the maximum number of digits allowed is less than a single group:
    if (maxIntegerDigits <= groupingSize)
      builder.append(startingGroup).append(maxIntegerDigits).append("}");
      // or, there will be at least one group of digits in the formatted number
    else {

      int leftmostGroupMaxSize = maxIntegerDigits % groupingSize;
      int middleGroupCount = maxIntegerDigits / groupingSize - 1;
      if (leftmostGroupMaxSize == 0) {
        leftmostGroupMaxSize = groupingSize;
        middleGroupCount -= 1; // the left-most group is full size, so there will be one less middle group; fixes https://github.com/vaadin-miki/super-fields/issues/10
      }

      // if there are no middle groups, things are simple
      if (middleGroupCount == 0) {
        builder.append(startingGroup).append(leftmostGroupMaxSize).append("}")
            .append(groupSeparatorRegexp).append("?").append(REGEXP_START_ANY_DIGITS).append(groupingSize).append("}");
      } else {
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

    if (this.format.getMaximumFractionDigits() > 0)
      RegexTools.characterSelector(builder.append("("), format.getDecimalFormatSymbols().getDecimalSeparator(), this.getDecimalSeparatorAlternatives())
          .append(REGEXP_START_ANY_DIGITS)
          .append(format.getMaximumFractionDigits()).append("})?");

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
    this.getEventBus().fireEvent(new BlurEvent<>(this, event.isFromClient(), null));
  }

  private void onFieldSelected(FocusNotifier.FocusEvent<TextField> event) {
    this.focused = true;
    if (this.isGroupingSeparatorHiddenOnFocus()) {
      String withThousandsRemoved = this.field.getValue().replace(String.valueOf(this.format.getDecimalFormatSymbols().getGroupingSeparator()), "");
      LOGGER.debug("selected field with value {}, setting to {}", this.field.getValue(), withThousandsRemoved);
      this.field.setValue(withThousandsRemoved);
    }
    // workaround for https://github.com/vaadin/vaadin-text-field-flow/issues/65
    if (this.isAutoselect())
      this.field.selectAll();
    // fire event
    this.getEventBus().fireEvent(new FocusEvent<>(this, event.isFromClient(), null));
  }

  /**
   * Returns the regular expression that matches the numbers as they are typed.
   *
   * @return A string with the regular expression.
   */
  public String getRegexp() {
    return regexp;
  }

  @Override
  protected final void setPresentationValue(T number) {
    LOGGER.trace("about to set presentation value to {}", number);

    if (number == null && !this.isNullValueAllowed())
      throw new IllegalArgumentException("null value is not allowed");

    // this block (from here to end) used to be run only when the field was not focused
    // this caused issue #537, where a value set by a global keybinding caused the component to be out of sync
    // seems that #337 (value change mode) is not affected, as events trigger happily without reformatting
    final String formatted = number == null ? "" : this.format.format(number);
    LOGGER.debug("value {} to be presented as {} with {} decimal digits", number, formatted, this.format.getMaximumFractionDigits());
    this.field.setValue(formatted);
    // fixes #241 caused by a Vaadin bug https://github.com/vaadin/vaadin-text-field/issues/547
    this.field.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this.field, context ->
        this.field.getElement().setProperty("invalid", super.isInvalid())
    ));

    LOGGER.trace("done setting presentation value");
  }

  /**
   * Parses (somewhat) raw value from the text field into a proper typed value.
   *
   * @param rawValue Value from text field.
   * @param format   Format to use.
   * @return Parsed value.
   * @throws ParseException       When parsing goes wrong.
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
   *
   * @return {@code true} when the component will automatically select its text upon receiving focus, {@code false} otherwise.
   */
  public boolean isAutoselect() {
    return autoselect;
  }

  /**
   * Changes whether or not the component will be auto-selecting upon focus.
   *
   * @param autoselect {@code true} when the component should automatically select its text upon receiving focus, {@code false} otherwise.
   */
  public void setAutoselect(boolean autoselect) {
    this.autoselect = autoselect;
  }

  /**
   * Chains {@link #setAutoselect(boolean)} and returns itself.
   *
   * @param autoselect Autoselection value.
   * @return This.
   * @see #setAutoselect(boolean)
   */
  @SuppressWarnings("unchecked")
  public final SELF withAutoselect(boolean autoselect) {
    this.setAutoselect(autoselect);
    return (SELF) this;
  }

  /**
   * Whether or not grouping separator (used typically for thousands) should be hidden when the component gets focused.
   * Grouping separators are always shown when the component is not focused.
   * Defaults to {@code false}.
   *
   * @return {@code true} when the value is shown without grouping separator upon focusing, {@code false} otherwise.
   */
  public boolean isGroupingSeparatorHiddenOnFocus() {
    return groupingSeparatorHiddenOnFocus;
  }

  /**
   * Changes whether or not grouping separator (used typically for thousands) should be hidden when the component gets focused.
   * Grouping separators are always shown when the component is not focused.
   *
   * @param groupingSeparatorHiddenOnFocus {@code true} when the value should be shown without grouping separator upon focusing, {@code false} otherwise.
   */
  public void setGroupingSeparatorHiddenOnFocus(boolean groupingSeparatorHiddenOnFocus) {
    this.groupingSeparatorHiddenOnFocus = groupingSeparatorHiddenOnFocus;
  }

  /**
   * Chains {@link #setGroupingSeparatorHiddenOnFocus(boolean)} and returns itself.
   *
   * @param groupingSeparatorHiddenOnFocus Whether or not to hide grouping separator on component focus.
   * @return This.
   * @see #setGroupingSeparatorHiddenOnFocus(boolean)
   */
  @SuppressWarnings("unchecked")
  public final SELF withGroupingSeparatorHiddenOnFocus(boolean groupingSeparatorHiddenOnFocus) {
    this.setGroupingSeparatorHiddenOnFocus(groupingSeparatorHiddenOnFocus);
    return (SELF) this;
  }

  /**
   * Whether or not negative values are allowed.
   * Defaults to {@code true}.
   *
   * @return {@code true} when negative values are allowed, {@code false} when not.
   */
  public boolean isNegativeValueAllowed() {
    return negativeValueAllowed;
  }

  /**
   * Changes whether or not negative values are allowed.
   * If this is changed while the value entered is negative, it will be switched to positive.
   *
   * @param negativeValueAllowed {@code true} when negative values should be allowed, {@code false} when not.
   */
  public void setNegativeValueAllowed(boolean negativeValueAllowed) {
    this.negativeValueAllowed = negativeValueAllowed;
    this.updateRegularExpression();
  }

  /**
   * Chains {@link #setNegativeValueAllowed(boolean)} and returns itself.
   *
   * @param negativeValueAllowed Whether or not to allow negative values.
   * @return This.
   * @see #setNegativeValueAllowed(boolean)
   */
  @SuppressWarnings("unchecked")
  public final SELF withNegativeValueAllowed(boolean negativeValueAllowed) {
    this.setNegativeValueAllowed(negativeValueAllowed);
    return (SELF) this;
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
    this.field.setId(id == null ? null : TEXT_FIELD_STYLE_PREFIX + id);
  }

  /**
   * Returns the raw value, as currently displayed in the underlying text field.
   * This may depend on whether the component has focus, what locale is used, etc.
   *
   * @return Raw value currently displayed in the underlying text field.
   */
  public String getRawValue() {
    return this.field.getValue();
  }

  /**
   * Allows adding theme variants to the underlying text field.
   *
   * @param variants Theme variants to add.
   * @see TextField#addThemeVariants(ThemeVariant[])
   */
  public void addThemeVariants(TextFieldVariant... variants) {
    this.field.addThemeVariants(variants);
  }

  /**
   * Allows removing theme variants from the underlying text field.
   *
   * @param variants Theme variants to remove.
   * @see TextField#removeThemeVariants(ThemeVariant[])
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
    final TextSelectionEvent<SELF> selfEvent = new TextSelectionEvent<>((SELF) this, event.isFromClient(), event.getSelectionStart(), event.getSelectionEnd(), event.getSelectedText());
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
    return this.getEventBus().addListener((Class<TextSelectionEvent<SELF>>) (Class<?>) TextSelectionEvent.class, listener);
  }

  @Override
  @SuppressWarnings("unchecked")
  public SELF withReceivingSelectionEventsFromClient(boolean receivingSelectionEventsFromClient) {
    this.setReceivingSelectionEventsFromClient(receivingSelectionEventsFromClient);
    return (SELF) this;
  }

  @Override
  public final void setNullValueAllowed(boolean allowingNullValue) {
    this.nullValueAllowed = allowingNullValue;
    if (!allowingNullValue && this.getRawValue().isEmpty())
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
   *
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

  @Override
  public void setTextInputMode(TextInputMode inputMode) {
    this.field.setTextInputMode(inputMode);
  }

  @Override
  public TextInputMode getTextInputMode() {
    return this.field.getTextInputMode();
  }

  @Override
  public void setPreventingInvalidInput(boolean prevent) {
    this.field.setPreventingInvalidInput(prevent);
  }

  @Override
  public boolean isPreventingInvalidInput() {
    return this.field.isPreventingInvalidInput();
  }

  /**
   * Checks if alternative symbols are allowed to overlap with one another and the default locale's symbols.
   *
   * @return {@code true} when overlapping between symbols is allowed; {@code false} otherwise and by default.
   */
  public boolean isOverlappingAlternatives() {
    return overlappingAlternatives;
  }

  /**
   * Allows (or disallows) alternative grouping, separator and negative sign symbols to overlap with each other and with the locale's default symbols.
   * Note that this may lead to some unexpected results.
   * Alternative symbols are always replaced in fixed order: negative sign, grouping symbols, decimal separator symbols.
   *
   * @param overlappingAlternatives When {@code true}, overlapping between symbols will be allowed.
   */
  public void setOverlappingAlternatives(boolean overlappingAlternatives) {
    this.overlappingAlternatives = overlappingAlternatives;
    // when alternatives are no longer allowed, make sure none are left
    if (!overlappingAlternatives) {
      // remove locale symbols
      final Set<Character> formatSymbols = Set.of(this.format.getDecimalFormatSymbols().getMinusSign(),
          this.format.getDecimalFormatSymbols().getDecimalSeparator(),
          this.format.getDecimalFormatSymbols().getGroupingSeparator());
      Stream.of(this.negativeSignAlternatives, this.groupingAlternatives, this.decimalSeparatorAlternatives)
          .forEach(characters -> characters.removeAll(formatSymbols));
      // remove overlapping alternatives
      this.negativeSignAlternatives.removeAll(this.getGroupingSeparatorAlternatives());
      this.negativeSignAlternatives.removeAll(this.getDecimalSeparatorAlternatives());
      this.groupingAlternatives.removeAll(this.getNegativeSignAlternatives());
      this.groupingAlternatives.removeAll(this.getDecimalSeparatorAlternatives());
      this.decimalSeparatorAlternatives.removeAll(this.getNegativeSignAlternatives());
      this.decimalSeparatorAlternatives.removeAll(this.getGroupingSeparatorAlternatives());
    }
  }

  /**
   * Chains {@link #setOverlappingAlternatives(boolean)} and returns itself.
   *
   * @param overlappingAlternatives Whether to allow overlapping alternatives.
   * @return This.
   * @see #setOverlappingAlternatives(boolean)
   */
  @SuppressWarnings("unchecked")
  public SELF withOverlappingAlternatives(boolean overlappingAlternatives) {
    this.setOverlappingAlternatives(overlappingAlternatives);
    return (SELF) this;
  }

  /**
   * Chains {@link #setOverlappingAlternatives(boolean)} with {@code true} as a parameter, and returns itself.
   *
   * @return This.
   * @see #setOverlappingAlternatives(boolean)
   */
  public SELF withOverlappingAlternatives() {
    return this.withOverlappingAlternatives(true);
  }

  /**
   * Chains {@link #setOverlappingAlternatives(boolean)} with {@code false} as a parameter, and returns itself.
   *
   * @return This.
   * @see #setOverlappingAlternatives(boolean)
   */
  public SELF withoutOverlappingAlternatives() {
    return this.withOverlappingAlternatives(false);
  }

  /**
   * Prevents specified alternative characters from being typed with keyboard.
   * Only the characters that are also used as grouping separator, decimal separator or negative sign alternative are prevented.
   * This alters the underlying field's allowed char pattern; it is still possible to paste text with that character.
   *
   * @param characters Characters to be disallowed.
   */
  public void setKeyboardDisallowedAlternatives(Set<Character> characters) {
    this.disallowedAlternativeChars.clear();
    this.disallowedAlternativeChars.addAll(characters);
    this.updateRegularExpression();
  }

  /**
   * Returns the set of characters that are prevented from being typed in.
   * Note that this set may include more characters than the alternative symbols.
   *
   * @return A set of characters that, if they are an alternative symbol, cannot be typed in. Never {@code null}, but possibly empty.
   */
  public Set<Character> getKeyboardDisallowedAlternatives() {
    return Collections.unmodifiableSet(this.disallowedAlternativeChars);
  }

  /**
   * Chains {@link #setKeyboardDisallowedAlternatives(Set)} and returns itself.
   *
   * @param characters Characters to disallow.
   * @return This.
   * @see #setKeyboardDisallowedAlternatives(Set)
   */
  @SuppressWarnings("unchecked")
  public SELF withKeyboardDisallowedAlternatives(Set<Character> characters) {
    this.setKeyboardDisallowedAlternatives(characters);
    return (SELF) this;
  }

  /**
   * Chains {@link #setKeyboardDisallowedAlternatives(Set)} and returns itself.
   *
   * @param characters Characters to disallow.
   * @return This.
   * @see #setKeyboardDisallowedAlternatives(Set)
   */
  public SELF withKeyboardDisallowedAlternatives(char... characters) {
    return this.withKeyboardDisallowedAlternatives(StringTools.toCharacterSet(characters));
  }

  /**
   * Returns the currently accepted alternatives to the grouping (thousand) separator.
   *
   * @return Currently allowed alternatives to the main grouping separator, which is not included in the result. Never {@code null}, but possibly empty.
   */
  public Set<Character> getGroupingSeparatorAlternatives() {
    return Collections.unmodifiableSet(this.groupingAlternatives);
  }

  /**
   * Sets grouping separator alternatives, replacing previously existing ones.
   * Note that this preserves the automatically added {@link #SPACE} when the format uses {@link #NON_BREAKING_SPACE}.
   *
   * @param alternatives Any alternatives that are identical to the already used separators or negative sign or their alternatives are ignored.
   */
  public void setGroupingSeparatorAlternatives(Set<Character> alternatives) {
    this.groupingAlternatives.clear();
    this.groupingAlternatives.addAll(
        alternatives.stream()
            .filter(c -> this.isOverlappingAlternatives() || (!this.negativeSignAlternatives.contains(c)
                && !this.decimalSeparatorAlternatives.contains(c)
                && this.format.getDecimalFormatSymbols().getMinusSign() != c
                && this.format.getDecimalFormatSymbols().getGroupingSeparator() != c
                && this.format.getDecimalFormatSymbols().getDecimalSeparator() != c))
            .collect(Collectors.toCollection(LinkedHashSet::new))
    );
    if (this.groupingAlternativeAutomaticallyAdded) {
      if (alternatives.contains(SPACE)) this.groupingAlternativeAutomaticallyAdded = false;
      else this.groupingAlternatives.add(SPACE);
    }
    this.updateRegularExpression();
  }

  /**
   * Chains {@link #setGroupingSeparatorAlternatives(Set)} and returns itself.
   *
   * @param alternatives Alternatives to use.
   * @return This.
   * @see #setGroupingSeparatorAlternatives(Set)
   */
  @SuppressWarnings("unchecked")
  public SELF withGroupingSeparatorAlternatives(Set<Character> alternatives) {
    this.setGroupingSeparatorAlternatives(alternatives);
    return (SELF) this;
  }

  /**
   * Chains {@link #setGroupingSeparatorAlternatives(Set)} and returns itself.
   *
   * @param alternatives Alternatives to use.
   * @return This.
   * @see #setGroupingSeparatorAlternatives(Set)
   */
  public SELF withGroupingSeparatorAlternatives(char... alternatives) {
    return this.withGroupingSeparatorAlternatives(StringTools.toCharacterSet(alternatives));
  }

  /**
   * Returns the currently accepted alternatives to the decimal separator.
   *
   * @return Currently allowed alternatives to the main decimal separator, which is not included in the result. Never {@code null}, but possibly empty.
   */
  protected Set<Character> getDecimalSeparatorAlternatives() {
    return Collections.unmodifiableSet(this.decimalSeparatorAlternatives);
  }

  /**
   * Sets decimal separator alternatives, replacing previously existing ones.
   *
   * @param alternatives Any alternatives that are identical to the already used separators or negative sign or their alternatives are ignored.
   */
  protected void setDecimalSeparatorAlternatives(Set<Character> alternatives) {
    this.decimalSeparatorAlternatives.clear();
    this.decimalSeparatorAlternatives.addAll(
        alternatives.stream()
            .filter(c -> this.isOverlappingAlternatives() || (!this.groupingAlternatives.contains(c)
                && !this.negativeSignAlternatives.contains(c)
                && (this.format.getDecimalFormatSymbols().getMinusSign() != c)
                && (this.format.getDecimalFormatSymbols().getGroupingSeparator() != c)
                && (this.format.getDecimalFormatSymbols().getDecimalSeparator() != c)))
            .collect(Collectors.toCollection(LinkedHashSet::new))
    );
    this.updateRegularExpression();
  }

  /**
   * Chains {@link #setDecimalSeparatorAlternatives(Set)} and returns itself.
   *
   * @param alternatives Alternatives to use.
   * @return This.
   * @see #setDecimalSeparatorAlternatives(Set)
   */
  @SuppressWarnings("unchecked")
  protected SELF withDecimalSeparatorAlternatives(Set<Character> alternatives) {
    this.setDecimalSeparatorAlternatives(alternatives);
    return (SELF) this;
  }

  /**
   * Chains {@link #setDecimalSeparatorAlternatives(Set)} and returns itself.
   *
   * @param alternatives Alternatives to use.
   * @return This.
   * @see #setDecimalSeparatorAlternatives(Set)
   */
  protected SELF withDecimalSeparatorAlternatives(char... alternatives) {
    return this.withDecimalSeparatorAlternatives(StringTools.toCharacterSet(alternatives));
  }

  /**
   * Returns the currently accepted alternatives to the negative (minus) sign.
   *
   * @return Currently allowed alternatives to the main negative sign, which is not included in the result. Never {@code null}, but possibly empty.
   */
  public Set<Character> getNegativeSignAlternatives() {
    return Collections.unmodifiableSet(negativeSignAlternatives);
  }

  /**
   * Sets negative sign alternatives, replacing previously existing ones.
   *
   * @param alternatives Any alternatives that are identical to the already used separators or negative sign or their alternatives are ignored.
   */
  public void setNegativeSignAlternatives(Set<Character> alternatives) {
    this.negativeSignAlternatives.clear();
    this.negativeSignAlternatives.addAll(
        alternatives.stream()
            .filter(c -> this.isOverlappingAlternatives() || (!this.groupingAlternatives.contains(c)
                && !this.decimalSeparatorAlternatives.contains(c)
                && (this.format.getDecimalFormatSymbols().getMinusSign() != c)
                && (this.format.getDecimalFormatSymbols().getGroupingSeparator() != c)
                && (this.format.getDecimalFormatSymbols().getDecimalSeparator() != c)))
            .collect(Collectors.toCollection(LinkedHashSet::new))
    );
    this.updateRegularExpression();
  }

  /**
   * Chains {@link #setNegativeSignAlternatives(Set)} and returns itself.
   *
   * @param alternatives Alternatives to use.
   * @return This.
   * @see #setNegativeSignAlternatives(Set)
   */
  @SuppressWarnings("unchecked")
  public SELF withNegativeSignAlternatives(Set<Character> alternatives) {
    this.setNegativeSignAlternatives(alternatives);
    return (SELF) this;
  }

  /**
   * Chains {@link #setNegativeSignAlternatives(Set)} and returns itself.
   *
   * @param alternatives Alternatives to use.
   * @return This.
   * @see #setNegativeSignAlternatives(Set)
   */
  public SELF withNegativeSignAlternatives(char... alternatives) {
    return this.withNegativeSignAlternatives(StringTools.toCharacterSet(alternatives));
  }

  /**
   * Explicitly invokes code that would otherwise be called when the component receives focus.
   * For testing purposes only.
   */
  final void simulateFocus() {
    this.onFieldSelected(new FocusNotifier.FocusEvent<>(this.field, false, null));
  }

  /**
   * Explicitly invokes code that would otherwise be called when the component loses focus.
   * For testing purposes only.
   */
  final void simulateBlur() {
    this.onFieldBlurred(new BlurNotifier.BlurEvent<>(this.field, false, null));
  }

  /**
   * Returns the maximum number of fraction digits allowed by this component.
   * For testing purposes mostly.
   *
   * @return The maximum number of fraction digits defined in this component's {@link DecimalFormat}.
   */
  final int getMaximumFractionDigits() {
    return this.format.getMaximumFractionDigits();
  }

  /**
   * Returns the minimum number of fraction digits allowed by this component.
   * For testing purposes mostly.
   *
   * @return The minimum number of fraction digits defined in this component's {@link DecimalFormat}.
   */
  final int getMinimumFractionDigits() {
    return this.format.getMinimumFractionDigits();
  }

  /**
   * Returns the maximum number of integer digits allowed by this component.
   * For testing purposes mostly.
   *
   * @return The maximum number of integer digits defined in this component's {@link DecimalFormat}.
   */
  final int getMaximumIntegerDigits() {
    return this.format.getMaximumIntegerDigits();
  }

  /**
   * Calls {@link #parseRawValue(String, DecimalFormat)} using this object's format.
   * Does some mandatory checks as well (i.e. empty vs null value allowed).
   * This method is called by {@link #generateModelValue()} with the String value currently in the field.
   *
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
    // replace alternative characters
    for (char c : this.getNegativeSignAlternatives())
      rawValue = rawValue.replace(c, this.format.getDecimalFormatSymbols().getMinusSign());
    for (char c : this.getGroupingSeparatorAlternatives())
      rawValue = rawValue.replace(c, this.format.getDecimalFormatSymbols().getGroupingSeparator());
    for (char c : this.getDecimalSeparatorAlternatives())
      rawValue = rawValue.replace(c, this.format.getDecimalFormatSymbols().getDecimalSeparator());

    return this.parseRawValue(rawValue, this.format);
  }

}