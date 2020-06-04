package org.vaadin.miki.superfields.text;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventBus;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A class that handles common behaviour related to text selection.
 * This assumes that the client-side component mixes in {@code text-selection-mikin.js}.
 * @author miki
 * @since 2020-06-01
 */
public class TextSelectionDelegate<C extends Component & CanSelectText & CanReceiveSelectionEventsFromClient> implements Serializable {

    /**
     * Defines the name of the HTML attribute that contains the selected text.
     */
    public static final String SELECTED_TEXT_ATTRIBUTE_NAME = "data-selected-text";

    private final C source;

    /**
     * Creates the delegate for a given component.
     * @param source Source of all events, data, etc.
     */
    public TextSelectionDelegate(C source) {
        this.source = source;
    }

    /**
     * Sends information to the client side about whether or not it should forward text selection change events.
     * @param value When {@code true}, client-side will notify server about changes in text selection.
     */
    public void informClientAboutSendingEvents(boolean value) {
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
    public void fireTextSelectionEvent(ComponentEventBus eventBus, TextSelectionEvent<C> event) {
        eventBus.fireEvent(event);
    }

    private void selectionChanged(ComponentEventBus eventBus, int start, int end, String selection) {
        TextSelectionEvent<C> event = new TextSelectionEvent<>(this.source, true, start, end, selection);
        this.fireTextSelectionEvent(eventBus, event);
    }

    /**
     * Selects all text.
     * @param valueSupplier Way of getting current value. Needed if no client notifications.
     * @param eventBusSupplier Way of getting event bus. Needed if no client notifications.
     */
    public void selectAll(Supplier<String> valueSupplier, Supplier<ComponentEventBus> eventBusSupplier) {
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

    /**
     * Selects no text.
     * @param eventBusSupplier Way of getting event bus. Needed if no client notifications.
     */
    public void selectNone(Supplier<ComponentEventBus> eventBusSupplier) {
        this.source.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this.source, context ->
                this.source.getElement().callJsFunction("selectNone", this.source.getElement())
        ));
        // send event if the client is not doing it
        if(!this.source.isReceivingSelectionEventsFromClient()) {
            this.source.getElement().setAttribute(SELECTED_TEXT_ATTRIBUTE_NAME, "");
            this.selectionChanged(eventBusSupplier.get(), -1, -1, "");
        }
    }

    /**
     * Selects some text.
     * @param valueSupplier Way of getting current value. Needed if no client notifications.
     * @param eventBusSupplier Way of getting event bus. Needed if no client notifications.
     * @param from Selection starting index, inclusive.
     * @param to Selection end index, exclusive.
     */
    public void select(Supplier<String> valueSupplier, Supplier<ComponentEventBus> eventBusSupplier, int from, int to) {
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

    /**
     * Handles selection change on value change if there are no client notifications.
     * Does nothing if the component is receiving client-side notifications.
     * @param eventBusSupplier Way of getting event bus.
     */
    public void updateAttributeOnValueChange(Supplier<ComponentEventBus> eventBusSupplier) {
        // special case here: if there was selection, no client-side events are caught and value is set, event must be fired
        if(!this.source.isReceivingSelectionEventsFromClient()) {
            final String lastSelected = Optional.ofNullable(this.source.getElement().getAttribute(SELECTED_TEXT_ATTRIBUTE_NAME)).orElse("");
            this.source.getElement().setAttribute(SELECTED_TEXT_ATTRIBUTE_NAME, "");
            if(!Objects.equals(lastSelected, ""))
                this.fireTextSelectionEvent(eventBusSupplier.get(), new TextSelectionEvent<>(this.source, false, -1, -1, ""));
        }
    }

}
