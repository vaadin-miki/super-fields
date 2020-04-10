package org.vaadin.miki.superfields.tabs;

import com.vaadin.flow.component.Component;

/**
 * Marker interface for objects that produce components for tabs.
 * @param <V> Type of object to generate content for.
 * @author miki
 * @since 2020-04-10
 */
@FunctionalInterface
public interface TabsContentGenerator<V> {

    /**
     * Creates a new instance of a component that corresponds to the given object.
     * @param object Object to generate component for.
     * @return Content to be added to tabs. Must not be {@code null}.
     */
    Component generateComponent(V object);

}
