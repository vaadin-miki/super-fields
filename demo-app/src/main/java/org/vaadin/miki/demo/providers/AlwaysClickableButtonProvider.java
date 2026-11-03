package org.vaadin.miki.demo.providers;

import com.vaadin.flow.component.icon.VaadinIcon;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.buttons.AlwaysClickableButton;

@Order(82)
public class AlwaysClickableButtonProvider implements ComponentProvider<AlwaysClickableButton> {
  @Override
  public AlwaysClickableButton getComponent() {
    return new AlwaysClickableButton().withText("Click me").withIcon(VaadinIcon.SUN.create()).withTooltipText("this button can be clicked even when placed in a disabled layout");
  }
}
