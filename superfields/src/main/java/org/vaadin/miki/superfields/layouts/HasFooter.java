package org.vaadin.miki.superfields.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;

import java.util.Optional;

/**
 * Marker interface for objects that have a footer.
 * @param <F> Type of the footer {@link Component}.
 *
 * @author miki
 * @since 2021-09-03
 */
@FunctionalInterface
public interface HasFooter<F extends Component & HasComponents> {

    /**
     * Gets the current footer.
     * @return Perhaps the current footer.
     */
    Optional<F> getFooter();

}
