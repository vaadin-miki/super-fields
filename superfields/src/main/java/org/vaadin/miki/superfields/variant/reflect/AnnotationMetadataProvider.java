package org.vaadin.miki.superfields.variant.reflect;

import com.vaadin.flow.function.SerializableFunction;
import org.vaadin.miki.superfields.variant.ObjectPropertyMetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides {@link ObjectPropertyMetadata} based on annotations present in given class.
 * @author miki
 * @since 2022-09-01
 */
public class AnnotationMetadataProvider implements MetadataProvider {

    private final Map<Class<? extends Annotation>, Function<Annotation, ObjectPropertyMetadata>> registeredAnnotations = new HashMap<>();

    @Override
    public Collection<ObjectPropertyMetadata> getMetadata(String name, Field field, Method setter, Method getter) {
        return Stream.of(field, setter, getter)
                .filter(Objects::nonNull)
                .map(AccessibleObject::getDeclaredAnnotations)
                .flatMap(Stream::of)
                .filter(a -> this.registeredAnnotations.containsKey(a.getClass()))
                .map(a -> this.registeredAnnotations.get(a.getClass()).apply(a))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public <A extends Annotation, V> void registerAnnotation(String name, Class<A> annotation, Class<V> type, SerializableFunction<A, V> valueFunction) {
        this.registeredAnnotations.put(annotation, a -> new ObjectPropertyMetadata(name, type, valueFunction.apply(annotation.cast(a))));
    }

    public void registerAnnotation(String name, Class<? extends Annotation> annotation) {
        this.registeredAnnotations.put(annotation, a -> new ObjectPropertyMetadata(name, boolean.class, true));
    }

}
