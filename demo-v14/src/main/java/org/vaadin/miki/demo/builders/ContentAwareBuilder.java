package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.contentaware.ContentAware;
import org.vaadin.miki.superfields.text.SuperTextField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Consumer;

@Order(165)
public class ContentAwareBuilder implements ContentBuilder<ContentAware> {

    @Override
    public void buildContent(ContentAware component, Consumer<Component[]> callback) {
        component.addContentChangeListener(event -> Notification.show(String.format("ContentAware has changed, %d nodes were added and %d nodes were removed", event.getNumberOfAddedNodes(), event.getNumberOfRemovedNodes())));

        final Checkbox active = new Checkbox("Active?", event -> component.setActive(event.getValue()));

        final Button addText = new Button("Add text", event -> component.add(new Text("epoch time is "+ LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))));
        final Button addTextField = new Button("Add text field", event -> component.add(new SuperTextField("dynamically added", "(nothing here, carry on)")));
        final Button addDivAndText = new Button("Add text and div", event -> component.add(new Div(new Text("epoch day is "+ LocalDate.now().toEpochDay()))));
        final Button removeRandom = new Button("Remove random", event -> component.getChildren().findAny().ifPresent(component::remove));

        callback.accept(new Component[]{active, new HorizontalLayout(addText, addTextField, addDivAndText, removeRandom)});
    }
}
