package org.vaadin.miki.demo.providers;

import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.unload.UnloadObserver;

/**
 * Provides {@link UnloadObserver}.
 * @author miki
 * @since 2020-11-18
 */
@Order(140)
public class UnloadObserverProvider implements ComponentProvider<UnloadObserver> {

    @Override
    public UnloadObserver getComponent() {
        return UnloadObserver.get().withoutQueryingOnUnload();
    }
}
