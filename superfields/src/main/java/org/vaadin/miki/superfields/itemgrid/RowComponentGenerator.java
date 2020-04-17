package org.vaadin.miki.superfields.itemgrid;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;

/**
 * Interface for objects generating a component for each row in an {@link ItemGrid}.
 * @author miki
 * @since 2020-04-16
 */
@FunctionalInterface
public interface RowComponentGenerator<C extends Component & HasComponents> {

    /**
     * Creates a component that corresponds to a given row number.
     * @param rowNumber Number of the row to create component for. 0-based.
     * @return A component. Must not be {@code null}.
     */
    C generateRowComponent(int rowNumber);

}
