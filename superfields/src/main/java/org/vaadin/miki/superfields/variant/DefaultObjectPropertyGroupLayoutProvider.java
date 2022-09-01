package org.vaadin.miki.superfields.variant;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.function.SerializableSupplier;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;

import java.util.List;
import java.util.Optional;

/**
 * Default implementation of {@link ObjectPropertyGroupLayoutProvider}.
 * @author miki
 * @since 2022-09-01
 */
public class DefaultObjectPropertyGroupLayoutProvider implements ObjectPropertyGroupLayoutProvider {

    private final SerializableSupplier<?> layoutProvider;

    public DefaultObjectPropertyGroupLayoutProvider() {
        this(FlexLayoutHelpers::row);
    }

    public <C extends Component & HasComponents> DefaultObjectPropertyGroupLayoutProvider(SerializableSupplier<C> newLayoutProvider) {
        this.layoutProvider = newLayoutProvider;
    }

    @Override
    @SuppressWarnings("unchecked") // all should be fine
    public <T, C extends Component & HasComponents> Optional<C> buildGroupLayout(String groupName, List<ObjectPropertyDefinition<T, ?>> definitions) {
        // no layout for empty groups or groups of just one component
        if(definitions.size() < 2)
            return Optional.empty();
        else return Optional.of((C)this.layoutProvider.get());
    }
}
