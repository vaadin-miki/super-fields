package org.vaadin.miki.superfields.componentselect;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableBiFunction;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Contains helpers to be used with {@link ComponentSelect}.
 *
 * @author miki
 * @since 2023-11-17
 */
public final class ComponentSelectHelpers {

  /**
   * Returns a consumer that changes the style of the component from a given style to another style.
   *
   * @param fromStyle Style to change from (will be removed from the style class names of the component).
   * @param toStyle   Style to change to (will be added to the style class names of the component).
   * @param <C>       Type of the component.
   * @return A {@link SerializableBiConsumer}.
   */
  public static <C extends Component & ClickNotifier<C> & HasStyle> SerializableBiConsumer<Integer, C> changeStyle(final String fromStyle, final String toStyle) {
    return (index, component) -> {
      if (fromStyle != null && !fromStyle.isEmpty())
        component.removeClassName(fromStyle);
      if (toStyle != null && !toStyle.isEmpty())
        component.addClassName(toStyle);
    };
  }

  /**
   * Returns a simple component factory that uses {@link Object#toString()} to produce component text.
   * @param constructor Reference to a constructor of a component.
   * @return A {@link SerializableBiFunction} that returns components with caption.
   * @param <T> Type of item.
   * @param <C> Type of component.
   */
  public static <T, C extends Component & ClickNotifier<C> & HasText> SerializableBiFunction<Integer, T, C> simpleComponentFactory(final Supplier<C> constructor) {
    return simpleComponentFactory(constructor, Object::toString);
  }

  /**
   * Returns a simple component factory.
   * @param constructor Reference to a constructor of a component.
   * @param captionGenerator Function to generate captions.
   * @return A {@link SerializableBiFunction} that returns components with caption.
   * @param <T> Type of item.
   * @param <C> Type of component.
   */
  public static <T, C extends Component & ClickNotifier<C> & HasText> SerializableBiFunction<Integer, T, C> simpleComponentFactory(final Supplier<C> constructor, final Function<T, String> captionGenerator) {
    return (index, t) -> {
      final C result = constructor.get();
      result.setText(captionGenerator.apply(t));
      return result;
    };
  }

  private ComponentSelectHelpers() {
    // no instances allowed
  }

}
