package org.vaadin.miki.util;

import java.io.Serializable;
import java.util.function.UnaryOperator;

/**
 * A {@link UnaryOperator} that is also {@link Serializable}.
 *
 * @author miki
 * @since 2026-11-02
 */
@FunctionalInterface
public interface SerializableUnaryOperator<T> extends UnaryOperator<T>, Serializable {

  static <X> SerializableUnaryOperator<X> identity() {
    return x -> x;
  }

}
