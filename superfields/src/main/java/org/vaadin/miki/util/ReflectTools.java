package org.vaadin.miki.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
     * This method will attempt to find a field in the class of the given object, and then it will go up the hierarchy until the field of given name and a compatible type is found.
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

    private static Optional<Method> findGetter(Field field, Class<?> type) {
        for(String prefix: Objects.equals(boolean.class, field.getType()) || Objects.equals(Boolean.class, field.getType()) ? new String[]{"is", "get", "are"} : new String[]{"get"}) {
            try {
                final Method method = type.getMethod(prefix + field.getName().substring(0, 1).toUpperCase(Locale.ROOT) + field.getName().substring(1));
                // compatible type, public and not static
                if(method.getReturnType().isAssignableFrom(field.getType()) && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()))
                    return Optional.of(method);
            } catch (NoSuchMethodException e) {
                // method not found, ignore and carry on
            }
        }
        return Optional.empty();
    }

    private static Optional<Method> findSetter(Field field, Class<?> type) {
        final String setterName = "set" + field.getName().substring(0, 1).toUpperCase(Locale.ROOT) + field.getName().substring(1);
        for(Method method: type.getMethods()) {
            if(setterName.equals(method.getName()) && method.getParameterCount() == 1 && field.getType().isAssignableFrom(method.getParameterTypes()[0]) && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()))
                return Optional.of(method);
        }
        return Optional.empty();
    }

    /**
     * Scans a class using reflection and associates fields with getters (first) and setters (second).
     * First, it scans all the fields in the given class, then moves up the hierarchy if told to and scans fields on the way.
     * Then, it attempts to find matching {@code public}, non-{@code static} getters/setters in {@code type}, and returns those.
     * Setters must start with {@code set}, getters with {@code get} ({@code is} and {@code are} supported for booleans).
     * Return type of the getter must be the same as the field's type or one of its superclasses or superinterfaces.
     * The only parameter of the setter must either be the same type as the field's type or one of its subclasses.
     * Fields that have no setter and no getter are stripped from the result.
     * In case of duplicate names of fields only the first occurrence is kept.
     *
     * @param type Type to scan.
     * @param ignoreSuperclasses Whether to ignore superclasses (all the way until {@link Object}.
     * @return A non-{@code null} {@link Map} that associates a {@link Field} with a corresponding getter method and a setter method (either can be {@code null}).
     */
    public static Map<Field, Method[]> extractFieldsWithMethods(Class<?> type, boolean ignoreSuperclasses) {
        final Map<Field, Method[]> result = new HashMap<>();
        if(type != null) {
            Class<?> toScan = type;
            // 1. scan all declared fields
            while (toScan != null && !Objects.equals(toScan, Object.class)) {
                for (Field declaredField : toScan.getDeclaredFields())
                    result.putIfAbsent(declaredField, new Method[2]);
                toScan = ignoreSuperclasses ? Object.class : toScan.getSuperclass();
            }

            final Set<Field> fieldsWithoutAccessors = new HashSet<>();

            result.forEach((field, methods) -> {
                // 2. find getter and setter
                findGetter(field, type).ifPresent(method -> methods[GETTER_INDEX] = method);
                findSetter(field, type).ifPresent(method -> methods[SETTER_INDEX] = method);
                // 3. mark for removal if no accessors
                if (methods[GETTER_INDEX] == null && methods[SETTER_INDEX] == null)
                    fieldsWithoutAccessors.add(field);
            });

            fieldsWithoutAccessors.forEach(result::remove);
        }
        return result;
    }

    /**
     * Extracts the class present as the given parameter in the generic definition of the type in the given field.
     * For example, if field is of type {@code List<String>}, the generic type at index {@code zero} will be {@code String.class}.
     * @param field Field to check.
     * @param genericIndex Index of the type to return.
     * @return The type, if present or found.
     */
    public static Optional<Class<?>> extractGenericType(Field field, int genericIndex) {
        if(field.getGenericType() instanceof ParameterizedType) {
            final Type[] params = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
            return (genericIndex < 0 || genericIndex >= params.length) ? Optional.empty() : Optional.of((Class<?>) params[genericIndex]);
        }
        else return Optional.empty();
    }

    /**
     * Creates an instance of a given type through a publicly accessible no-arg constructor.
     *
     * @param type Type to create.
     * @return An instance of a given type.
     * @param <T> Type of object to create.
     * @throws IllegalArgumentException when the object cannot be created for any reason
     */
    public static <T> T newInstance(Class<T> type) {
        try {
            return type.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("cannot create an instance of "+type.getName(), e);
        }
    }

    private ReflectTools() {
        // instances not allowed
    }
}
