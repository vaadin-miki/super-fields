package org.vaadin.miki.superfields.collections;

/**
 * Marker interface for objects that have a {@link CollectionValueComponentProvider}.
 * @param <T> Type of the element in the collection.
 * @author miki
 * @since 2021-08-25
 */
public interface HasCollectionValueComponentProvider<T> {

    /**
     * Sets the provider used to generate components for each element of the collection.
     * @param provider A non-{@code null} provider.
     */
    void setCollectionValueComponentProvider(CollectionValueComponentProvider<T, ?> provider);

    /**
     * Returns the current provider used to generate components.
     * @return A non-{@code null} provider.
     */
    CollectionValueComponentProvider<T, ?> getCollectionValueComponentProvider();

}
