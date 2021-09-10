package org.vaadin.miki.superfields.collections;

import com.vaadin.flow.component.Component;

import java.io.Serializable;

/**
 * Catch-all interface for everything related to producing {@link Component}s for a {@link CollectionField}.
 * @param <C> Type of component provided.
 * @author miki
 * @since 2021-09-10
 */
@FunctionalInterface
public interface CollectionComponentProvider<C extends Component> extends Serializable {

    /**
     * Constructs the component for an object at given index.
     * @param index Current index of {@code object} in the collection.
     * @param controller A {@link CollectionController} to be used for callbacks.
     * @return Non-{@code null} component.
     */
    C provideComponent(int index, CollectionController controller);

}
