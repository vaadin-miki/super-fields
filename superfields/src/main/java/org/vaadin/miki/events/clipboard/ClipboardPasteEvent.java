package org.vaadin.miki.events.clipboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

import java.util.Objects;

/**
 * An even marking the clicking of the "paste from clipboard" button.
 *
 * @param <C> Source component type.
 * @author miki
 * @since 2026-11-02
 */
public class ClipboardPasteEvent<C extends Component> extends ComponentEvent<C> {

  private final String pastedValue;

  /**
   * Creates the event.
   *
   * @param source Source button.
   * @param value  Value that was pasted.
   */
  public ClipboardPasteEvent(C source, String value) {
    // those have to always be initiated by the client (i.e. the user has to do it in the browser)
    super(source, true);
    this.pastedValue = value;
  }

  /**
   * The value that was pasted, as a string.
   *
   * @return The pasted value (as a string).
   */
  public String getPastedValue() {
    return pastedValue;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ClipboardPasteEvent that = (ClipboardPasteEvent) o;
    return Objects.equals(this.pastedValue, that.pastedValue);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(pastedValue);
  }

  @Override
  public String toString() {
    return "ClipboardPasteEvent{" +
        "pastedValue='" + pastedValue + '\'' +
        '}';
  }
}
