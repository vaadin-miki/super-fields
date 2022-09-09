package org.vaadin.miki.superfields.object;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;

import java.io.Serializable;
import java.util.Optional;

/**
 * Builds a component that corresponds to a given object property.
 *
 * @author miki
 * @since 2022-05-19
 */
@FunctionalInterface
public interface PropertyComponentBuilder extends Serializable {

    /**
     * Builds a component for a given property.
     * @param property Property to build a component for.
     * @return A component capable of displaying the value of the given property, if any.
     * @param <P> Type of the value of the property.
     * @param <C> Component to be returned.
     */
    <P, C extends Component & HasValue<?, P>> Optional<C> buildPropertyField(Property<?, P> property);

}
