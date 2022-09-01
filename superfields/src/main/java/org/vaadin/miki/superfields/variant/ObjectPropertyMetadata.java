package org.vaadin.miki.superfields.variant;

import java.util.Objects;

/**
 * Metadata for an {@link ObjectPropertyDefinition}.
 * @author miki
 * @since 2022-09-01
 */
public class ObjectPropertyMetadata {

    private final String name;
    private final Class<?> valueType;
    private final Object value;

    public <V> ObjectPropertyMetadata(String name, Class<V> valueType, V value) {
        this.name = name;
        this.valueType = valueType;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Class<?> getValueType() {
        return valueType;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectPropertyMetadata that = (ObjectPropertyMetadata) o;
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
