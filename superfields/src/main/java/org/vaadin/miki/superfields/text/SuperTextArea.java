package org.vaadin.miki.superfields.text;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.shared.Registration;
import org.vaadin.miki.events.text.TextSelectionListener;
import org.vaadin.miki.events.text.TextSelectionNotifier;
import org.vaadin.miki.markers.CanModifyText;
import org.vaadin.miki.markers.CanSelectText;
import org.vaadin.miki.markers.WithHelperMixin;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithPlaceholderMixin;
import org.vaadin.miki.markers.WithReceivingSelectionEventsFromClientMixin;
import org.vaadin.miki.markers.WithValueMixin;
import org.vaadin.miki.shared.text.TextModificationDelegate;

/**
 * An extension of {@link TextArea} with some useful features.
 * @author miki
 * @since 2020-06-01
 */
@Tag("super-text-area")
@JsModule("./super-text-area.js")
@SuppressWarnings("squid:S110") // there is no way to reduce the number of parent classes
public class SuperTextArea extends TextArea implements CanSelectText, TextSelectionNotifier<SuperTextArea>,
        CanModifyText,
        WithIdMixin<SuperTextArea>, WithLabelMixin<SuperTextArea>, WithPlaceholderMixin<SuperTextArea>,
        WithReceivingSelectionEventsFromClientMixin<SuperTextArea>,
        WithHelperMixin<SuperTextArea>,
        WithValueMixin<AbstractField.ComponentValueChangeEvent<TextArea, String>, String, SuperTextArea> {

    private final TextModificationDelegate<SuperTextArea> delegate = new TextModificationDelegate<>(this, this.getEventBus(), this::getValue);

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
    protected void onAttach(AttachEvent attachEvent) {
        this.delegate.onAttach(attachEvent, super::onAttach);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        this.delegate.onDetach(detachEvent, super::onDetach);
    }

    @Override
    public boolean isReceivingSelectionEventsFromClient() {
        return this.delegate.isReceivingSelectionEventsFromClient();
    }

    @Override
    public void setReceivingSelectionEventsFromClient(boolean receivingSelectionEventsFromClient) {
        this.delegate.setReceivingSelectionEventsFromClient(receivingSelectionEventsFromClient);
    }

    @Override
    public void selectAll() {
        this.delegate.selectAll();
    }

    @Override
    public void selectNone() {
        this.delegate.selectNone();
    }

    @Override
    public void select(int from, int to) {
        this.delegate.select(from, to);
    }

    @Override
    public void modifyText(String replacement, int from, int to) {
        this.delegate.modifyText(replacement, from, to);
    }

    @Override
    public Registration addTextSelectionListener(TextSelectionListener<SuperTextArea> listener) {
        return this.delegate.addTextSelectionListener(listener);
    }

    @ClientCallable
    private void selectionChanged(int start, int end, String selection) {
        this.delegate.fireTextSelectionEvent(true, start, end, selection);
    }

    @ClientCallable
    private void reinitialiseListening() {
        this.delegate.reinitialiseListeners();
    }

}
