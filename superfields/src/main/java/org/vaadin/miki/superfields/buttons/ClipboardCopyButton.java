package org.vaadin.miki.superfields.buttons;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.dom.DisabledUpdateMode;
import com.vaadin.flow.shared.Registration;
import org.vaadin.miki.events.clipboard.ClipboardCopyEvent;
import org.vaadin.miki.events.clipboard.ClipboardCopyListener;
import org.vaadin.miki.events.clipboard.ClipboardCopyNotifier;
import org.vaadin.miki.markers.WithIconMixin;
import org.vaadin.miki.markers.WithTextMixin;
import org.vaadin.miki.markers.WithTooltipMixin;
import org.vaadin.miki.util.SerializableUnaryOperator;

import java.util.Objects;

/**
 * An always-enabled button that copies to clipboard.
 *
 * @author miki (idea contributed by Egor Vasilyev)
 * @since 2025-08-16
 */
@Tag("clipboard-copy-button")
@JsModule("./clipboard-copy-button.ts")
public class ClipboardCopyButton extends Component implements ClipboardCopyNotifier<ClipboardCopyButton>,
    WithTextMixin<ClipboardCopyButton>, WithIconMixin<ClipboardCopyButton>, WithTooltipMixin<ClipboardCopyButton> {

  private static final String EVENT_NAME = "button-pressed";
  private static final String RAW_VALUE_EVENT_PATH = "event.detail.raw";
  private static final String COPIED_VALUE_EVENT_PATH = "event.detail.value";
  private static final String TEXT_PROPERTY = "text";
  private static final String ICON_SLOT_NAME = "icon";

  private SerializableUnaryOperator<String> clipboardModifierFunction = SerializableUnaryOperator.identity();
  private Icon icon;

  /**
   * Creates the button with no text, no tooltip and a {@link VaadinIcon#COPY} icon.
   */
  public ClipboardCopyButton() {
    this(VaadinIcon.COPY.create());
  }

  /**
   * Creates the button with no text, no tooltip and a {@link VaadinIcon#COPY} icon, with the given modifier function.
   *
   * @param clipboardModifierFunction Function to modify the text before it is copied to the clipboard.
   */
  public ClipboardCopyButton(SerializableUnaryOperator<String> clipboardModifierFunction) {
    this(VaadinIcon.COPY.create(), clipboardModifierFunction);
  }

  /**
   * Creates the button with no icon, no tooltip and the given text.
   *
   * @param text Text to show on the button.
   */
  public ClipboardCopyButton(String text) {
    this(text, null, null, SerializableUnaryOperator.identity());
  }

  /**
   * Creates the button with no icon, no tooltip and the given text, with the given modifier function
   *
   * @param text                      Text to show on the button.
   * @param clipboardModifierFunction Function to modify the text before it is copied to the clipboard.
   */
  public ClipboardCopyButton(String text, SerializableUnaryOperator<String> clipboardModifierFunction) {
    this(text, null, null, clipboardModifierFunction);
  }

  /**
   * Creates the button with no text, no tooltip and the given icon.
   *
   * @param icon Icon to display.
   */
  public ClipboardCopyButton(Icon icon) {
    this(null, icon, null, SerializableUnaryOperator.identity());
  }

  /**
   * Creates the button with no text, no tooltip and the given icon, with the given modifier function.
   *
   * @param icon                      Icon to display.
   * @param clipboardModifierFunction Function to modify the text before it is copied to the clipboard.
   */
  public ClipboardCopyButton(Icon icon, SerializableUnaryOperator<String> clipboardModifierFunction) {
    this(null, icon, null, clipboardModifierFunction);
  }

  /**
   * Creates the button with no text and the given icon and tooltip.
   *
   * @param icon    Icon to display.
   * @param tooltip Tooltip to show.
   */
  public ClipboardCopyButton(Icon icon, String tooltip) {
    this(null, icon, tooltip);
  }

  /**
   * Creates the button with no text and the given icon and tooltip, with the given modifier function.
   *
   * @param icon                      Icon to display.
   * @param tooltip                   Tooltip to show.
   * @param clipboardModifierFunction Function to modify the text before it is copied to the clipboard.
   */
  public ClipboardCopyButton(Icon icon, String tooltip, SerializableUnaryOperator<String> clipboardModifierFunction) {
    this(null, icon, tooltip, clipboardModifierFunction);
  }

  /**
   * Creates the button with no tooltip and the given text and icon.
   *
   * @param text Text on the button.
   * @param icon Icon to display.
   */
  public ClipboardCopyButton(String text, Icon icon) {
    this(text, icon, null, SerializableUnaryOperator.identity());
  }

  /**
   * Creates the button with no tooltip and the given text and icon, with the given modifier function.
   *
   * @param text                      Text on the button.
   * @param icon                      Icon to display.
   * @param clipboardModifierFunction Function to modify the text before it is copied to the clipboard.
   */
  public ClipboardCopyButton(String text, Icon icon, SerializableUnaryOperator<String> clipboardModifierFunction) {
    this(text, icon, null, clipboardModifierFunction);
  }

  /**
   * Creates the button with given text, icon and tooltip.
   *
   * @param text    Text on the button.
   * @param icon    Icon to display.
   * @param tooltip Tooltip to show.
   */
  public ClipboardCopyButton(String text, Icon icon, String tooltip) {
    this(text, icon, tooltip, SerializableUnaryOperator.identity());
  }

  /**
   * Creates the button with given text, icon and tooltip.
   *
   * @param text    Text on the button.
   * @param icon    Icon to display.
   * @param tooltip Tooltip to show.
   */
  public ClipboardCopyButton(String text, Icon icon, String tooltip, SerializableUnaryOperator<String> clipboardModifierFunction) {
    super();
    // handle events from client
    this.getElement()
        .addEventListener(EVENT_NAME, event -> {
          this.onValueCopied(
              event.getEventData().get(RAW_VALUE_EVENT_PATH).asString(),
              event.getEventData().get(COPIED_VALUE_EVENT_PATH).asString()
          );
        })
        .addEventData(COPIED_VALUE_EVENT_PATH)
        .addEventData(RAW_VALUE_EVENT_PATH)
        .setDisabledUpdateMode(DisabledUpdateMode.ALWAYS);
    // set up
    this.setClipboardModifierFunction(clipboardModifierFunction);
    this.setText(text);
    this.setIcon(icon);
    this.setTooltipText(tooltip);
  }

  /**
   * Returns the current clipboard modifier function.
   *
   * @return A non-{@code null} function.
   */
  public final SerializableUnaryOperator<String> getClipboardModifierFunction() {
    return this.clipboardModifierFunction;
  }

  /**
   * Sets the client modifier function.
   * This function will be applied to the selection and whatever it returns, will be copied to the clipboard.
   *
   * @param clipboardModifierFunction A non-{@code null} function. If {@code null} is passed, {@link SerializableUnaryOperator#identity()} will be used.
   */
  public final void setClipboardModifierFunction(SerializableUnaryOperator<String> clipboardModifierFunction) {
    this.clipboardModifierFunction = Objects.requireNonNullElseGet(clipboardModifierFunction, SerializableUnaryOperator::identity);
  }

  /**
   * Chains {@link #setClipboardModifierFunction(SerializableUnaryOperator)} and returns itself.
   *
   * @param clipboardModifierFunction A non-{@code null} function. If {@code null} is passed, {@link SerializableUnaryOperator#identity()} will be used.
   * @return This.
   * @see #setClipboardModifierFunction(SerializableUnaryOperator)
   */
  public ClipboardCopyButton withClipboardModifierFunction(SerializableUnaryOperator<String> clipboardModifierFunction) {
    this.setClipboardModifierFunction(clipboardModifierFunction);
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Registration addClipboardCopyListener(ClipboardCopyListener<ClipboardCopyButton> listener) {
    return this.getEventBus().addListener((Class<ClipboardCopyEvent<ClipboardCopyButton>>) (Class<?>) ClipboardCopyEvent.class, listener);
  }

  /**
   * Called when the actual clicking on the client happens. Bypasses disabledness.
   *
   * @param raw   Raw value, before modification.
   * @param value Copied value, after modification.
   */
  private void onValueCopied(String raw, String value) {
    this.fireEvent(new ClipboardCopyEvent<>(this, raw, value));
  }

  // this is called to modify the selection and replace it if requested
  @ClientCallable(DisabledUpdateMode.ALWAYS)
  private String processText(String value) {
    return this.clipboardModifierFunction.apply(value);
  }

  @Override
  public void setText(String text) {
    this.getElement().setProperty(TEXT_PROPERTY, text);
  }

  @Override
  public String getText() {
    return this.getElement().getProperty(TEXT_PROPERTY);
  }

  @Override
  public void setIcon(Icon icon) {
    if (this.icon != null)
      this.getElement().removeChild(this.icon.getElement());
    this.icon = icon;
    if (this.icon != null) {
      this.icon.getElement().setAttribute("slot", ICON_SLOT_NAME);
      this.getElement().appendChild(this.icon.getElement());
    }
  }

  @Override
  public Icon getIcon() {
    return this.icon;
  }

  @Override
  public Tooltip setTooltipText(String text) {
    // without this explicit setup the tooltip is not rendered
    final Tooltip result = Tooltip.forComponent(this);
    result.setText(text);
    return result;
  }

  @Override
  public Tooltip setTooltipMarkdown(String markdown) {
    final Tooltip result = Tooltip.forComponent(this);
    result.setMarkdown(markdown);
    return result;
  }
}
