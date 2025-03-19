package org.vaadin.miki.demo.providers;

import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.enabler.TimedEnabler;
import org.vaadin.miki.superfields.text.SuperTextField;

/**
 * Provides a new instance of a {@link TimedEnabler}.
 *
 * @author miki
 * @since 2025-03-19
 */
@Order(180)
public class TimedEnablerProvider implements ComponentProvider<TimedEnabler<SuperTextField>> {
  @Override
  public TimedEnabler<SuperTextField> getComponent() {
    final SuperTextField field = new SuperTextField().withHelperText("(there is a delay after setting this component to enabled or disabled)");
    final TimedEnabler<SuperTextField> enabler = new TimedEnabler<>(field);
    enabler.setEnabledTimeout(2000);
    enabler.setDisabledTimeout(2000);
    return enabler;
  }
}
