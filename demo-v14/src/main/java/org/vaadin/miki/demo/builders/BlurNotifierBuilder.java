package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import org.vaadin.miki.demo.ContentBuilder;

import java.util.function.Consumer;

/**
 * Builds content for {@link BlurNotifier}.
 * @author miki
 * @since 2020-11-19
 */
public class BlurNotifierBuilder implements ContentBuilder<BlurNotifier<?>> {

    @Override
    public void buildContent(BlurNotifier<?> component, Consumer<Component[]> callback) {
        component.addBlurListener(event ->
                Notification.show(String.format(NotificationConstants.BLUR_MESSAGE, component.getClass().getSimpleName()), NotificationConstants.NOTIFICATION_TIME, Notification.Position.BOTTOM_END)
        );
        callback.accept(new Component[]{new Span("Leave the demo component to see a notification.")});
    }
}
