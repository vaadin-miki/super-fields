package org.vaadin.miki.markers;

import org.vaadin.miki.shared.text.TextInputMode;

/**
 * Mixin interface for {@link HasTextInputMode}.
 *
 * @author miki
 * @since 2023-04-21
 */
@SuppressWarnings("squid:S119") // SELF is a good generic name
public interface WithTextInputModeMixin<SELF extends WithTextInputModeMixin<SELF>> extends HasTextInputMode {

  /**
   * Chains {@link #setTextInputMode(TextInputMode)} and returns itself.
   * @param mode New text input mode.
   * @return This.
   */
  @SuppressWarnings("unchecked") // should be fine
  default SELF withTextInputMode(TextInputMode mode) {
    this.setTextInputMode(mode);
    return (SELF) this;
  }

}
