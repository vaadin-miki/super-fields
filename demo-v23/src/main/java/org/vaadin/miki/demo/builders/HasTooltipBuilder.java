package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.shared.HasTooltip;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.text.SuperTextField;

import java.util.function.Consumer;

/**
 * Builds content for anything that implements {@link HasTooltip}.
 * @author jc
 * @since 2023-01-26
 */
@Order(119)
public class HasTooltipBuilder implements ContentBuilder<HasTooltip> {

    @Override
    public void buildContent(HasTooltip component, Consumer<Component[]> callback) {
        final SuperTextField title = new SuperTextField("Tooltip of the component: ");
        title.addValueChangeListener(event -> component.setTooltipText(event.getValue()));
        callback.accept(new Component[]{title});
    }
}
