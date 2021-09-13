package org.vaadin.miki.superfields.collections;

/**
 * Mixin for chaining {@link #setCollectionValueComponentProvider(CollectionValueComponentProvider)}.
 * @param <T> Type of data in the collection.
 * @param <SELF> Self type.
 * @author miki
 * @since 2021-08-25
 */
public interface WithCollectionValueComponentProviderMixin<T, SELF extends HasCollectionValueComponentProvider<T>> extends HasCollectionValueComponentProvider<T> {

    /**
     * Chains {@link #setCollectionValueComponentProvider(CollectionValueComponentProvider)} and returns itself.
     * @param provider Provider to use.
     * @return This.
     * @see #setCollectionValueComponentProvider(CollectionValueComponentProvider)
     */
    @SuppressWarnings("unchecked")
    default SELF withCollectionComponentProvider(CollectionValueComponentProvider<T, ?> provider) {
        this.setCollectionValueComponentProvider(provider);
        return (SELF) this;
    }
}
