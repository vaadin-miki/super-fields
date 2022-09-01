package org.vaadin.miki.superfields.variant;

import com.vaadin.flow.component.HasValue;

import java.util.List;

/**
 * Configures an entire group of components.
 * @author miki
 * @since 2022-09-01
 */
public interface ObjectPropertyComponentGroupConfigurator {

    <T> void configureComponentGroup(T object, String groupName, List<ObjectPropertyDefinition<T, ?>> definitions, List<? extends HasValue<?, ?>> components);

}
