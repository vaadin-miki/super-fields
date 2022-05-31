package org.vaadin.miki.superfields.variant;

import java.util.List;

/**
 * @author miki
 * @since 2022-05-19
 */
@FunctionalInterface
public interface ObjectPropertyDefinitionProvider<T> {

    List<ObjectPropertyDefinition<T, ?>> getObjectPropertyDefinitions(Class<T> type, T instance);

}
