package org.vaadin.miki.demo;

import com.vaadin.flow.component.Component;
import org.atteo.classindex.IndexSubclasses;

import java.util.function.Consumer;

/**
 * Marker interface for objects that build content for components.
 * Implementations of this interface must have a public no-arg constructor.
 * @param <T> Type of the object to build content for.
 * @author miki
 * @since 2020-11-18
 */
@IndexSubclasses
@FunctionalInterface
public interface ContentBuilder<T> {

    /**
     * Builds content.
     * @param component {@link Component} to build content for. Already cast to {@code T} for easier use.
     * @param callback Callback to call when content is built.
     */
    void buildContent(T component, Consumer<Component[]> callback);

}
