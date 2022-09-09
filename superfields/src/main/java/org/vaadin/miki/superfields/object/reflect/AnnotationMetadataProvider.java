package org.vaadin.miki.superfields.object.reflect;

import com.vaadin.flow.function.SerializableFunction;
import org.vaadin.miki.superfields.object.PropertyMetadata;

import java.lang.annotation.Annotation;
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
 * Provides {@link PropertyMetadata} based on previously registered annotations. Each field together with its setter and getter are scanned.
 *
 * @author miki
 * @since 2022-09-01
 */
public class AnnotationMetadataProvider implements MetadataProvider {

    private final Map<Class<? extends Annotation>, Function<Annotation, PropertyMetadata>> registeredAnnotations = new HashMap<>();

    @Override
    public Collection<PropertyMetadata> getMetadata(String name, Field field, Method setter, Method getter) {
        return Stream.of(field, setter, getter)
                .filter(Objects::nonNull)
                .map(obj -> this.registeredAnnotations.keySet().stream().map(type -> {
                    final Annotation annotation = obj.getAnnotation(type);
                    if(annotation == null) return null;
                    else return this.registeredAnnotations.get(type).apply(annotation);
                }).toArray(PropertyMetadata[]::new))
                .flatMap(Stream::of)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * Registers the given annotation to be discovered during scanning and maps it to a {@link PropertyMetadata}.
     *
     * @param name Name of the metadata.
     * @param type Value type of the metadata.
     * @param annotation Annotation to map.
     * @param valueFunction Function to obtain the value from the given annotation.
     * @param <A> Annotation type.
     * @param <V> Metadata value type.
     */
    public <V, A extends Annotation> void registerAnnotation(String name, Class<V> type, Class<A> annotation, SerializableFunction<A, V> valueFunction) {
        this.registeredAnnotations.put(annotation, a -> new PropertyMetadata(name, type, valueFunction.apply(annotation.cast(a))));
    }

    /**
     * Chains {@link #registerAnnotation(String, Class, Class, SerializableFunction)} and returns itself.
     *
     * @param name Name of the metadata.
     * @param type Value type of the metadata.
     * @param annotation Annotation to map.
     * @param valueFunction Function to obtain the value from the given annotation.
     * @return This.
     * @param <A> Annotation type.
     * @param <V> Metadata value type.
     * @see #registerAnnotation(String, Class, Class, SerializableFunction)
     */
    public final <V, A extends Annotation> AnnotationMetadataProvider withRegisteredAnnotation(String name, Class<A> annotation, Class<V> type, SerializableFunction<A, V> valueFunction) {
        this.registerAnnotation(name, type, annotation, valueFunction);
        return this;
    }

    /**
     * Registers the given annotation to be discovered during scanning and maps it to a {@code boolean} {@link PropertyMetadata}.
     * Only the presence of the annotation will result in creation of the metadata and setting its value to {@code true}.
     * This means that the absence of the annotation <strong>will not</strong> create the metadata.
     *
     * @param name Name of the metadata.
     * @param annotation Annotation to map.
     */
    public void registerAnnotation(String name, Class<? extends Annotation> annotation) {
        this.registeredAnnotations.put(annotation, a -> new PropertyMetadata(name, boolean.class, true));
    }

    /**
     * Chains {@link #registerAnnotation(String, Class)} and returns itself.
     * @param name Name of the metadata.
     * @param annotation Annotation to map.
     * @return This.
     * @see #registerAnnotation(String, Class)
     */
    public final AnnotationMetadataProvider withRegisteredAnnotation(String name, Class<? extends Annotation> annotation) {
        this.registerAnnotation(name, annotation);
        return this;
    }

}
