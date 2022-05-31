package org.vaadin.miki.superfields.variant;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;

/**
 * @author miki
 * @since 2022-05-19
 */
@FunctionalInterface
public interface ObjectPropertyFieldBuilder<T> {

    <P, C extends Component & HasValue<?, P>> C buildPropertyField(ObjectPropertyDefinition<T, P> property);

}
