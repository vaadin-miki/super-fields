package org.vaadin.miki.superfields.variant;

import com.vaadin.flow.component.HasValue;

public interface ObjectPropertyComponentConfigurator<T> {

    void configureComponent(T object, ObjectPropertyDefinition<T, ?> definition, HasValue<?, ?> component);

}
