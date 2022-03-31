package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.markers.HasHelperPositionable;

import java.util.function.Consumer;

/**
 * Builds configuration options for {@link HasHelperPositionable}.
 * @author miki
 * @since 2021-10-29
 */
@Order(117)
public class HasHelperPositionableBuilder implements ContentBuilder<HasHelperPositionable> {

    @Override
    public void buildContent(HasHelperPositionable component, Consumer<Component[]> callback) {
        callback.accept(new Component[]{
                new Checkbox("Position helper above the component?", event -> component.setHelperAbove(event.getValue())),
                new Span("Note: this will work only if the demoed component has a helper (and not all have).")
        });
    }
}
