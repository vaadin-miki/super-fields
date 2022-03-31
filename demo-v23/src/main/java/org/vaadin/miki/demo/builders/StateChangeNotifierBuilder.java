package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.events.state.StateChangeNotifier;

import java.util.function.Consumer;

/**
 * Provides content for {@link StateChangeNotifier}.
 * @author miki
 * @since 2020-11-19
 */
@Order(140)
public class StateChangeNotifierBuilder implements ContentBuilder<StateChangeNotifier<?, ?>> {

    @Override
    public void buildContent(StateChangeNotifier<?, ?> component, Consumer<Component[]> callback) {
        component.addStateChangeListener(event ->
                Notification.show(String.format(NotificationConstants.STATE_MESSAGE, component.getClass().getSimpleName()), NotificationConstants.NOTIFICATION_TIME, Notification.Position.BOTTOM_END)
        );
        callback.accept(new Component[]{new Span("Notifications will be shown when this component changes its state for any reason.")});
    }
}
