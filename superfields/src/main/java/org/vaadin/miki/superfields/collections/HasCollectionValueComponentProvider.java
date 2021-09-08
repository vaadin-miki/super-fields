package org.vaadin.miki.superfields.collections;

/**
 * Marker interface for objects that have a {@link CollectionValueComponentProvider}.
 * @param <T> Type of the element in the collection.
 * @author miki
 * @since 2021-08-25
 */
public interface HasCollectionValueComponentProvider<T> {

    void setCollectionValueComponentProvider(CollectionValueComponentProvider<T, ?> provider);

    CollectionValueComponentProvider<T, ?> getCollectionValueComponentProvider();

}
