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
        WithValueMixin<AbstractField.ComponentValueChangeEvent<TextField, String>, String, SuperTextField> {

    private boolean clientCallingServerOnSelectionChange = false;

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
     * Sends information to the client side about whether or not it should text selection change events.
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
        this.informClientAboutSendingEvents(this.isClientCallingServerOnSelectionChange());
        super.onAttach(attachEvent);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // detaching means server should not be informed
        if(this.isClientCallingServerOnSelectionChange())
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
        if(!this.isClientCallingServerOnSelectionChange())
            this.selectionChanged(0, this.getValue().length(), this.getValue());
    }

    @Override
    public void selectNone() {
        this.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this, context ->
                this.getElement().callJsFunction("selectNone", this.getElement())
        ));
        // send event if the client is not doing it
        if(!this.isClientCallingServerOnSelectionChange())
            this.selectionChanged(-1, -1, "");
    }

    @Override
    public void select(int from, int to) {
        if(from <= to)
            this.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this, context ->
                this.getElement().callJsFunction("select", this.getElement(), from, to)
            ));
        // send event if the client is not doing it
        if(!this.isClientCallingServerOnSelectionChange())
            this.selectionChanged(from, to, this.getSelectedText());
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
        return this.getElement().getAttribute("data-selected-text");
    }

    /**
     * Check if client will inform server on selection change.
     * Note: this feature is by default turned off.
     * @return When {@code true}, each selection change in the client-side component will result in this component broadcasting a {@link TextSelectionEvent}.
     */
    public boolean isClientCallingServerOnSelectionChange() {
        return this.clientCallingServerOnSelectionChange;
    }

    /**
     * Configures sending events by the client-side component.
     * Note: this feature is by default turned off.
     * @param clientCallingServerOnSelectionChange When {@code false}, selecting text in client-side component will not send an event to server-side component. When {@code true}, it will.
     */
    public void setClientCallingServerOnSelectionChange(boolean clientCallingServerOnSelectionChange) {
        this.clientCallingServerOnSelectionChange = clientCallingServerOnSelectionChange;
        this.informClientAboutSendingEvents(clientCallingServerOnSelectionChange);
    }

    /**
     * Chains {@link #setClientCallingServerOnSelectionChange(boolean)} and returns itself.
     * Note: this feature is by default turned off.
     * @param clientCallingServerOnSelectionChange Whether or not the client should send events about text selection changes.
     * @return This.
     * @see #setClientCallingServerOnSelectionChange(boolean)
     */
    public SuperTextField withClientCallingServerOnSelectionChange(boolean clientCallingServerOnSelectionChange) {
        this.setClientCallingServerOnSelectionChange(clientCallingServerOnSelectionChange);
        return this;
    }
}
