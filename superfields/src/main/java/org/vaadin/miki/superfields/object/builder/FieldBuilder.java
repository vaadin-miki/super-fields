package org.vaadin.miki.superfields.object.builder;

import com.vaadin.flow.component.HasValue;
import org.vaadin.miki.superfields.object.Property;

import java.io.Serializable;

/**
 * Marker interface for {@link SimplePropertyComponentBuilder} for objects that build individual components.
 * @param <P> Value type of the property.
 */
@FunctionalInterface
public interface FieldBuilder<P> extends Serializable {

    /**
     * Builds a field for a given property.
     * @param property Property.
     * @return A component capable of displaying the value of the property.
     * @implSpec The returned object must also be a {@link com.vaadin.flow.component.Component}.
     */
    HasValue<?, P> buildPropertyField(Property<?, P> property);

}
