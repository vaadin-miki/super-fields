package org.vaadin.miki.superfields.collections;

/**
 * Marker interface for objects that have a {@link CollectionComponentProvider}.
 * @param <T> Type of the element in the collection.
 * @author miki
 * @since 2021-08-25
 */
public interface HasCollectionComponentProvider<T> {

    void setCollectionComponentProvider(CollectionComponentProvider<T, ?> provider);

    CollectionComponentProvider<T, ?> getCollectionComponentProvider();

}
