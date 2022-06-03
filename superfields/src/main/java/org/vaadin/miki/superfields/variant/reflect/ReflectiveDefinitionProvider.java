package org.vaadin.miki.superfields.variant.reflect;

import org.vaadin.miki.superfields.variant.ObjectPropertyDefinition;
import org.vaadin.miki.superfields.variant.ObjectPropertyDefinitionProvider;
import org.vaadin.miki.util.ReflectTools;

import java.lang.reflect.Field;
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
        return ReflectTools.extractProperties(type, type.isAnnotationPresent(DoNotScanSuperclasses.class)).entrySet()
                .stream()
                .filter(fieldEntry -> !fieldEntry.getKey().isAnnotationPresent(Ignore.class))
                .map(fieldEntry -> this.buildDefinition(type, fieldEntry.getKey(), fieldEntry.getValue()[ReflectTools.GETTER_INDEX], fieldEntry.getValue()[ReflectTools.SETTER_INDEX] ))
                .collect(Collectors.toList());
    }

    private <T> ObjectPropertyDefinition<T, ?> buildDefinition(Class<T> type, Field field, Method getter, Method setter) {

    }
}
