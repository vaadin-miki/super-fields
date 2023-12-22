package org.vaadin.miki.markers;

/**
 * Marker interface for components that have a placeholder text.
 * This is a workaround for <a href="https://github.com/vaadin/flow/issues/4068">issue #4068 in Vaadin Flow</a>.
 * @author miki
 * @since 2020-04-07
 */
public interface HasPlaceholder {

  /**
   * Returns current placeholder text for this object.
   * @return Current placeholder text. Can be {@code null}, meaning no placeholder.
   */
  String getPlaceholder();

  /**
   * Sets the placeholder text for this object.
   * @param placeholder Placeholder text. Can be {@code null}, meaning no placeholder.
   */
  void setPlaceholder(String placeholder);

}
