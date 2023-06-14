package org.vaadin.miki.markers;

/**
 * Mixin interface for {@link HasInvalidInputPrevention}.
 *
 * @author miki
 * @since 2023-06-05
 */
@SuppressWarnings("squid:S119") // SELF is a fine generic name that is more descriptive than S
public interface WithInvalidInputPreventionMixin<SELF extends HasInvalidInputPrevention> extends HasInvalidInputPrevention {

  /**
   * Chains {@link #setPreventingInvalidInput(boolean)} and returns itself.
   *
   * @param prevent Whether to prevent invalid input.
   * @return This.
   * @see #setPreventingInvalidInput(boolean)
   */
  @SuppressWarnings("unchecked")
  default SELF withPreventingInvalidInput(boolean prevent) {
    this.setPreventingInvalidInput(prevent);
    return (SELF) this;
  }

  /**
   * Chains {@link #setPreventingInvalidInput(boolean)} called with {@code true} and returns itself.
   * @return This.
   * @see #setPreventingInvalidInput(boolean)
   */
  default SELF withPreventingInvalidInput() {
    return this.withPreventingInvalidInput(true);
  }

  /**
   * Chains {@link #setPreventingInvalidInput(boolean)} called with {@code false} and returns itself.
   * @return This.
   * @see #setPreventingInvalidInput(boolean)
   */
  default SELF withoutPreventingInvalidInput(){
    return this.withPreventingInvalidInput(false);
  }

}
