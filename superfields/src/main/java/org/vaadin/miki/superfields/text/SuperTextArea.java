package org.vaadin.miki.superfields.text;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.textfield.TextArea;
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
import org.vaadin.miki.markers.WithLabelPositionableMixin;
import org.vaadin.miki.markers.WithReceivingSelectionEventsFromClientMixin;
import org.vaadin.miki.markers.WithRequiredMixin;
import org.vaadin.miki.markers.WithTitleMixin;
import org.vaadin.miki.markers.WithValueMixin;
import org.vaadin.miki.shared.text.TextModificationDelegate;

import java.util.Objects;

/**
 * An extension of {@link TextArea} with some useful features.
 * @author miki
 * @since 2020-06-01
 */
@Tag("super-text-area")
@JsModule("./super-text-area.js")
@CssImport(value = "./styles/label-positions.css", themeFor = "super-text-area")
@SuppressWarnings("squid:S110") // there is no way to reduce the number of parent classes
public class SuperTextArea extends TextArea implements CanSelectText, TextSelectionNotifier<SuperTextArea>,
        CanModifyText, WithRequiredMixin<SuperTextArea>, WithLabelPositionableMixin<SuperTextArea>,
        WithIdMixin<SuperTextArea>, WithLabelMixin<SuperTextArea>, WithPlaceholderMixin<SuperTextArea>,
        WithReceivingSelectionEventsFromClientMixin<SuperTextArea>, WithClearButtonMixin<SuperTextArea>,
        WithHelperMixin<SuperTextArea>, WithHelperPositionableMixin<SuperTextArea>, WithTitleMixin<SuperTextArea>,
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

    @Override
    public void setTitle(String title) {
        this.getElement().setProperty("title", Objects.requireNonNullElse(title, ""));
    }

    @Override
    public String getTitle() {
        return Objects.requireNonNullElse(this.getElement().getProperty("title"), "");
    }

    @SuppressWarnings("squid:S1185") // removing this method makes the class impossible to compile due to missing methods
    @Override
    public void setClearButtonVisible(boolean clearButtonVisible) {
        super.setClearButtonVisible(clearButtonVisible);
    }

    @SuppressWarnings("squid:S1185") // same comment as above
    @Override
    public boolean isClearButtonVisible() {
        return super.isClearButtonVisible();
    }
}
