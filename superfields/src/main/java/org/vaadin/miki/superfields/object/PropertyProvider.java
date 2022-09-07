package org.vaadin.miki.superfields.object;

import java.io.Serializable;
import java.util.List;

/**
 * Marker interface for objects that provide a list of properties of that object (see {@link Property}).
 *
 * @author miki
 * @since 2022-05-19
 */
@FunctionalInterface
public interface PropertyProvider extends Serializable {

    /**
     * Builds a list of property definitions for a given object.
     * @param type Type of the object.
     * @param instance An instance of the object. Can be {@code null}.
     * @param <T> Generic type to enforce both {@code type} and {@code instance} are compatible.
     * @return A non-{@code null} list of {@link Property}s that can be empty.
     * @implNote This method should be fast, as it will be called often. It is encouraged to cache the results if possible.
     */
    <T> List<Property<T, ?>> getObjectPropertyDefinitions(Class<T> type, T instance);

}
