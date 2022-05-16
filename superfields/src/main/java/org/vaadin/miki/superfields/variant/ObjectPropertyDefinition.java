package org.vaadin.miki.superfields.variant;

import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableFunction;

import java.util.Objects;

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
    private final SerializableBiConsumer<T, P> setter;
    private final SerializableFunction<T, P> getter;

    public ObjectPropertyDefinition(String name, SerializableBiConsumer<T, P> setter, SerializableFunction<T, P> getter) {
        this.name = name;
        this.setter = setter;
        this.getter = getter;
    }

    public String getName() {
        return name;
    }

    public SerializableBiConsumer<T, P> getSetter() {
        return setter;
    }

    public SerializableFunction<T, P> getGetter() {
        return getter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectPropertyDefinition<?, ?> that = (ObjectPropertyDefinition<?, ?>) o;
        return Objects.equals(name, that.name) && Objects.equals(setter, that.setter) && Objects.equals(getter, that.getter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, setter, getter);
    }

    @Override
    public String toString() {
        return "ObjectPropertyDefinition{" +
                "name='" + name + '\'' +
                ", setter=" + setter +
                ", getter=" + getter +
                '}';
    }
}
