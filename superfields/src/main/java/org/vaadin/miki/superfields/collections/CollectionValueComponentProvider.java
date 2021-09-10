package org.vaadin.miki.superfields.collections;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;

/**
 * Interface for objects providing a component capable of displaying a single element of a {@link CollectionField}.
 * It enforces the generated component to implement {@link HasValue}.
 *
 * @param <T> Type of data to display.
 * @param <C> Type of generated component. Not the same as the {@code C} in {@link CollectionField}.
 *
 * @author miki
 * @since 2021-09-10
 */
@FunctionalInterface
public interface CollectionValueComponentProvider<T, C extends Component & HasValue<?, T>> extends CollectionComponentProvider<C> {

}
