package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.slf4j.LoggerFactory;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.unload.UnloadObserver;

import java.util.function.Consumer;

/**
 * Builds content for {@link UnloadObserver}.
 * @author miki
 * @since 2020-11-19
 */
@Order(110)
public class UnloadObserverBuilder implements ContentBuilder<UnloadObserver> {

    @Override
    public void buildContent(UnloadObserver component, Consumer<Component[]> callback) {
        final Checkbox query = new Checkbox("Query on window unload?", event -> component.setQueryingOnUnload(event.getValue()));
        final Span description = new Span("This component optionally displays a browser-native window when leaving this app. Select the checkbox above and try to close the window or tab to see it in action.");
        final Span counterText = new Span("There were this many attempts to leave this app so far: ");
        final Span counter = new Span("0");
        component.addUnloadListener(event -> {
            if(event.isBecauseOfQuerying())
                counter.setText(String.valueOf(Integer.parseInt(counter.getText()) + 1));
            LoggerFactory.getLogger(this.getClass()).info("Unload event; attempt? {}; captured in {} and UnloadObserver is inside {}", event.isBecauseOfQuerying(), this.getClass().getSimpleName(), event.getSource().getParent().orElse(new Span()).getClass().getSimpleName());
        });

        callback.accept(new Component[]{query, description, new HorizontalLayout(counterText, counter)});
    }
}
