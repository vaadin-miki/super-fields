package org.vaadin.miki.superfields.variant.reflect;

import org.vaadin.miki.superfields.variant.ObjectPropertyDefinition;
import org.vaadin.miki.superfields.variant.ObjectPropertyDefinitionProvider;
import org.vaadin.miki.util.ReflectTools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple reflection-based definition provider.
 * It scans a given type for private fields (including final) and corresponding setters and/or getters.
 * Superclasses are by default included.
 *
 * @author miki
 * @since 2022-06-03
 */
public class ReflectiveDefinitionProvider implements ObjectPropertyDefinitionProvider {

    private final Map<Class<?>, List<ObjectPropertyDefinition<?, ?>>> cache = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked") // should be fine
    public <T> List<ObjectPropertyDefinition<T, ?>> getObjectPropertyDefinitions(Class<T> type, T instance) {
        return (List<ObjectPropertyDefinition<T,?>>)(List<?>) this.cache.computeIfAbsent(type, t -> (List<ObjectPropertyDefinition<?,?>>)(List<?>) this.buildProperties(t));
    }

    private <T> List<ObjectPropertyDefinition<T, ?>> buildProperties(Class<T> type) {
        return ReflectTools.extractFieldsWithMethods(type, type.isAnnotationPresent(DoNotScanSuperclasses.class)).entrySet()
                .stream()
                .filter(fieldEntry -> !fieldEntry.getKey().isAnnotationPresent(Ignore.class))
                .map(fieldEntry -> this.buildDefinition(type, fieldEntry.getKey(), fieldEntry.getKey().getType(), fieldEntry.getValue()[ReflectTools.GETTER_INDEX], fieldEntry.getValue()[ReflectTools.SETTER_INDEX] ))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked") // P is the type of field, so all well here
    private <T, P> ObjectPropertyDefinition<T, P> buildDefinition(Class<T> type, Field field, Class<P> fieldType, Method getter, Method setter) {
        return new ObjectPropertyDefinition<>(type, field.getName(), fieldType,
                setter == null ? (t, p) -> {} :
                (t, o) -> {
                    try {
                        setter.invoke(t, o);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new IllegalStateException();
                    }
                },
                getter == null ? t -> null :
                t -> {
                    try {
                        return (P) getter.invoke(t);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new IllegalStateException();
                    }
                }
                );
    }
}
