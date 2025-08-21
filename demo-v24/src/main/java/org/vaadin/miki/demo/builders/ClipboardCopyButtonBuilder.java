package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.buttons.ClipboardCopyButton;
import org.vaadin.miki.superfields.buttons.ClipboardPasteButton;
import org.vaadin.miki.superfields.text.LabelField;
import org.vaadin.miki.superfields.text.SuperTextField;

import java.util.function.Consumer;

@Order(82)
public class ClipboardCopyButtonBuilder implements ContentBuilder<ClipboardCopyButton> {
  @Override
  public void buildContent(ClipboardCopyButton component, Consumer<Component[]> callback) {
    final SuperTextField source = new SuperTextField().withLabel("Type text here:");
    component.setSource(source);

    final ClipboardPasteButton pasteButton = new ClipboardPasteButton();
    final LabelField<String> target = new LabelField<String>().withLabel("Click button to paste from clipboard.");
    pasteButton.setTarget(target);

    callback.accept(new Component[]{source, pasteButton, target});
  }
}
