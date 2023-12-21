package org.vaadin.miki.markers;

import com.vaadin.flow.component.HasValue;

/**
 * Mixin interface for {@link HasNullValueOptionallyAllowed}.
 *
 * @author miki
 * @since 2020-06-09
 */
@SuppressWarnings("squid:S119") // SELF is a fine generic name that is more descriptive than S
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

    /**
     * Chains {@link #setNullValueAllowed(boolean)} called with {@code true} and returns itself.
     * @return This.
     * @see #setNullValueAllowed(boolean)
     */
    default SELF withNullValueAllowed() {
        return this.withNullValueAllowed(true);
    }

    /**
     * Chains {@link #setNullValueAllowed(boolean)} called with {@code false} and returns itself.
     * @return This.
     * @see #setNullValueAllowed(boolean)
     */
    default SELF withoutNullValueAllowed() {
        return this.withNullValueAllowed(false);
    }

}
