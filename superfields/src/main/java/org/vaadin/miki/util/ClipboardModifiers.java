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

  public static String toUpperCase(String value) {
    return value.toUpperCase();
  }

  public static String toLowerCase(String value) {
    return value.toLowerCase();
  }

  public static SerializableUnaryOperator<String> replaceWithValue(HasValue<?, String> component) {
    return text -> component.getValue();
  }

  public static <T> SerializableUnaryOperator<String> replaceWithValue(HasValue<?, T> component, Function<T, String> converter) {
    return text -> converter.apply(component.getValue());
  }

  private ClipboardModifiers() {
    // no instances allowed
  }

}
