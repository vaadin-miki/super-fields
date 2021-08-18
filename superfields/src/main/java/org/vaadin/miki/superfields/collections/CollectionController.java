package org.vaadin.miki.superfields.collections;

/**
 * Marker interface for all components that can control a collection.
 * Note: this is not generic in any way to allow reusing one controller among many components.
 * @author miki
 * @since 2021-08-14
 */
public interface CollectionController {

    /**
     * Returns the current size of the collection.
     * @return A non-negative number.
     */
    int size();

    /**
     * Checks if the collection is empty.
     * @return {@code true} if {@link #size()} returns {@code 0}, {@code false} otherwise.
     */
    default boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * Clears the collection and removes all elements from it.
     */
    void removeAll();

    /**
     * Adds a new component at a specified position, moving subsequent elements by one.
     * @param atIndex Index to add at.
     */
    void add(int atIndex);

    /**
     * Adds a new component at the end of current list.
     * By default, it has the same effect as calling {@link #add(int)} with {@link #size()} as parameter.
     */
    default void add() {
        this.add(this.size());
    }

    /**
     * Removes a component at given index. All subsequent components are moved forward by one.
     * @param atIndex Index to remove component at.
     */
    void remove(int atIndex);

    /**
     * Removes the last component.
     * By default, it has the same effect as calling {@link #remove(int)} with {@link #size()}{@code - 1} as parameter.
     */
    default void remove() {
        this.remove(this.size() - 1);
    }
}
