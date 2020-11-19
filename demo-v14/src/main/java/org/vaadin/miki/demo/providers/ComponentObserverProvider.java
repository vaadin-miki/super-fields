package org.vaadin.miki.demo.providers;

import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.superfields.lazyload.ComponentObserver;

/**
 * Provides {@link ComponentObserver}.
 * @author miki
 * @since 2020-11-18
 */
public class ComponentObserverProvider implements ComponentProvider<ComponentObserver> {
    @Override
    public ComponentObserver getComponent() {
        return new ComponentObserver();
    }
}
