package org.vaadin.miki.superfields.collections;

import com.vaadin.flow.component.Component;

import java.io.Serializable;

@FunctionalInterface
public interface CollectionComponentProvider<C extends Component> extends Serializable {

    /**
     * Constructs the component for an object at given index.
     * @param index Current index of {@code object} in the collection.
     * @param controller A {@link CollectionController} to be used for callbacks.
     * @return Non-{@code null} component that will be used to display {@code object}.
     */
    C provideComponent(int index, CollectionController controller);

}
