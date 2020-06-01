package org.vaadin.miki.superfields.text;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventBus;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Internal class that handles common behaviour related to text selection.
 * Note: this is for internal use only.
 * @author miki
 * @since 2020-06-01
 */
class TextSelectionDelegate<C extends Component & CanSelectText & CanReceiveSelectionEventsFromClient> implements Serializable {

    /**
     * Defines the name of the HTML attribute that contains the selected text.
     */
    public static final String SELECTED_TEXT_ATTRIBUTE_NAME = "data-selected-text";

    private final C source;

    TextSelectionDelegate(C source) {
        this.source = source;
    }

    /**
     * Sends information to the client side about whether or not it should forward text selection change events.
     * @param value When {@code true}, client-side will notify server about changes in text selection.
     */
    protected void informClientAboutSendingEvents(boolean value) {
        this.source.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this.source, context ->
                this.source.getElement().callJsFunction(
                        "setCallingServer",
                        value
                )
        ));
    }

    /**
     * Fires text selection event.
     * @param eventBus Event bus.
     * @param event Event with information about text selection.
     */
    protected void fireTextSelectionEvent(ComponentEventBus eventBus, TextSelectionEvent<C> event) {
        eventBus.fireEvent(event);
    }

    private void selectionChanged(ComponentEventBus eventBus, int start, int end, String selection) {
        TextSelectionEvent<C> event = new TextSelectionEvent<>(this.source, true, start, end, selection);
        this.fireTextSelectionEvent(eventBus, event);
    }

    void selectAll(Supplier<String> valueSupplier, Supplier<ComponentEventBus> eventBusSupplier) {
        this.source.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this.source, context ->
                this.source.getElement().callJsFunction("selectAll", this.source.getElement())
        ));
        // send event if the client is not doing it
        if(!this.source.isReceivingSelectionEventsFromClient()) {
            final String value = valueSupplier.get();
            this.source.getElement().setAttribute(SELECTED_TEXT_ATTRIBUTE_NAME, value);
            this.selectionChanged(eventBusSupplier.get(), 0, value.length(), value);
        }
    }

    void selectNone(Supplier<ComponentEventBus> eventBusSupplier) {
        this.source.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this.source, context ->
                this.source.getElement().callJsFunction("selectNone", this.source.getElement())
        ));
        // send event if the client is not doing it
        if(!this.source.isReceivingSelectionEventsFromClient()) {
            this.source.getElement().setAttribute(SELECTED_TEXT_ATTRIBUTE_NAME, "");
            this.selectionChanged(eventBusSupplier.get(), -1, -1, "");
        }
    }

    void select(Supplier<String> valueSupplier, Supplier<ComponentEventBus> eventBusSupplier, int from, int to) {
        if(from <= to)
            this.source.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this.source, context ->
                    this.source.getElement().callJsFunction("select", this.source.getElement(), from, to)
            ));
        // send event if the client is not doing it
        if(!this.source.isReceivingSelectionEventsFromClient()) {
            final String value = valueSupplier.get().substring(from, to);
            this.source.getElement().setAttribute(SELECTED_TEXT_ATTRIBUTE_NAME, value);
            this.selectionChanged(eventBusSupplier.get(), from, to, value);
        }
    }

    void updateAttributeOnValueChange(Supplier<ComponentEventBus> eventBusSupplier) {
        // special case here: if there was selection, no client-side events are caught and value is set, event must be fired
        if(!this.source.isReceivingSelectionEventsFromClient()) {
            final String lastSelected = Optional.ofNullable(this.source.getElement().getAttribute(SELECTED_TEXT_ATTRIBUTE_NAME)).orElse("");
            this.source.getElement().setAttribute(SELECTED_TEXT_ATTRIBUTE_NAME, "");
            if(!Objects.equals(lastSelected, ""))
                this.fireTextSelectionEvent(eventBusSupplier.get(), new TextSelectionEvent<>(this.source, false, -1, -1, ""));
        }
    }

}
