package org.vaadin.miki.superfields.text;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import org.vaadin.miki.markers.HasLabel;
import org.vaadin.miki.markers.HasPlaceholder;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithPlaceholderMixin;
import org.vaadin.miki.markers.WithValueMixin;

import java.util.Objects;
import java.util.Optional;

/**
 * An extension of {@link TextField} with some useful (hopefully) features.
 * @author miki
 * @since 2020-05-29
 */
@Tag("super-text-field")
@JsModule("./super-text-field.js")
public class SuperTextField extends TextField implements CanSelectText, TextSelectionNotifier<SuperTextField>,
        HasLabel, HasPlaceholder,
        WithIdMixin<SuperTextField>,  WithLabelMixin<SuperTextField>, WithPlaceholderMixin<SuperTextField>,
        WithValueMixin<AbstractField.ComponentValueChangeEvent<TextField, String>, String, SuperTextField>,
        WithReceivingSelectionEventsFromClientMixin<SuperTextField> {

    /**
     * Defines the name of the HTML attribute that contains the selected text.
     */
    public static final String SELECTED_TEXT_ATTRIBUTE_NAME = "data-selected-text";

    private boolean receivingSelectionEventsFromClient = false;

    public SuperTextField() {
        super();
    }

    public SuperTextField(String label) {
        super(label);
    }

    public SuperTextField(String label, String placeholder) {
        super(label, placeholder);
    }

    public SuperTextField(String label, String initialValue, String placeholder) {
        super(label, initialValue, placeholder);
    }

    public SuperTextField(ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener) {
        super(listener);
    }

    public SuperTextField(String label, ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener) {
        super(label, listener);
    }

    public SuperTextField(String label, String initialValue, ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener) {
        super(label, initialValue, listener);
    }

    /**
     * Sends information to the client side about whether or not it should forward text selection change events.
     * @param value When {@code true}, client-side will notify server about changes in text selection.
     */
    protected void informClientAboutSendingEvents(boolean value) {
        this.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this, context ->
                this.getElement().callJsFunction(
                        "setCallingServer",
                        value
                        )
        ));
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        this.informClientAboutSendingEvents(this.isReceivingSelectionEventsFromClient());
        super.onAttach(attachEvent);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // detaching means server should not be informed
        if(this.isReceivingSelectionEventsFromClient())
            this.informClientAboutSendingEvents(false);
        super.onDetach(detachEvent);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Registration addTextSelectionListener(TextSelectionListener<SuperTextField> listener) {
        return this.getEventBus().addListener((Class<TextSelectionEvent<SuperTextField>>)(Class<?>)TextSelectionEvent.class, listener);
    }

    @Override
    public void selectAll() {
        this.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this, context ->
                this.getElement().callJsFunction("selectAll", this.getElement())
        ));
        // send event if the client is not doing it
        if(!this.isReceivingSelectionEventsFromClient()) {
            this.getElement().setAttribute(SELECTED_TEXT_ATTRIBUTE_NAME, this.getValue());
            this.selectionChanged(0, this.getValue().length(), this.getValue());
        }
    }

    @Override
    public void selectNone() {
        this.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this, context ->
                this.getElement().callJsFunction("selectNone", this.getElement())
        ));
        // send event if the client is not doing it
        if(!this.isReceivingSelectionEventsFromClient()) {
            this.getElement().setAttribute(SELECTED_TEXT_ATTRIBUTE_NAME, "");
            this.selectionChanged(-1, -1, "");
        }
    }

    @Override
    public void select(int from, int to) {
        if(from <= to)
            this.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this, context ->
                this.getElement().callJsFunction("select", this.getElement(), from, to)
            ));
        // send event if the client is not doing it
        if(!this.isReceivingSelectionEventsFromClient()) {
            this.getElement().setAttribute(SELECTED_TEXT_ATTRIBUTE_NAME, this.getValue().substring(from, to));
            this.selectionChanged(from, to, this.getSelectedText());
        }
    }

    @ClientCallable
    private void selectionChanged(int start, int end, String selection) {
        TextSelectionEvent<SuperTextField> event = new TextSelectionEvent<>(this, true, start, end, selection);
        this.fireTextSelectionEvent(event);
    }

    /**
     * Fires text selection event.
     * @param event Event with information about text selection.
     */
    protected void fireTextSelectionEvent(TextSelectionEvent<SuperTextField> event) {
        this.getEventBus().fireEvent(event);
    }

    @Override
    public String getSelectedText() {
        return this.getElement().getAttribute(SELECTED_TEXT_ATTRIBUTE_NAME);
    }

    @Override
    public boolean isReceivingSelectionEventsFromClient() {
        return this.receivingSelectionEventsFromClient;
    }

    @Override
    public void setReceivingSelectionEventsFromClient(boolean receivingSelectionEventsFromClient) {
        this.receivingSelectionEventsFromClient = receivingSelectionEventsFromClient;
        this.informClientAboutSendingEvents(receivingSelectionEventsFromClient);
    }

    @Override
    public void setValue(String value) {
        // special case here: if there was selection, no client-side events are caught and value is set, event must be fired
        if(!this.isReceivingSelectionEventsFromClient()) {
            final String lastSelected = Optional.ofNullable(this.getElement().getAttribute(SELECTED_TEXT_ATTRIBUTE_NAME)).orElse("");
            this.getElement().setAttribute(SELECTED_TEXT_ATTRIBUTE_NAME, "");
            if(!Objects.equals(lastSelected, ""))
                this.fireTextSelectionEvent(new TextSelectionEvent<>(this, false, -1, -1, ""));
        }
        super.setValue(value);
    }
}
