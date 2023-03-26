package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.markers.HasRequired;

import java.util.function.Consumer;

/**
 * Configuration options for {@link HasRequired}.
 *
 * @author miki
 * @since 2022-09-09
 */
@Order(127)
public class HasRequiredBuilder implements ContentBuilder<HasRequired> {

    @Override
    public void buildContent(HasRequired component, Consumer<Component[]> callback) {
        callback.accept(new Component[]{
                new Checkbox("Mark field as required?", event -> {
                    component.setRequired(event.getValue());
                    if(component instanceof HasValue)
                        ((HasValue<?, ?>) component).setRequiredIndicatorVisible(event.getValue());
                })
        });
    }
}
