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
public interface CollectionLayoutProvider<C extends Component & HasComponents> extends CollectionComponentProvider<C> {

}
