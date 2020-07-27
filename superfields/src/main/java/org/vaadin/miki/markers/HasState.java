package org.vaadin.miki.markers;

/**
 * Marker interface for objects that have a state.
 * State is read-only by default and other actions influence the state the component is in.
 *
 * @param <S> Object that encapsulates current state.
 *
 * @author miki
 * @since 2020-07-08
 */
@FunctionalInterface
public interface HasState<S> {

    /**
     * Returns the current state of this object.
     * The changes to the resulting object should not affect this object.
     * @return Current state. May never be {@code null}.
     */
    S getState();

}
