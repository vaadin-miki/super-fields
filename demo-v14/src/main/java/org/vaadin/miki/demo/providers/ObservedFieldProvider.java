package org.vaadin.miki.demo.providers;

import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.superfields.lazyload.ObservedField;

/**
 * Provides {@link ObservedField}.
 * @author miki
 * @since 2020-11-17
 */
public class ObservedFieldProvider implements ComponentProvider<ObservedField> {
    @Override
    public ObservedField getComponent() {
        return new ObservedField();
    }
}
