package org.vaadin.miki.events.state;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import org.vaadin.miki.markers.HasState;

import java.io.Serializable;

/**
 * Event associated with the change of component's state.
 * Somewhat similar to value change.
 * @param <C> Source component.
 * @param <S> Information about the state.
 */
public class StateChangeEvent<S extends Serializable, C extends Component & HasState<S>> extends ComponentEvent<C> {

    private final S state;

    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *  @param source     the source component
     * @param fromClient <code>true</code> if the event originated from the client
     * @param state Current state of the component.
     */
    public StateChangeEvent(C source, boolean fromClient, S state) {
        super(source, fromClient);
        this.state = state;
    }

    /**
     * Returns current state of the source component. Modifying the returned object may affect the source component, but it is not required nor enforced.
     * @return Component state. May never be {@code null}.
     */
    public S getState() {
        return state;
    }
}
