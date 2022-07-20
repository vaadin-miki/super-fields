package org.vaadin.miki.superfields.variant.builder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import org.vaadin.miki.markers.HasLabel;
import org.vaadin.miki.superfields.text.LabelField;
import org.vaadin.miki.superfields.variant.ObjectPropertyComponentBuilder;
import org.vaadin.miki.superfields.variant.ObjectPropertyDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Reference implementation of {@link ObjectPropertyComponentBuilder}.
 *
 * @author miki
 * @since 2022-06-06
 */
public class SimplePropertyComponentBuilder implements ObjectPropertyComponentBuilder {

    private final Map<Class<?>, ObjectPropertyComponentBuilder> typeBuilders = new HashMap<>();

    private final Supplier<? extends Component> defaultBuilder = LabelField::new;

    private boolean defaultLabel = true;

    @SuppressWarnings("unchecked") // fine, I guess?
    @Override
    public <T, P, C extends Component & HasValue<?, P>> C buildPropertyField(ObjectPropertyDefinition<T, P> property) {
        final C result;
        if(this.typeBuilders.containsKey(property.getType()))
            result = this.typeBuilders.get(property.getType()).buildPropertyField(property);
        else result = (C) this.defaultBuilder.get();

        if(result instanceof HasLabel && this.isDefaultLabel())
            ((HasLabel) result).setLabel(property.getOwner().getSimpleName() + "." + property.getName());

        return result;
    }

    public boolean isDefaultLabel() {
        return defaultLabel;
    }

    public void setDefaultLabel(boolean defaultLabel) {
        this.defaultLabel = defaultLabel;
    }
}
