package org.vaadin.miki.events.state;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.shared.Registration;
import org.vaadin.miki.markers.HasState;

import java.io.Serializable;

/**
 * Marker interface for objects that broadcast {@link StateChangeEvent}s.
 * @param <S> State type.
 * @param <C> Component type.
 * @author miki
 * @since 2020-07-08
 */
@FunctionalInterface
public interface StateChangeNotifier<S extends Serializable, C extends Component & HasState<S>> {

    /**
     * Adds given {@link StateChangeListener}.
     * @param listener Listener to be notified about {@link StateChangeEvent}s.
     * @return A {@link Registration} that can be used to stop listening to the event.
     */
    Registration addStateChangeListener(StateChangeListener<S, C> listener);

}
