package org.vaadin.miki.demo.providers;

import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.buttons.ClipboardCopyButton;

@Order(82)
public class ClipboardCopyButtonProvider implements ComponentProvider<ClipboardCopyButton> {
  @Override
  public ClipboardCopyButton getComponent() {
    return new ClipboardCopyButton();
  }
}
