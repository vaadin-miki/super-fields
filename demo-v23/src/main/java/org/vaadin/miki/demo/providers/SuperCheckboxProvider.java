package org.vaadin.miki.demo.providers;

import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.checkbox.SuperCheckbox;

/**
 * Provides a fresh {@link SuperCheckbox}.
 * @author miki
 * @since 2022-09-15
 */
@Order(99)
public class SuperCheckboxProvider implements ComponentProvider<SuperCheckbox> {
    @Override
    public SuperCheckbox getComponent() {
        return new SuperCheckbox().withLabel("This component can be made read-only, contrary to the original Checkbox.");
    }
}
