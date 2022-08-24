package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.shared.HasClearButton;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;

import java.util.function.Consumer;

/**
 * Builds content for {@link HasClearButton}.
 * @author miki
 * @since 2022-01-10
 */
@Order(77)
public class HasClearButtonBuilder implements ContentBuilder<HasClearButton> {

    @Override
    public void buildContent(HasClearButton component, Consumer<Component[]> callback) {
        final Checkbox checkbox = new Checkbox("Clear button visible?", event -> component.setClearButtonVisible(event.getValue()));
        checkbox.setValue(component.isClearButtonVisible());
        callback.accept(new Component[]{checkbox});
    }
}
