package org.vaadin.miki.superfields.variant;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;

public interface ObjectPropertyComponentConfigurator {

    <T, P, C extends Component & HasValue<?, P>> void configureComponent(T object, ObjectPropertyDefinition<T, P> definition, C component);

}
