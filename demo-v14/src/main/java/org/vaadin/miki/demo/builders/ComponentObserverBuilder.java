package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.lazyload.ComponentObserver;

import java.util.function.Consumer;

/**
 * Builds content for {@link ComponentObserver}.
 * @author miki
 * @since 2020-11-19
 */
@Order(100)
public class ComponentObserverBuilder implements ContentBuilder<ComponentObserver> {

    @Override
    public void buildContent(ComponentObserver component, Consumer<Component[]> callback) {
        for(String s: new String[]{"span-one", "span-two", "span-three"}) {
            Span span = new Span("This text is observed by the intersection observer. Resize the window to make it disappear and see what happens. It has id of "+s+". ");
            span.setId(s);
            component.observe(span);
            callback.accept(new Component[]{span});
        }
        component.addComponentObservationListener(event -> {
            if(event.isFullyVisible()) {
                Notification.show("Component with id " + event.getObservedComponent().getId().orElse("(no id)") + " is now fully visible.");
                if(event.getObservedComponent().getId().orElse("").equals("span-two")) {
                    event.getSource().unobserve(event.getObservedComponent());
                    Notification.show("Component with id span-two has been unobserved.");
                }
            }
            else if(event.isNotVisible())
                Notification.show("Component with id "+event.getObservedComponent().getId().orElse("(no id)")+" is now not visible.");
        });
    }
}
