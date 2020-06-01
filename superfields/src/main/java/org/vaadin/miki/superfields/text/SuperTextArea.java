package org.vaadin.miki.superfields.text;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.shared.Registration;
import org.vaadin.miki.markers.HasLabel;
import org.vaadin.miki.markers.HasPlaceholder;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithPlaceholderMixin;
import org.vaadin.miki.markers.WithValueMixin;

/**
 * An extension of {@link TextArea} with some useful features.
 * @author miki
 * @since 2020-06-01
 */
@Tag("super-text-area")
@JsModule("./super-text-area.js")
@SuppressWarnings("squid:S110") // there is no way to reduce the number of parent classes
public class SuperTextArea extends TextArea implements CanSelectText, TextSelectionNotifier<SuperTextArea>,
        HasLabel, HasPlaceholder, WithIdMixin<SuperTextArea>, WithLabelMixin<SuperTextArea>, WithPlaceholderMixin<SuperTextArea>,
        WithReceivingSelectionEventsFromClientMixin<SuperTextArea>,
        WithValueMixin<AbstractField.ComponentValueChangeEvent<TextArea, String>, String, SuperTextArea> {

    private final TextSelectionDelegate<SuperTextArea> delegate = new TextSelectionDelegate<>(this);

    private boolean receivingSelectionEventsFromClient = false;

    public SuperTextArea() {
    }

    public SuperTextArea(String label) {
        super(label);
    }

    public SuperTextArea(String label, String placeholder) {
        super(label, placeholder);
    }

    public SuperTextArea(String label, String initialValue, String placeholder) {
        super(label, initialValue, placeholder);
    }

    public SuperTextArea(ValueChangeListener<? super ComponentValueChangeEvent<TextArea, String>> listener) {
        super(listener);
    }

    public SuperTextArea(String label, ValueChangeListener<? super ComponentValueChangeEvent<TextArea, String>> listener) {
        super(label, listener);
    }

    public SuperTextArea(String label, String initialValue, ValueChangeListener<? super ComponentValueChangeEvent<TextArea, String>> listener) {
        super(label, initialValue, listener);
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

    @Override
    @SuppressWarnings("unchecked")
    public Registration addTextSelectionListener(TextSelectionListener<SuperTextArea> listener) {
        return this.getEventBus().addListener((Class<TextSelectionEvent<SuperTextArea>>)(Class<?>)TextSelectionEvent.class, listener);
    }

    @ClientCallable
    private void selectionChanged(int start, int end, String selection) {
        TextSelectionEvent<SuperTextArea> event = new TextSelectionEvent<>(this, true, start, end, selection);
        this.delegate.fireTextSelectionEvent(this.getEventBus(), event);
    }

    @Override
    public void setValue(String value) {
        this.delegate.updateAttributeOnValueChange(this::getEventBus);
        super.setValue(value);
    }
}
