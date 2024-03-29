package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.FocusNotifier;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;

import java.util.function.Consumer;

/**
 * Builds content for {@link FocusNotifier}.
 * @author miki
 * @since 2020-11-19
 */
@Order(120)
public class FocusNotifierBuilder implements ContentBuilder<FocusNotifier<?>> {

    @Override
    public void buildContent(FocusNotifier<?> component, Consumer<Component[]> callback) {
        component.addFocusListener(event ->
                Notification.show(String.format(NotificationConstants.FOCUS_MESSAGE, component.getClass().getSimpleName()), NotificationConstants.NOTIFICATION_TIME, Notification.Position.BOTTOM_END)
        );
        if(component instanceof Focusable) {
            final Button focus = new Button("Focus component", event -> ((Focusable<?>) component).focus());
            callback.accept(new Component[]{focus, new Span("(not all components can be focused, despite being Focusable)")});
        }
        callback.accept(new Component[]{new Span("Focus the demoed component to see a notification.")});
    }
}
