package org.vaadin.miki;

import com.vaadin.browserless.ComponentTester;
import com.vaadin.flow.component.Component;

/**
 * Clicks a component the way a browser does, by firing a DOM {@code click} event on its element.
 *
 * <p>Browserless' own {@code Clickable.click()} fires a Vaadin {@code ClickEvent} through the component's
 * event bus, which never reaches listeners registered with
 * {@code getElement().addEventListener("click", ...)}. Components wired that way - {@code ItemGrid} cells,
 * for example - need a DOM event instead.</p>
 *
 * @author miki
 * @since 2026-09-11
 */
public class DomClickTester extends ComponentTester<Component> {

  public DomClickTester(Component component) {
    super(component);
  }

  /**
   * Clicks the component, provided a user could have clicked it.
   *
   * @throws IllegalStateException when the component is detached, hidden or disabled.
   */
  public void click() {
    this.ensureComponentIsUsable();
    this.fireDomEvent("click");
  }

}
