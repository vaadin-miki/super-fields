package org.vaadin.miki.superfields.unload;

import com.vaadin.flow.component.ComponentEvent;

/**
 * Server-side event class associated with {@code beforeunload} event happening in the client-side.
 * @author miki
 * @since 2020-04-29
 */
public class UnloadEvent extends ComponentEvent<UnloadObserver> {

    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *
     * @param source     the source component
     */
    public UnloadEvent(UnloadObserver source) {
        super(source, true);
    }
}
