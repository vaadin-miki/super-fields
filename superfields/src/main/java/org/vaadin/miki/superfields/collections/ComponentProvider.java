package org.vaadin.miki.superfields.collections;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;

import java.io.Serializable;

/**
 * Interface for objects providing a component capable of displaying a single element of a {@link CollectionField}.
 * @param <T> Type of the element to display.
 * @param <C> Type of generated component.
 */
@FunctionalInterface
public interface ComponentProvider<T, C extends Component & HasValue<?, T>> extends Serializable {

    /**
     * Constructs the component for an object at given index.
     * Note: {@link HasValue#setValue(Object)} will be called on the returned object.
     * @param index Current index of {@code object} in the collection.
     * @param controller A {@link CollectionController} to be used for callbacks.
     * @return Non-{@code null} component that will be used to display {@code object}.
     */
    C provideComponent(int index, CollectionController controller);

}
