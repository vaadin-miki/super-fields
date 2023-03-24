package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.numbers.AbstractSuperFloatingPointField;

import java.util.function.Consumer;

/**
 * Builds content for {@link AbstractSuperFloatingPointField}.
 * @author miki
 * @since 2021-08-09
 */
@Order(28)
@SuppressWarnings("squid:S5411") // no way around boxed values
public class AbstractSuperFloatingPointFieldBuilder implements ContentBuilder<AbstractSuperFloatingPointField<?, ?>> {
    @Override
    public void buildContent(AbstractSuperFloatingPointField<?, ?> component, Consumer<Component[]> callback) {
        final Checkbox integerPartOptional = new Checkbox("Make integer part optional?");
        integerPartOptional.addValueChangeListener(event -> component.setIntegerPartOptional(event.getValue()));
        callback.accept(new Component[]{integerPartOptional});
    }
}
