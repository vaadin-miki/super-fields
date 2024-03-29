package org.vaadin.miki.markers;

import com.vaadin.flow.component.HasValue;

/**
 * Marker interface for descendants of {@link HasValue} that may optionally allow {@code null} as value.
 * By default, objects should not allow {@code null}s.
 * In general, when this feature is turned on and {@link #setValue(Object)} is called with {@code null}, the
 * object should never throw a {@link NullPointerException}.
 *
 * @param <E> Event type.
 * @param <V> Value type.
 *
 * @author miki
 * @since 2021-09-13
 */
public interface HasNullValueOptionallyAllowed<E extends HasValue.ValueChangeEvent<V>, V> extends HasValue<E, V> {

    /**
     * Checks whether {@code null} is allowed as a value of the component.
     * @return Whether {@code null} is allowed as a value. Should default to {@code false}.
     */
    boolean isNullValueAllowed();

    /**
     * Sets allowance of {@code null} as this component's value.
     * @param allowingNullValue Whether to allow {@code null} as a value.
     */
    void setNullValueAllowed(boolean allowingNullValue);

}
