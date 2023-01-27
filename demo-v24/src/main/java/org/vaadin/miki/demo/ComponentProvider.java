package org.vaadin.miki.demo;

import com.vaadin.flow.component.Component;
import org.atteo.classindex.IndexSubclasses;

/**
 * Marker interface for objects that provide an instance of a Vaadin {@link Component}.
 * It is assumed that implementations of this interface provide a public, no-arg constructor.
 * @param <T> Type of the component to be created.
 * @author miki
 * @since 2020-11-17
 */
@FunctionalInterface
@IndexSubclasses
public interface ComponentProvider<T extends Component> {

    /**
     * Builds a fresh instance of a component.
     * @return A non-null instance of an object.
     */
    T getComponent();

}
