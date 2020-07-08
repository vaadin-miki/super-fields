package org.vaadin.miki.events.state;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import org.vaadin.miki.markers.HasState;

/**
 * Marker interface for objects that listen to state changes.
 * @param <S> Information about the state.
 * @param <C> Source of the changes.
 */
@FunctionalInterface
public interface StateChangeListener<S, C extends Component & HasState<S>> extends ComponentEventListener<StateChangeEvent<S, C>> {
}
