package org.vaadin.miki.superfields.object;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.function.SerializableSupplier;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;

import java.util.List;
import java.util.Optional;

/**
 * Default implementation of {@link PropertyGroupLayoutProvider}.
 * When a group consists of 2 or more elements, it returns a layout for it. Otherwise, it returns no layout.
 *
 * @author miki
 * @since 2022-09-01
 */
public class DefaultPropertyGroupLayoutProvider implements PropertyGroupLayoutProvider {

    private final SerializableSupplier<?> layoutProvider;

    public DefaultPropertyGroupLayoutProvider() {
        this(FlexLayoutHelpers::row);
    }

    public <C extends Component & HasComponents> DefaultPropertyGroupLayoutProvider(SerializableSupplier<C> newLayoutProvider) {
        this.layoutProvider = newLayoutProvider;
    }

    @Override
    @SuppressWarnings("unchecked") // all should be fine
    public <T, C extends Component & HasComponents> Optional<C> buildGroupLayout(String groupName, List<Property<T, ?>> definitions) {
        // no layout for empty groups or groups of just one component
        if(definitions.size() < 2)
            return Optional.empty();
        else return Optional.of((C)this.layoutProvider.get());
    }
}
