package org.vaadin.miki.superfields.text;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import org.vaadin.miki.events.text.TextSelectionListener;
import org.vaadin.miki.events.text.TextSelectionNotifier;
import org.vaadin.miki.markers.CanModifyText;
import org.vaadin.miki.markers.CanSelectText;
import org.vaadin.miki.markers.WithClearButtonMixin;
import org.vaadin.miki.markers.WithHelperMixin;
import org.vaadin.miki.markers.WithHelperPositionableMixin;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithPlaceholderMixin;
import org.vaadin.miki.markers.WithReceivingSelectionEventsFromClientMixin;
import org.vaadin.miki.markers.WithTitleMixin;
import org.vaadin.miki.markers.WithValueMixin;
import org.vaadin.miki.shared.text.TextModificationDelegate;

/**
 * An extension of {@link TextField} with some useful (hopefully) features.
 * @author miki
 * @since 2020-05-29
 */
@Tag("super-text-field")
@JsModule("./super-text-field.js")
@SuppressWarnings("squid:S110") // there is no way to reduce the number of parent classes
public class SuperTextField extends TextField implements CanSelectText, TextSelectionNotifier<SuperTextField>,
        CanModifyText,
        WithIdMixin<SuperTextField>, WithLabelMixin<SuperTextField>, WithPlaceholderMixin<SuperTextField>,
        WithValueMixin<AbstractField.ComponentValueChangeEvent<TextField, String>, String, SuperTextField>,
        WithHelperMixin<SuperTextField>, WithTitleMixin<SuperTextField>, WithHelperPositionableMixin<SuperTextField>,
        WithReceivingSelectionEventsFromClientMixin<SuperTextField>, WithClearButtonMixin<SuperTextField> {

    private final TextModificationDelegate<SuperTextField> delegate = new TextModificationDelegate<>(this, this.getEventBus(), this::getValue);

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
        this.delegate.onAttach(attachEvent, event -> super.onAttach(event));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        this.delegate.onDetach(detachEvent, event -> super.onDetach(event));
    }

    @Override
    public Registration addTextSelectionListener(TextSelectionListener<SuperTextField> listener) {
        return this.delegate.addTextSelectionListener(listener);
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

    @ClientCallable
    private void selectionChanged(int start, int end, String selection) {
        this.delegate.fireTextSelectionEvent(true, start, end, selection);
    }

    @ClientCallable
    private void reinitialiseListening() {
        this.delegate.reinitialiseListeners();
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
    public void modifyText(String replacement, int from, int to) {
        this.delegate.modifyText(replacement, from, to);
    }
}
