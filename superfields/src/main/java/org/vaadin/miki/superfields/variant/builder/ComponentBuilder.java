package org.vaadin.miki.superfields.variant.builder;

import com.vaadin.flow.component.HasValue;
import org.vaadin.miki.superfields.variant.ObjectPropertyDefinition;

@FunctionalInterface
public interface ComponentBuilder<P> {

    HasValue<?, P> buildPropertyField(ObjectPropertyDefinition<?, P> property);

}
