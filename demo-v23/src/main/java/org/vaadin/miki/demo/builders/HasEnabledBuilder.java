package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.checkbox.Checkbox;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;

import java.util.function.Consumer;

/**
 * Builds content for {@link HasEnabled}.
 * @author miki
 * @since 2021-09-04
 */
@Order(19)
public class HasEnabledBuilder implements ContentBuilder<HasEnabled> {

    @Override
    public void buildContent(HasEnabled component, Consumer<Component[]> callback) {
        callback.accept(new Component[]{
                new Checkbox("Mark component as disabled?", event -> component.setEnabled(!event.getValue()))
        });

    }
}
