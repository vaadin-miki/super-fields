package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.markers.HasTitle;
import org.vaadin.miki.superfields.text.SuperTextField;

import java.util.function.Consumer;

/**
 * Builds content for anything that implements {@link HasTitle}.
 * @author miki
 * @since 2021-09-13
 */
@Order(118)
public class HasTitleBuilder implements ContentBuilder<HasTitle> {

    @Override
    public void buildContent(HasTitle component, Consumer<Component[]> callback) {
        final SuperTextField title = new SuperTextField("Title (tooltip) of the component: ");
        title.addValueChangeListener(event -> component.setTitle(event.getValue()));
        callback.accept(new Component[]{title});
    }
}
