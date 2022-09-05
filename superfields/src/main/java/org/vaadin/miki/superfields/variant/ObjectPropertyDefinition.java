package org.vaadin.miki.superfields.variant;

import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableFunction;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Defines a property of an object.
 *
 * @param <T> Object type.
 * @param <P> Property type.
 *
 * @author miki
 * @since 2022-05-16
 */
public class ObjectPropertyDefinition<T, P> {

    private final String name;
    private final Class<T> owner;
    private final Class<P> type;
    private final SerializableBiConsumer<T, P> setter;
    private final SerializableFunction<T, P> getter;
    private final Map<String, ObjectPropertyMetadata> metadata;

    public ObjectPropertyDefinition(Class<T> owner, String name, Class<P> type, SerializableBiConsumer<T, P> setter, SerializableFunction<T, P> getter, ObjectPropertyMetadata... metadata) {
        this(owner, name, type, setter, getter, metadata.length == 0 ? Collections.emptySet() : Set.of(metadata));
    }

    public ObjectPropertyDefinition(Class<T> owner, String name, Class<P> type, SerializableBiConsumer<T, P> setter, SerializableFunction<T, P> getter, Collection<ObjectPropertyMetadata> metadata) {
        this.owner = owner;
        this.name = name;
        this.type = type;
        this.setter = setter;
        this.getter = getter;
        this.metadata = metadata.stream().collect(Collectors.toMap(ObjectPropertyMetadata::getName, Function.identity()));
    }

    public String getName() {
        return name;
    }

    public Class<T> getOwner() {
        return owner;
    }

    public Optional<SerializableBiConsumer<T, P>> getSetter() {
        return Optional.ofNullable(setter);
    }

    public Optional<SerializableFunction<T, P>> getGetter() {
        return Optional.ofNullable(getter);
    }

    public Class<P> getType() {
        return type;
    }

    public Map<String, ObjectPropertyMetadata> getMetadata() {
        return metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectPropertyDefinition<?, ?> that = (ObjectPropertyDefinition<?, ?>) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getOwner(), that.getOwner()) && Objects.equals(getType(), that.getType()) && Objects.equals(getSetter(), that.getSetter()) && Objects.equals(getGetter(), that.getGetter()) && Objects.equals(getMetadata(), that.getMetadata());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getOwner(), getType(), getSetter(), getGetter(), getMetadata());
    }

    @Override
    public String toString() {
        return "ObjectPropertyDefinition{" +
                "name='" + name + '\'' +
                ", owner=" + owner +
                ", type=" + type +
                ", setter=" + setter +
                ", getter=" + getter +
                ", metadata=" + metadata +
                '}';
    }
}
