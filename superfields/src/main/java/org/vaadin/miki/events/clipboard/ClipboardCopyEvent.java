package org.vaadin.miki.events.clipboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

import java.util.Objects;

/**
 * Event for copy-to-clipboard.
 *
 * @param <C> Source component.
 * @author miki
 * @since 2026-11-02
 */
public class ClipboardCopyEvent<C extends Component> extends ComponentEvent<C> {

  private final String valueBeforeModification;
  private final String copiedValue;

  /**
   * Constructs the event.
   *
   * @param source                  Source component.
   * @param valueBeforeModification Value as it was before modification, if any.
   * @param copiedValue             Value as it was pasted to clipboard.
   */
  public ClipboardCopyEvent(C source, String valueBeforeModification, String copiedValue) {
    // always initiated by the client
    super(source, true);
    this.valueBeforeModification = valueBeforeModification;
    this.copiedValue = copiedValue;
  }

  /**
   * The value before modification, if any.
   *
   * @return Raw value that was selected by the user when the button was pressed.
   */
  public String getValueBeforeModification() {
    return valueBeforeModification;
  }

  /**
   * The value copied to the clipboard.
   *
   * @return A non-{@code null} value copied to clipboard.
   */
  public String getCopiedValue() {
    return copiedValue;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ClipboardCopyEvent<?> that = (ClipboardCopyEvent<?>) o;
    return Objects.equals(valueBeforeModification, that.valueBeforeModification) && Objects.equals(copiedValue, that.copiedValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(valueBeforeModification, copiedValue);
  }

  @Override
  public String toString() {
    return "ClipboardCopyEvent{" +
        "valueBeforeModification='" + valueBeforeModification + '\'' +
        ", copiedValue='" + copiedValue + '\'' +
        '}';
  }
}
