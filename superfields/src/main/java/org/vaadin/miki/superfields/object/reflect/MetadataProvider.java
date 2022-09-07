package org.vaadin.miki.superfields.object.reflect;

import org.vaadin.miki.superfields.object.PropertyMetadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Marker interface for objects that provide {@link PropertyMetadata} for a given field.
 * Used with {@link ReflectivePropertyProvider} to obtain more detailed information about a given property.
 *
 * @author miki
 * @since 2022-09-07
 */
@FunctionalInterface
public interface MetadataProvider {

    /**
     * Collects {@link PropertyMetadata} for a given property.
     * @param name Name of the property.
     * @param field A {@link Field} that corresponds to the property. Will not be {@code null}, but may not be accessible.
     * @param setter A {@link Method} that is a setter for the given field. May be {@code null} if there is no setter.
     * @param getter A {@link Method} that is a getter for the given field. May be {@code null} if there is no getter.
     * @return A non-{@code null}, but possibly empty, collection of {@link PropertyMetadata}.
     */
    Collection<PropertyMetadata> getMetadata(String name, Field field, Method setter, Method getter);

}
