package org.vaadin.miki.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A collection of static methods to access things through reflection.
 * @author miki
 * @since 2020-05-03
 */
public final class ReflectTools {

    public static final int GETTER_INDEX = 0;
    public static final int SETTER_INDEX = 1;

    /**
     * Attempts to get the value of a {@link Field} of a given name that is declared in given class. {@link Field#trySetAccessible()} will be used.
     * This method will attempt to find a field in the class of the given object and then it will go up the hierarchy until the field of given name and compatible type is found.
     * When a field is found, an attempt to {@code trySetAccessible} will be made, followed by casting it to the provided value type.
     * When all the above is successful, an optional with the value of the field for the given object will be returned.
     *
     * @param instance Instance of an object to get the field from.
     * @param valueType Type of value to expect from the field. Type of the field, if found, will be passed to this type's {@link Class#isAssignableFrom(Class)} for type checking.
     * @param fieldName Name of the field.
     * @param <V> Type of the returned object.
     * @return Value, if successfully obtained. Otherwise, {@link Optional#empty()}.
     */
    public static <V> Optional<V> getValueOfField(Object instance, Class<? extends V> valueType, String fieldName) {
        Class<?> typeToCheck = instance.getClass();
        Field field = null;
        // keep looking all the way up the hierarchy
        while(typeToCheck != null) {
            try {
                field = typeToCheck.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                typeToCheck = typeToCheck.getSuperclass();
                // nothing happens
            }
            // the field must be found, and it must be of the correct type - then stop
            if(field != null && valueType.isAssignableFrom(field.getType()))
                typeToCheck = null;
        }
        if(field != null && field.trySetAccessible()) {
            try {
                return Optional.ofNullable(valueType.cast(field.get(instance)));
            } catch (IllegalAccessException | ClassCastException e) {
                // ignored
            }
        }
        return Optional.empty();
    }

    /**
     * Scans a class using reflection and associates fields with getters (first) and setters (second).
     *
     * @param type Type to scan.
     * @param ignoreSuperclasses Whether to ignore superclasses (all the way until {@link Object}.
     * @return A non-{@code null} {@link Map} that associates a {@link Field} with a corresponding getter method and a setter method (either can be {@code null}).
     */
    public static Map<Field, Method[]> extractProperties(Class<?> type, boolean ignoreSuperclasses) {
        final Map<Field, Method[]> result = new HashMap<>();
        return result;
    }

    private ReflectTools() {
        // instances not allowed
    }
}
