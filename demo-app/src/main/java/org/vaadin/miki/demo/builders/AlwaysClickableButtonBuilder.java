package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.notification.Notification;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.buttons.AlwaysClickableButton;
import org.vaadin.miki.superfields.buttons.ClipboardCopyButton;
import org.vaadin.miki.superfields.buttons.ClipboardPasteButton;
import org.vaadin.miki.superfields.text.SuperTextField;
import org.vaadin.miki.util.ClipboardModifiers;

import java.util.function.Consumer;

@Order(82)
public class AlwaysClickableButtonBuilder implements ContentBuilder<AlwaysClickableButton> {
  @Override
  public void buildContent(AlwaysClickableButton component, Consumer<Component[]> callback) {
    component.setWidth("220px");
    component.setHeight("80px");
    component.addClickListener(event -> Notification.show("AlwaysClickableButton has been clicked"));

    // show also copy and paste buttons
    final SuperTextField source = new SuperTextField().withLabel("Type text here:").withHelperText("The text in this field will replace anything selected by the user on the page before they click the copy button.");
    final ClipboardCopyButton copyButton = new ClipboardCopyButton(ClipboardModifiers.replaceWithValue(source));
    copyButton.addClipboardCopyListener(event -> Notification.show("Value %s was copied to the clipboard directly from the text field.".formatted(event.getCopiedValue())));
    source.setSuffixComponent(copyButton);

    final Checkbox disableSource = new Checkbox("Disable the text field above?", event -> source.setEnabled(!event.getValue()));

    final ClipboardPasteButton pasteButton = new ClipboardPasteButton();
    final SuperTextField target = new SuperTextField().withLabel("Pasted from clipboard").withPlaceholder("(nothing here)");
    target.setSuffixComponent(pasteButton);
    target.setEnabled(false);
    pasteButton.addClipboardPasteListener(event -> target.setValue(event.getPastedValue()));

    callback.accept(new Component[]{source, disableSource, target});
  }
}
