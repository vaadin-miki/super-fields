package org.vaadin.miki.superfields.variant;

import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableFunction;

import java.util.Objects;
import java.util.Optional;

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

    public ObjectPropertyDefinition(Class<T> owner, String name, Class<P> type, SerializableBiConsumer<T, P> setter, SerializableFunction<T, P> getter) {
        this.owner = owner;
        this.name = name;
        this.type = type;
        this.setter = setter;
        this.getter = getter;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectPropertyDefinition<?, ?> that = (ObjectPropertyDefinition<?, ?>) o;
        return Objects.equals(name, that.name) && Objects.equals(owner, that.owner) && Objects.equals(type, that.type) && Objects.equals(setter, that.setter) && Objects.equals(getter, that.getter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, owner, type, setter, getter);
    }

    @Override
    public String toString() {
        return "ObjectPropertyDefinition{" +
                "name='" + name + '\'' +
                ", owner=" + owner +
                ", type=" + type +
                ", setter=" + setter +
                ", getter=" + getter +
                '}';
    }
}
