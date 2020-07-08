package org.vaadin.miki.events.click;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.shared.Registration;
import org.vaadin.miki.markers.Clickable;

/**
 * Marker interface for objects that broadcast {@link ComponentClickEvent}s.
 * @author miki
 * @since 2020-07-08
 */
public interface ComponentClickNotifier<C extends Component & Clickable> {

    /**
     * Adds a listener.
     * @param listener Listener to be notified when event is fired.
     * @return A {@link Registration} that can be used to stop listening to the event.
     */
    Registration addClickListener(ComponentClickListener<C> listener);

}
