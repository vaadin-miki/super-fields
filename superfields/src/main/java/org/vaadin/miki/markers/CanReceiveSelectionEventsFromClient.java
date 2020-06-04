package org.vaadin.miki.markers;

import org.vaadin.miki.events.text.TextSelectionEvent;

/**
 * Marker interface for objects capable of receiving text selection events from client-side code.
 * @author miki
 * @since 2020-06-04
 */
public interface CanReceiveSelectionEventsFromClient {
    /**
     * Check if client will inform server on selection change.
     * Note: this feature is by default turned off.
     * @return When {@code true}, each selection change in the client-side component will result in this component broadcasting a {@link TextSelectionEvent}.
     */
    boolean isReceivingSelectionEventsFromClient();

    /**
     * Configures sending events by the client-side component.
     * Note: this feature is by default turned off.
     * @param receivingSelectionEventsFromClient When {@code false}, selecting text in client-side component will not send an event to server-side component. When {@code true}, it will.
     */
    void setReceivingSelectionEventsFromClient(boolean receivingSelectionEventsFromClient);
}
