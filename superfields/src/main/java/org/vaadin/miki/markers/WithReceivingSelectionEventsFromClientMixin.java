package org.vaadin.miki.markers;

/**
 * Marker interface for chaining {@link #setReceivingSelectionEventsFromClient(boolean)}.
 * @param <SELF> Self type.
 * @author miki
 * @since 2020-06-01
 */
@SuppressWarnings("squid:S119") // SELF is a fine generic name that is more descriptive than S
public interface WithReceivingSelectionEventsFromClientMixin<SELF extends CanReceiveSelectionEventsFromClient> extends CanReceiveSelectionEventsFromClient {

    /**
     * Chains {@link #setReceivingSelectionEventsFromClient(boolean)} and returns itself.
     * Note: this feature is by default turned off.
     * @param receivingSelectionEventsFromClient Whether the client should send events about text selection changes.
     * @return This.
     * @see #setReceivingSelectionEventsFromClient(boolean)
     */
    @SuppressWarnings("unchecked")
    default SELF withReceivingSelectionEventsFromClient(boolean receivingSelectionEventsFromClient) {
        this.setReceivingSelectionEventsFromClient(receivingSelectionEventsFromClient);
        return (SELF)this;
    }

    /**
     * Chains {@link #setReceivingSelectionEventsFromClient(boolean)} called with {@code true} and returns itself.
     * @return This.
     */
    default SELF withReceivingSelectionEventsFromClient() {
        return this.withReceivingSelectionEventsFromClient(true);
    }

    /**
     * Chains {@link #setReceivingSelectionEventsFromClient(boolean)} called with {@code false} and returns itself.
     * @return This.
     */
    default SELF withoutReceivingSelectionEventsFromClient() {
        return this.withReceivingSelectionEventsFromClient(false);
    }

}
