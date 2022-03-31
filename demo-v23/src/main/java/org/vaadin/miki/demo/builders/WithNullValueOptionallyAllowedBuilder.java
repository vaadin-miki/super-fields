package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.markers.WithNullValueOptionallyAllowedMixin;

import java.util.function.Consumer;

/**
 * Builds content for {@link WithNullValueOptionallyAllowedMixin}.
 * @author miki
 * @since 2020-11-19
 */
@Order(40)
public class WithNullValueOptionallyAllowedBuilder implements ContentBuilder<WithNullValueOptionallyAllowedMixin<?, ?, ?>> {

    @Override
    public void buildContent(WithNullValueOptionallyAllowedMixin<?, ?, ?> component, Consumer<Component[]> callback) {
        final Checkbox allow = new Checkbox("Allow empty string as null value?", event -> component.setNullValueAllowed(event.getValue()));
        callback.accept(new Component[]{allow});
    }
}
