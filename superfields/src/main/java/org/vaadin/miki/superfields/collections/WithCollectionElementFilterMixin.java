package org.vaadin.miki.superfields.collections;

import com.vaadin.flow.function.SerializablePredicate;

/**
 * Mixin interface to allow chaining {@link #setCollectionElementFilter(SerializablePredicate)}.
 *
 * @author miki
 * @since 2022-04-28
 */
@SuppressWarnings("squid:S119")
public interface WithCollectionElementFilterMixin<T, SELF extends HasCollectionElementFilter<T>> extends HasCollectionElementFilter<T> {

    /**
     * Chains {@link #setCollectionElementFilter(SerializablePredicate)} and returns itself.
     * @param valueFilter Filter to use.
     * @return This.
     */
    @SuppressWarnings("unchecked")
    default SELF withCollectionElementFilter(SerializablePredicate<T> valueFilter) {
        this.setCollectionElementFilter(valueFilter);
        return (SELF) this;
    }
}
