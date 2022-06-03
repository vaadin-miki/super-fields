package org.vaadin.miki.superfields.variant;

import java.util.List;

/**
 * Marker interface for objects that provide property definitions.
 *
 * @author miki
 * @since 2022-05-19
 */
@FunctionalInterface
public interface ObjectPropertyDefinitionProvider {

    /**
     * Builds a list of property definitions for a given object.
     * @param type Type of the object.
     * @param instance An instance of the object. Can be {@code null}.
     * @param <T> Generic type to enforce both {@code type} and {@code instance} are compatible.
     * @return A non-{@code null} list of {@link ObjectPropertyDefinition}s that can be empty.
     * @implNote This method should be fast, as it will be called often. It is encouraged to cache the results if possible.
     */
    <T> List<ObjectPropertyDefinition<T, ?>> getObjectPropertyDefinitions(Class<T> type, T instance);

}
