package org.vaadin.miki.markers;

import com.vaadin.flow.component.HasValue;

/**
 * Mixin interface for {@link HasNullValueOptionallyAllowed}.
 *
 * @author miki
 * @since 2020-06-09
 */
public interface WithNullValueOptionallyAllowedMixin<SELF extends HasNullValueOptionallyAllowed<E, V>, E extends HasValue.ValueChangeEvent<V>, V> extends HasNullValueOptionallyAllowed<E, V> {

    /**
     * Chains {@link #setNullValueAllowed(boolean)} and returns itself.
     * @param allowingNullValue Whether to allow {@code null} as a value.
     * @return This.
     * @see #setNullValueAllowed(boolean)
     */
    @SuppressWarnings("unchecked")
    default SELF withNullValueAllowed(boolean allowingNullValue) {
        this.setNullValueAllowed(allowingNullValue);
        return (SELF)this;
    }

}
