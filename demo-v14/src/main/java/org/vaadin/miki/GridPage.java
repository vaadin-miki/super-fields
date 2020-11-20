package org.vaadin.miki;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import org.vaadin.miki.superfields.numbers.SuperBigDecimalField;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Route(value = "grid")
public class GridPage extends Div {

    public static final class NotificationEvent {
        private BigDecimal limit;
        private String eventType = "";

        public BigDecimal getLimit() {
            return limit;
        }

        public void setLimit(BigDecimal limit) {
            this.limit = limit;
        }

        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }
    }

    public GridPage() {
        final NotificationEvent notificationEvent = new NotificationEvent();
        notificationEvent.setEventType("test");
        notificationEvent.setLimit(BigDecimal.TEN);
        final ArrayList<NotificationEvent> events = new ArrayList<>();
        events.add(notificationEvent);

        final Map<NotificationEvent, Binder<NotificationEvent>> eventBinders = new HashMap<>();
        events.forEach(event -> eventBinders.put(event, new Binder<>(NotificationEvent.class)));
        Grid<NotificationEvent> grid = new Grid<>(NotificationEvent.class, false);
        grid.addColumn(NotificationEvent::getEventType).setHeader("Event type");
        grid.addColumn(new ComponentRenderer<>(event -> {
            SuperBigDecimalField limitTextField = new SuperBigDecimalField(event.getLimit(), null, Locale.UK, 0);
            limitTextField.withAutoselect(true)
                    .withGroupingSeparatorHiddenOnFocus(true)
                    .withNullValueAllowed(true)
                    .withReceivingSelectionEventsFromClient(true)
                    .setDecimalFormat(new DecimalFormat("#,##0.##", new DecimalFormatSymbols(Locale.UK)));
            eventBinders.get(event).forField(limitTextField)
                    .withValidator(value -> value == null || BigDecimal.valueOf(0).compareTo(value) <= 0, "Limit has to be 0 or bigger")
                    .bind(NotificationEvent::getLimit, NotificationEvent::setLimit);
            limitTextField.addTextSelectionListener(e -> Notification.show("Selected text: "+e.getSelectedText()));
            return limitTextField;
        })).setHeader("Limit");

        grid.setItems(events);

        this.add(grid);
    }
}
