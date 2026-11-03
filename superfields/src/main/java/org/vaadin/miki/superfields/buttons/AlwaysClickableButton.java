package org.vaadin.miki.superfields.buttons;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.dom.DomEvent;
import org.vaadin.miki.markers.Clickable;

/**
 * A button that can always be clicked (i.e. it cannot be disabled and it works even if placed on a disabled layout).
 *
 * @author miki
 * @since 2026-11-02
 */
@Tag("always-clickable-button")
@JsModule("./always-clickable-button.ts")
public class AlwaysClickableButton extends AbstractAlwaysClickableButton<AlwaysClickableButton> implements Clickable, ClickNotifier<AlwaysClickableButton> {

  @Override
  protected void handleDomEvent(DomEvent event) {
    this.click();
  }

  @Override
  public void click() {
    this.fireEvent(new ClickEvent<>(this));
  }
}
