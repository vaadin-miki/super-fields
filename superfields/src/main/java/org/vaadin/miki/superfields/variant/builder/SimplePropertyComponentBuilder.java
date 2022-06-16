package org.vaadin.miki.superfields.variant.builder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
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

    @SuppressWarnings("unchecked") // fine, I guess?
    @Override
    public <T, P, C extends Component & HasValue<?, P>> C buildPropertyField(ObjectPropertyDefinition<T, P> property) {
        if(this.typeBuilders.containsKey(property.getType()))
            return this.typeBuilders.get(property.getType()).buildPropertyField(property);
        else return (C) this.defaultBuilder.get();
    }
}
