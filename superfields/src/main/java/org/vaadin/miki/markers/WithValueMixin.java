package org.vaadin.miki.markers;

import com.vaadin.flow.component.HasValue;

/**
 * Mixin for chaining {@link #setValue(Object)}.
 *
 * @param <E> Event type.
 * @param <V> Value type.
 * @param <SELF> Self type.
 *
 * @author miki
 * @since 2020-05-01
 */
@SuppressWarnings("squid:S119") // SELF is a fine generic name that is more descriptive than S
public interface WithValueMixin<E extends HasValue.ValueChangeEvent<V>, V, SELF extends HasValue<E, V>> extends HasValue<E, V> {

    /**
     * Chains {@link #setValue(Object)} and returns itself.
     * @param value Value to set.
     * @return This.
     * @see #setValue(Object)
     */
    @SuppressWarnings("unchecked")
    default SELF withValue(V value) {
        this.setValue(value);
        return (SELF)this;
    }

}
