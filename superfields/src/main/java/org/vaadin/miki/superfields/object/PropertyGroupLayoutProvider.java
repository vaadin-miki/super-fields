package org.vaadin.miki.superfields.object;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Builds a layout, if needed, for a given group of {@link Property}s.
 * @author miki
 * @since 2022-09-01
 */
public interface PropertyGroupLayoutProvider extends Serializable {

    /**
     * Builds a layout for a given group.
     * @param groupName Name of the group.
     * @param definitions A list of {@link Property}s within the group.
     * @return A layout for the group, if any needed. An empty {@link Optional} means the corresponding components will be added to the main layout of the parent component instead.
     * @param <C> Type to ensure the returned object is both a {@link Component} that {@link HasComponents}.
     * @param <T> Type of the object in {@link Property}s.
     */
    <T, C extends Component & HasComponents> Optional<C> buildGroupLayout(String groupName, List<Property<T, ?>> definitions);

}
