package org.vaadin.miki.superfields.text;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
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
import org.vaadin.miki.markers.WithInvalidInputPreventionMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithLabelPositionableMixin;
import org.vaadin.miki.markers.WithPlaceholderMixin;
import org.vaadin.miki.markers.WithReceivingSelectionEventsFromClientMixin;
import org.vaadin.miki.markers.WithRequiredMixin;
import org.vaadin.miki.markers.WithTextInputModeMixin;
import org.vaadin.miki.markers.WithTooltipMixin;
import org.vaadin.miki.markers.WithValueMixin;
import org.vaadin.miki.shared.text.TextInputMode;
import org.vaadin.miki.shared.text.TextModificationDelegate;

import java.util.Objects;

/**
 * An extension of {@link TextField} with some useful (hopefully) features.
 *
 * @author miki
 * @since 2020-05-29
 */
@Tag("super-text-field")
@JsModule("./super-text-field.js")
@CssImport(value = "./styles/label-positions.css", themeFor = "super-text-field")
@SuppressWarnings("squid:S110") // there is no way to reduce the number of parent classes
public class SuperTextField extends TextField implements CanSelectText, TextSelectionNotifier<SuperTextField>,
    CanModifyText, WithRequiredMixin<SuperTextField>, WithLabelPositionableMixin<SuperTextField>,
    WithIdMixin<SuperTextField>, WithLabelMixin<SuperTextField>, WithPlaceholderMixin<SuperTextField>,
    WithValueMixin<AbstractField.ComponentValueChangeEvent<TextField, String>, String, SuperTextField>,
    WithHelperMixin<SuperTextField>, WithHelperPositionableMixin<SuperTextField>,
    WithReceivingSelectionEventsFromClientMixin<SuperTextField>, WithClearButtonMixin<SuperTextField>,
    WithTooltipMixin<SuperTextField>, WithTextInputModeMixin<SuperTextField>,
    WithInvalidInputPreventionMixin<SuperTextField> {

    private final TextModificationDelegate<SuperTextField> delegate = new TextModificationDelegate<>(this, this.getEventBus(), this::getValue);
    private TextInputMode textInputMode;

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
  private void performDelayedInitialisation() {
    // fixes #243
    this.delegate.reinitialiseListeners();
    // fixes #513
    this.doChangeTextInputMode(this.getTextInputMode());
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

  @Override
  public void setTextInputMode(TextInputMode inputMode) {
    // only when there is a change
    if (!Objects.equals(inputMode, this.getTextInputMode()))
      this.doChangeTextInputMode(inputMode);
  }

  /**
   * Forces the change of the input mode.
   *
   * @param inputMode New input mode.
   */
  protected void doChangeTextInputMode(TextInputMode inputMode) {
    this.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this, context -> {
      // js courtesy of the one and only JC, thank you!
      if (inputMode != null)
        this.getElement().executeJs("if(this.inputElement !== undefined) {this.inputElement.inputMode = $0;}", inputMode.name().toLowerCase());
      else this.getElement().executeJs("delete this.inputElement.inputMode;");
      this.textInputMode = inputMode;
    }));
  }

  @Override
  public TextInputMode getTextInputMode() {
    return this.textInputMode;
  }

    @Override
    public void setPreventingInvalidInput(boolean prevent) {
        this.setPreventInvalidInput(prevent);
    }

    @Override
    public boolean isPreventingInvalidInput() {
        return this.isPreventInvalidInput();
    }

  @SuppressWarnings("squid:S1185") // removing this method makes the class impossible to compile due to missing methods
  @Override
  public void setClearButtonVisible(boolean clearButtonVisible) {
    super.setClearButtonVisible(clearButtonVisible);
  }

  @SuppressWarnings("squid:S1185") // see above
  @Override
  public boolean isClearButtonVisible() {
    return super.isClearButtonVisible();
  }

    @Override
    public void setTooltipText(String tooltipText) {
        super.setTitle(tooltipText);
    }

    @Override
    public String getTooltipText() {
        return super.getTitle();
    }
}
