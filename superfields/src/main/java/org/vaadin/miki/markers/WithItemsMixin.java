package org.vaadin.miki.markers;

import com.vaadin.flow.data.binder.HasItems;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Mixin interface to allow chaining of setting items.
 * @param <T> Type of items to add.
 * @param <SELF> Self type.
 * @author miki
 * @since 2020-04-30
 */
public interface WithItemsMixin<T, SELF extends HasItems<T>> extends HasItems<T> {

    /**
     * Chains {@link #setItems(Object[])} and returns itself.
     * @param items Items to add.
     * @return This.
     * @see #setItems(Object[])
     */
    @SuppressWarnings("unchecked")
    default SELF withItems(T... items) {
        this.setItems(items);
        return (SELF)this;
    }

    /**
     * Chains {@link #setItems(Collection)} and returns itself.
     * @param items Items to add.
     * @return This.
     * @see #setItems(Collection)
     */
    @SuppressWarnings("unchecked")
    default SELF withItems(Collection<T> items) {
        this.setItems(items);
        return (SELF)this;
    }

    /**
     * Chains {@link #setItems(Stream)} and returns itself.
     * @param items Items to add.
     * @return This.
     * @see #setItems(Stream)
     */
    @SuppressWarnings("unchecked")
    default SELF withItems(Stream<T> items) {
        this.setItems(items);
        return (SELF)this;
    }

}
