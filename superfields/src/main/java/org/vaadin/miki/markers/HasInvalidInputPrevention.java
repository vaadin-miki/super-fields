package org.vaadin.miki.markers;

/**
 * Marker interface for components that are capable of preventing invalid input.
 *
 * @author miki
 * @since 2023-06-05
 */
public interface HasInvalidInputPrevention {

  /**
   * Modifies the state of the component when it comes to preventing invalid input.
   * When the flag is set to {@code true} and the current value is invalid, it will be cleared.
   *
   * @param prevent Whether to prevent invalid input.
   */
  void setPreventingInvalidInput(boolean prevent);

  /**
   * Checks if this component is actively preventing invalid input.
   * @return {@code true} when entering invalid input is prevented, {@code false} otherwise and by default.
   */
  boolean isPreventingInvalidInput();

}
