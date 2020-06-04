package org.vaadin.miki.markers;

/**
 * Marker interface for chaining {@link #setReceivingSelectionEventsFromClient(boolean)}.
 * @param <SELF> Self type.
 * @author miki
 * @since 2020-06-01
 */
public interface WithReceivingSelectionEventsFromClientMixin<SELF extends CanReceiveSelectionEventsFromClient> extends CanReceiveSelectionEventsFromClient {

    /**
     * Chains {@link #setReceivingSelectionEventsFromClient(boolean)} and returns itself.
     * Note: this feature is by default turned off.
     * @param receivingSelectionEventsFromClient Whether or not the client should send events about text selection changes.
     * @return This.
     * @see #setReceivingSelectionEventsFromClient(boolean)
     */
    @SuppressWarnings("unchecked")
    default SELF withReceivingSelectionEventsFromClient(boolean receivingSelectionEventsFromClient) {
        this.setReceivingSelectionEventsFromClient(receivingSelectionEventsFromClient);
        return (SELF)this;
    }


}
