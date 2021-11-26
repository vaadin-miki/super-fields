package org.vaadin.miki.superfields.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;

import java.util.Optional;

/**
 * Marker interface for objects that have a header.
 * @param <H> Type of the header {@link Component}.
 *
 * @author miki
 * @since 2021-09-03
 */
@FunctionalInterface
public interface HasHeader<H extends Component & HasComponents> {

    /**
     * Gets the current header.
     * @return Perhaps the current header.
     */
    Optional<H> getHeader();

}
