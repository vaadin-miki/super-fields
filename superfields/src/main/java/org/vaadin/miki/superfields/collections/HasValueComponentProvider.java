package org.vaadin.miki.superfields.collections;

/**
 * Marker interface for objects that have a {@link ValueComponentProvider}.
 * @param <T> Type of the element in the collection.
 * @author miki
 * @since 2021-08-25
 */
public interface HasValueComponentProvider<T> {

    void setValueComponentProvider(ValueComponentProvider<T, ?> provider);

    ValueComponentProvider<T, ?> getValueComponentProvider();

}
