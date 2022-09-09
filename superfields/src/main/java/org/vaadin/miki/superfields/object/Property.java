package org.vaadin.miki.superfields.object;

import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableFunction;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
public class Property<T, P> {

    private final String name;
    private final Class<T> owner;
    private final Class<P> type;
    private final SerializableBiConsumer<T, P> setter;
    private final SerializableFunction<T, P> getter;
    private final Map<String, PropertyMetadata> metadata;

    /**
     * Constructs a {@link Property} of a given that belongs to the given owner, has a given value type, a corresponding setter and getter and optional {@link PropertyMetadata}.
     *
     * @param owner Object the property belongs to.
     * @param name Name of the property.
     * @param type Type of the property.
     * @param setter Setter. May be {@code null}.
     * @param getter Getter. May be {@code null}.
     * @param metadata Any {@link PropertyMetadata} this {@link Property} should have.
     */
    public Property(Class<T> owner, String name, Class<P> type, SerializableBiConsumer<T, P> setter, SerializableFunction<T, P> getter, PropertyMetadata... metadata) {
        this(owner, name, type, setter, getter, metadata.length == 0 ? Collections.emptySet() : Arrays.asList(metadata));
    }

    /**
     * Constructs a {@link Property} of a given that belongs to the given owner, has a given value type, a corresponding setter and getter and optional {@link PropertyMetadata}.
     *
     * @param owner Object the property belongs to.
     * @param name Name of the property.
     * @param type Type of the property.
     * @param setter Setter. May be {@code null}.
     * @param getter Getter. May be {@code null}.
     * @param metadata Any {@link PropertyMetadata} this {@link Property} should have.
     */
    public Property(Class<T> owner, String name, Class<P> type, SerializableBiConsumer<T, P> setter, SerializableFunction<T, P> getter, Collection<PropertyMetadata> metadata) {
        this.owner = owner;
        this.name = name;
        this.type = type;
        this.setter = setter;
        this.getter = getter;
        this.metadata = metadata.stream().collect(Collectors.toMap(PropertyMetadata::getName, Function.identity()));
    }

    /**
     * Returns the name of this property.
     * @return Name of the property.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type the property belongs to.
     * @return Type.
     */
    public Class<T> getOwner() {
        return owner;
    }

    /**
     * Returns the setter for the property, if any.
     * @return The setter, if any.
     */
    public Optional<SerializableBiConsumer<T, P>> getSetter() {
        return Optional.ofNullable(setter);
    }

    /**
     * Returns the getter for the property, if any.
     * @return The getter, if any.
     */
    public Optional<SerializableFunction<T, P>> getGetter() {
        return Optional.ofNullable(getter);
    }

    /**
     * Returns the value type of the property.
     * @return Value type.
     */
    public Class<P> getType() {
        return type;
    }

    /**
     * Returns the map of {@link PropertyMetadata}, organised by metadata name for easier use.
     * @return A non-{@code null}, but possibly empty, map of {@link PropertyMetadata} by name. Any changes to the returned map will affect this object.
     */
    public Map<String, PropertyMetadata> getMetadata() {
        return metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property<?, ?> that = (Property<?, ?>) o;
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
