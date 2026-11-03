package org.vaadin.miki.util;

import com.vaadin.flow.component.HasValue;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Contains a number of modifiers of clipboard values.
 *
 * @author miki
 * @since 2026-11-02
 */
public class ClipboardModifiers implements Serializable {

  /**
   * A modifier that makes no changes to the data.
   */
  public static final SerializableUnaryOperator<String> NO_CHANGES = SerializableUnaryOperator.identity();

  /**
   * Returns a {@link SerializableUnaryOperator} that replaces any text with contents of a text field.
   * @param component Component to obtain the value from.
   * @return A {@link SerializableUnaryOperator}.
   */
  public static SerializableUnaryOperator<String> replaceWithValue(HasValue<?, String> component) {
    return text -> component.getValue();
  }

  /**
   * Returns a {@link SerializableUnaryOperator} that replaces any text with contents of the given field, using the given converter.
   * @param component Component to obtain the value from.
   * @param converter Converter used to change the value to {@link String}.
   * @return A {@link SerializableUnaryOperator}.
   * @param <T> Type of data returned by the field.
   */
  public static <T> SerializableUnaryOperator<String> replaceWithValue(HasValue<?, T> component, Function<T, String> converter) {
    return text -> converter.apply(component.getValue());
  }

  /**
   * Returns a {@link SerializableUnaryOperator} that wraps any text with prefix and suffix, producing {@code prefix + text + suffix}, with both prefix and suffix optional.
   * If both prefix and suffix are not given (or empty), {@link ClipboardModifiers#NO_CHANGES} is returned.
   * @param prefix Prefix to use. Can be {@code null} or empty to ignore.
   * @param suffix Suffix to use. Can be {@code null} or empty to ignore.
   * @return A {@link SerializableUnaryOperator}.
   */
  public static SerializableUnaryOperator<String> wrapWith(String prefix, String suffix) {
    final boolean noPrefix = prefix == null || prefix.isEmpty();
    final boolean noSuffix = suffix == null || suffix.isEmpty();
    if(noPrefix && noSuffix)
      return NO_CHANGES;
    else if(noPrefix)
      return text -> text + suffix;
    else if(noSuffix)
      return text -> prefix + text;
    else return text -> prefix + text + suffix;
  }

  private ClipboardModifiers() {
    // no instances allowed
  }

}
