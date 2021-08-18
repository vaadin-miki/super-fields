package org.vaadin.miki.superfields.collections;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;

/**
 * Interface for objects providing a layout for any {@link CollectionField}.
 * @param <C> Component provided by this object.
 *
 * @author miki
 * @since 2021-08-11
 */
@FunctionalInterface
public interface LayoutProvider<C extends Component & HasComponents> {

    /**
     * Provides a new layout to which components (elements of the collection) will be added.
     * @param controller A {@link CollectionController} to be used for callbacks.
     * @return A non-{@code null} layout.
     */
    C provideLayout(CollectionController controller);

}
