package org.vaadin.miki.superfields.tabs;

import com.vaadin.flow.component.tabs.Tab;

import java.io.Serializable;

/**
 * Marker interface for objects that produce tabs.
 * @param <V> Type of object to generate tabs for.
 * @author miki
 * @since 2020-04-10
 */
@FunctionalInterface
public interface TabHeaderGenerator<V> extends Serializable {

    /**
     * Creates a new instance of a tab that corresponds to the given object.
     * @param object Object to generate tab for.
     * @return Tab to be added to tabs. Must not be {@code null}.
     */
    Tab generateTab(V object);

}
