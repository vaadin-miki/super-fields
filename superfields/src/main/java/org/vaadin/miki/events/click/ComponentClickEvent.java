package org.vaadin.miki.events.click;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import org.vaadin.miki.markers.Clickable;

/**
 * An event for button clicks.
 *
 * There already is {@link ClickEvent}, but that one is fired automatically by the framework. This event must be explicitly fired by the source component.
 *
 * @param <C> Event source.
 * @author miki
 * @since 2020-07-08
 */
public class ComponentClickEvent<C extends Component & Clickable> extends ComponentEvent<C> {

    private final ClickEvent<C> details;

    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *  @param source     the source component
     * @param originalEvent Original click event with even more details. Can be {@code null}.
     */
    public ComponentClickEvent(C source, ClickEvent<C> originalEvent) {
        super(source, originalEvent.isFromClient());
        this.details = originalEvent;
    }

    /**
     * Returns the underlying {@link ClickEvent}.
     * @return A {@link ClickEvent}. May be {@code null} if this event is not associated with any underlying event.
     */
    public ClickEvent<C> getDetails() {
        return details;
    }
}
