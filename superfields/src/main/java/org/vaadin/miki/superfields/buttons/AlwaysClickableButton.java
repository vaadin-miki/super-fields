package org.vaadin.miki.superfields.buttons;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.dom.DomEvent;

/**
 *
 *
 * @author miki
 * @since 2026-11-02
 */
@Tag("always-clickable-button")
@JsModule("./always-clickable-button.ts")
public class AlwaysClickableButton extends AbstractAlwaysClickableButton {
  @Override
  protected void handleDomEvent(DomEvent event) {
    this.fireEvent(new ClickEvent<>(this));
  }

}
