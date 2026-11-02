package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.notification.Notification;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.buttons.ClipboardCopyButton;
import org.vaadin.miki.superfields.buttons.ClipboardModifiers;
import org.vaadin.miki.superfields.buttons.ClipboardPasteButton;
import org.vaadin.miki.superfields.text.LabelField;
import org.vaadin.miki.superfields.text.SuperTextField;

import java.util.function.Consumer;

@Order(82)
public class ClipboardCopyButtonBuilder implements ContentBuilder<ClipboardCopyButton> {
  @Override
  public void buildContent(ClipboardCopyButton component, Consumer<Component[]> callback) {
    final SuperTextField source = new SuperTextField().withLabel("Type text here:").withHelperText("The text in this field will replace anything selected by the user on the page before they click the copy button.");
    final ClipboardCopyButton copyButton = new ClipboardCopyButton(ClipboardModifiers.replaceWithValue(source));
    copyButton.addClipboardCopyListener(event -> Notification.show("Value %s was copied to the clipboard directly from the text field.".formatted(event.getCopiedValue())));
    source.setSuffixComponent(copyButton);
    component.setClipboardModifierFunction(ClipboardModifiers.replaceWithValue(source));
    component.addClipboardCopyListener(event -> Notification.show("Value %s was copied to clipboard (modified from %s)".formatted(event.getCopiedValue(), event.getValueBeforeModification())));
    final Checkbox disableSource = new Checkbox("Disable the text field above?", event -> source.setEnabled(!event.getValue()));

    final ClipboardPasteButton pasteButton = new ClipboardPasteButton();
    final LabelField<String> target = new LabelField<String>().withLabel("Click button to paste from clipboard.").withValue("(nothing pasted yet)");

    pasteButton.addClipboardPasteListener(event -> target.setValue(event.getPastedValue()));

    callback.accept(new Component[]{source, disableSource, pasteButton, target});
  }
}
