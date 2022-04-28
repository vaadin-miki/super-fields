package org.vaadin.miki.superfields.collections;

import com.vaadin.flow.function.SerializablePredicate;

/**
 * @author miki
 * @since 2022-04-28
 */
public interface HasCollectionElementFilter<T> {

    /**
     * Sets a value filter. Only items matching the filter will be included in the generated value.
     * @param collectionElementFilter Value filter to use.
     */
    void setCollectionElementFilter(SerializablePredicate<T> collectionElementFilter);

    /**
     * Returns current value filter. All items in the value will pass this filter.
     * @return A non-{@code null} filter.
     */
    SerializablePredicate<T> getCollectionElementFilter();


}
