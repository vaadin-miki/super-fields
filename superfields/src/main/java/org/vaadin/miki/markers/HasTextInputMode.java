package org.vaadin.miki.markers;

import org.vaadin.miki.shared.text.TextInputMode;

/**
 * Marker interface for objects that have a text input mode.
 *
 * @author miki
 * @since 2023-04-21
 */
public interface HasTextInputMode {

  /**
   * Changes the text input mode of this object.
   * @param inputMode New input mode. Can be {@code null}.
   */
  void setTextInputMode(TextInputMode inputMode);

  /**
   * Returns the current text input mode of this object.
   * @return A {@link TextInputMode}, or {@code null} if none has been set.
   */
  TextInputMode getTextInputMode();

}
