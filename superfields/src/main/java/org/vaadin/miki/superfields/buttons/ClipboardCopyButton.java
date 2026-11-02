package org.vaadin.miki.superfields.buttons;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.DisabledUpdateMode;
import com.vaadin.flow.shared.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.miki.events.clipboard.ClipboardCopyEvent;
import org.vaadin.miki.events.clipboard.ClipboardCopyListener;
import org.vaadin.miki.events.clipboard.ClipboardCopyNotifier;
import org.vaadin.miki.util.SerializableUnaryOperator;

import java.util.Objects;
import java.util.function.Function;

/**
 * A button that can always be clicked, no matter the state it is in.
 *
 * @author miki
 * @since 2025-08-16
 */
@Tag("clipboard-copy-button")
@JsModule("./clipboard-copy-button.ts")
public class ClipboardCopyButton extends Div implements ClipboardCopyNotifier<ClipboardCopyButton> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClipboardCopyButton.class);

  private SerializableUnaryOperator<String> clipboardModifierFunction = SerializableUnaryOperator.identity();

  public ClipboardCopyButton() {
    super();
    this.getElement()
        .addEventListener("copy-pressed", event -> {
          LOGGER.info("details: {}", event.getEventData().toPrettyString());
          this.onValueCopied(
              event.getEventData().get("event.detail.raw").asString(),
              event.getEventData().get("event.detail.value").asString()
          );
        })
        .addEventData("event.detail.value").addEventData("event.detail.raw")
        .setDisabledUpdateMode(DisabledUpdateMode.ALWAYS);
  }

  public final Function<String, String> getClipboardModifierFunction() {
    return this.clipboardModifierFunction;
  }

  public final void setClipboardModifierFunction(SerializableUnaryOperator<String> clipboardModifierFunction) {
    this.clipboardModifierFunction = Objects.requireNonNullElseGet(clipboardModifierFunction, SerializableUnaryOperator::identity);
  }

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
    LOGGER.info("about to fire event with {} and {}", raw, value);
    this.fireEvent(new ClipboardCopyEvent<>(this, raw, value));
  }

  // this is called to modify the selection and replace it if requested
  @ClientCallable(DisabledUpdateMode.ALWAYS)
  private String processText(String value) {
    LOGGER.info("processing text: {}", value);
    return this.clipboardModifierFunction.apply(value);
  }

}
