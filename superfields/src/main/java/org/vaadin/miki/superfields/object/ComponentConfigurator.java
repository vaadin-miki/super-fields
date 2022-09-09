package org.vaadin.miki.superfields.object;

import com.vaadin.flow.component.HasValue;

/**
 * Marker interface for objects capable of configuring a component used in {@link ObjectField}{@code <T>}.
 * This means any configuration <strong>other than</strong> calling {@link HasValue#setValue(Object)}, as that method is called by {@link ObjectField} automatically.
 *
 * @param <T> Type of object used in {@link ObjectField}.
 *
 * @author miki
 * @since 2022-09-07
 */
@FunctionalInterface
public interface ComponentConfigurator<T> {

    /**
     * Configures a given component. Note: do not call {@link HasValue#setValue(Object)} on the given {@code component}.
     * @param object Object that is the source of the value shown in the {@link ObjectField}.
     * @param definition Property that the {@code component} corresponds to.
     * @param component Component used to display the value of the property (in {@code definition}) of the given {@code object}.
     * @implSpec The implementations <strong>must not</strong> call {@link HasValue#setValue(Object)} on the given {@code component}.
     */
    void configureComponent(T object, Property<T, ?> definition, HasValue<?, ?> component);

}
