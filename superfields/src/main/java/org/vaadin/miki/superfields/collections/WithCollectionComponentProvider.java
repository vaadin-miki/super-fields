package org.vaadin.miki.superfields.collections;

/**
 * Mixin for chaining {@link #setCollectionComponentProvider(CollectionComponentProvider)}.
 * @param <T> Type of data in the collection.
 * @param <SELF> Self type.
 * @author miki
 * @since 2021-08-25
 */
public interface WithCollectionComponentProvider<T, SELF extends HasCollectionComponentProvider<T>> extends HasCollectionComponentProvider<T> {

    /**
     * Chains {@link #setCollectionComponentProvider(CollectionComponentProvider)} and returns itself.
     * @param provider Provider to use.
     * @return This.
     */
    @SuppressWarnings("unchecked")
    default SELF withCollectionComponentProvider(CollectionComponentProvider<T, ?> provider) {
        this.setCollectionComponentProvider(provider);
        return (SELF) this;
    }
}
