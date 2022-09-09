package org.vaadin.miki.superfields.object;

import java.util.Objects;

/**
 * Metadata for a {@link Property}.
 *
 * @author miki
 * @since 2022-09-01
 */
public class PropertyMetadata {

    private final String name;
    private final Class<?> valueType;
    private final Object value;

    /**
     * Constructs a {@link PropertyMetadata}.
     * @param name Name of the metadata.
     * @param valueType Type of value of the metadata.
     * @param value Value of the metadata.
     * @param <V> Type of value.
     */
    public <V> PropertyMetadata(String name, Class<V> valueType, V value) {
        this.name = name;
        this.valueType = valueType;
        this.value = value;
    }

    /**
     * Returns the name of the metadata.
     * @return Name of the metadata.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of value of the metadata.
     * @return Type of value.
     */
    public Class<?> getValueType() {
        return valueType;
    }

    /**
     * Returns the value of the metadata. It is of the type returned by {@link #getValueType()}.
     * @return Value of the metadata. Can be {@code null}.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Checks if the value of the metadata is an instance of the given type.
     * @param type Type to check.
     * @return Same as {@code type.isInstance(this.getValue)}
     */
    public boolean hasValueOfType(Class<?> type) {
        return type.isInstance(this.getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyMetadata that = (PropertyMetadata) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getValueType(), that.getValueType()) && Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getValueType(), getValue());
    }

    @Override
    public String toString() {
        return "ObjectPropertyMetadata{" +
                "name='" + name + '\'' +
                ", valueType=" + valueType +
                ", value=" + value +
                '}';
    }
}
