package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.lazyload.ObservedField;

import java.util.function.Consumer;

/**
 * Builds content for {@link ObservedField}.
 * @author miki
 * @since 2020-11-19
 */
@Order(90)
public class ObservedFieldBuilder implements ContentBuilder<ObservedField> {

    @Override
    @SuppressWarnings("squid:S5411")
    public void buildContent(ObservedField component, Consumer<Component[]> callback) {
        final Span description = new Span("An instance of ObservedField is added below these texts. It has a value change listener that updates the counter whenever the field is shown on screen, for example as a result of resizing window or scrolling. The value does not change when the field gets hidden due to styling (display: none). The field itself in an empty HTML and it cannot be normally seen, but it still is rendered by the browser.");
        final Span counterText = new Span("The field has become visible this many times so far: ");
        final Span counterLabel = new Span("0");
        final Span instruction = new Span("The field is rendered below this text. Resize the window a few times to hide this line to see the value change events being triggered.");
        counterLabel.addClassName("counter-label");
        component.addValueChangeListener(event -> {
            if(event.getValue())
                counterLabel.setText(String.valueOf(Integer.parseInt(counterLabel.getText()) + 1));
        });
        callback.accept(new Component[]{description, new HorizontalLayout(counterText, counterLabel), instruction});
    }
}
