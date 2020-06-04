package org.vaadin.miki.superfields.text;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import org.vaadin.miki.events.text.TextSelectionEvent;
import org.vaadin.miki.events.text.TextSelectionListener;
import org.vaadin.miki.events.text.TextSelectionNotifier;
import org.vaadin.miki.markers.CanSelectText;
import org.vaadin.miki.markers.HasLabel;
import org.vaadin.miki.markers.HasPlaceholder;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithPlaceholderMixin;
import org.vaadin.miki.markers.WithReceivingSelectionEventsFromClientMixin;
import org.vaadin.miki.markers.WithValueMixin;
import org.vaadin.miki.shared.text.TextSelectionDelegate;

/**
 * An extension of {@link TextField} with some useful (hopefully) features.
 * @author miki
 * @since 2020-05-29
 */
@Tag("super-text-field")
@JsModule("./super-text-field.js")
@SuppressWarnings("squid:S110") // there is no way to reduce the number of parent classes
public class SuperTextField extends TextField implements CanSelectText, TextSelectionNotifier<SuperTextField>,
        HasLabel, HasPlaceholder,
        WithIdMixin<SuperTextField>,  WithLabelMixin<SuperTextField>, WithPlaceholderMixin<SuperTextField>,
        WithValueMixin<AbstractField.ComponentValueChangeEvent<TextField, String>, String, SuperTextField>,
        WithReceivingSelectionEventsFromClientMixin<SuperTextField> {

    private boolean receivingSelectionEventsFromClient = false;

    private final TextSelectionDelegate<SuperTextField> delegate = new TextSelectionDelegate<>(this);

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

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        this.delegate.informClientAboutSendingEvents(this.isReceivingSelectionEventsFromClient());
        super.onAttach(attachEvent);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // detaching means server should not be informed
        if(this.isReceivingSelectionEventsFromClient())
            this.delegate.informClientAboutSendingEvents(false);
        super.onDetach(detachEvent);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Registration addTextSelectionListener(TextSelectionListener<SuperTextField> listener) {
        return this.getEventBus().addListener((Class<TextSelectionEvent<SuperTextField>>)(Class<?>)TextSelectionEvent.class, listener);
    }

    @Override
    public void selectAll() {
        this.delegate.selectAll(this::getValue, this::getEventBus);
    }

    @Override
    public void selectNone() {
        this.delegate.selectNone(this::getEventBus);
    }

    @Override
    public void select(int from, int to) {
        this.delegate.select(this::getValue, this::getEventBus, from, to);
    }

    @ClientCallable
    private void selectionChanged(int start, int end, String selection) {
        TextSelectionEvent<SuperTextField> event = new TextSelectionEvent<>(this, true, start, end, selection);
        this.delegate.fireTextSelectionEvent(this.getEventBus(), event);
    }

    @Override
    public boolean isReceivingSelectionEventsFromClient() {
        return this.receivingSelectionEventsFromClient;
    }

    @Override
    public void setReceivingSelectionEventsFromClient(boolean receivingSelectionEventsFromClient) {
        this.receivingSelectionEventsFromClient = receivingSelectionEventsFromClient;
        this.delegate.informClientAboutSendingEvents(receivingSelectionEventsFromClient);
    }

    @Override
    public void setValue(String value) {
        this.delegate.updateAttributeOnValueChange(this::getEventBus);
        super.setValue(value);
    }
}
