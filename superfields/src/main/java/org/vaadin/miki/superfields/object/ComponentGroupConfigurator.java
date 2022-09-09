package org.vaadin.miki.superfields.object;

import com.vaadin.flow.component.HasValue;

import java.util.List;

/**
 * Configures an entire group of components.
 *
 * @author miki
 * @since 2022-09-01
 */
@FunctionalInterface
public interface ComponentGroupConfigurator {

    /**
     * Configures the given group of components.
     * @param object Object about to be displayed.
     * @param groupName Name of the group. May be {@code null} to indicate that all components (regardless of their individual groups) are given for configuration.
     * @param definitions Definitions of properties in the group.
     * @param components Components that correspond to the properties. Note: it is possible that the list is not in the order of definitions.
     * @param <T> Type of the object.
     */
    <T> void configureComponentGroup(T object, String groupName, List<Property<T, ?>> definitions, List<? extends HasValue<?, ?>> components);

}
