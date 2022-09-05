package org.vaadin.miki.superfields.variant.reflect;

import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableFunction;
import org.vaadin.miki.superfields.variant.ObjectPropertyDefinition;
import org.vaadin.miki.superfields.variant.ObjectPropertyDefinitionProvider;
import org.vaadin.miki.util.ReflectTools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    private final List<MetadataProvider> metadataProviders = new ArrayList<>();

    private boolean usingFakeSettersWhenNotPresent = false;
    private boolean usingFakeGettersWhenNotPresent = false;

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

    private <T, P> SerializableBiConsumer<T, P> getSetterFromMethod(Method method) {
        if(method != null)
            return (t, o) -> {
                try {
                    method.invoke(t, o);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalStateException("cannot access method "+method.getName(), e);
                }
            };
        else if(this.isUsingFakeSettersWhenNotPresent())
            return (t, p) -> {};
        else return null;
    }

    @SuppressWarnings("unchecked") // P is the type of field, so all well here
    private <T, P> SerializableFunction<T, P> getGetterFromMethod(Method method) {
        if(method != null)
            return t -> {
                try {
                    return (P) method.invoke(t);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalStateException();
                }
            };
        else if(this.isUsingFakeGettersWhenNotPresent())
            return t -> null;
        else return null;
    }

    private <T, P> ObjectPropertyDefinition<T, P> buildDefinition(Class<T> type, Field field, Class<P> fieldType, Method getter, Method setter) {
        return new ObjectPropertyDefinition<>(type, field.getName(), fieldType,
                this.getSetterFromMethod(setter),
                this.getGetterFromMethod(getter),
                this.metadataProviders.stream().flatMap(provider -> provider.getMetadata(field.getName(), field, setter, getter).stream()).collect(Collectors.toList())
        );
    }

    public final void addMetadataProvider(MetadataProvider... providers) {
        this.metadataProviders.addAll(Arrays.stream(providers).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    public final ReflectiveDefinitionProvider withMetadataProvider(MetadataProvider... providers) {
        this.addMetadataProvider(providers);
        return this;
    }

    public boolean isUsingFakeSettersWhenNotPresent() {
        return usingFakeSettersWhenNotPresent;
    }

    public void setUsingFakeSettersWhenNotPresent(boolean usingFakeSettersWhenNotPresent) {
        this.usingFakeSettersWhenNotPresent = usingFakeSettersWhenNotPresent;
    }

    public final ReflectiveDefinitionProvider withUsingFakeSettersWhenNotPresent(boolean setting) {
        this.setUsingFakeSettersWhenNotPresent(setting);
        return this;
    }

    public boolean isUsingFakeGettersWhenNotPresent() {
        return usingFakeGettersWhenNotPresent;
    }

    public void setUsingFakeGettersWhenNotPresent(boolean usingFakeGettersWhenNotPresent) {
        this.usingFakeGettersWhenNotPresent = usingFakeGettersWhenNotPresent;
    }

    public final ReflectiveDefinitionProvider withUsingFakeGettersWhenNotPresent(boolean setting) {
        this.setUsingFakeGettersWhenNotPresent(setting);
        return this;
    }
}
