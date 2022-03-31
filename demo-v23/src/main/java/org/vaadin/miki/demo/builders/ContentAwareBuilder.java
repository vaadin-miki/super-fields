package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
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
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Order(165)
public class ContentAwareBuilder implements ContentBuilder<ContentAware> {

    private static final Random RANDOM = new Random();

    private static Component getRandomChild(HasComponents parent) {
        final List<Component> list = ((Component) parent).getChildren().collect(Collectors.toList());
        return list.get(RANDOM.nextInt(list.size()));
    }

    @Override
    public void buildContent(ContentAware component, Consumer<Component[]> callback) {
        component.addContentChangeListener(event -> Notification.show(String.format("ContentAware has changed, %d nodes were added and %d nodes were removed", event.getNumberOfAddedNodes(), event.getNumberOfRemovedNodes())));

        final Checkbox active = new Checkbox("Active?", event -> component.setActive(event.getValue()));

        final Button addText = new Button("Add text", event -> ((HasComponents)getRandomChild(component)).add(new Text("epoch time is "+ LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))));
        final Button addTextField = new Button("Add text field", event -> ((HasComponents)getRandomChild(component)).add(new SuperTextField("dynamically added", "(nothing here, carry on)")));
        final Button addDivAndText = new Button("Add text and div", event -> ((HasComponents)getRandomChild(component)).add(new Div(new Text("epoch day is "+ LocalDate.now().toEpochDay()))));
        final Button removeRandom = new Button("Remove random", event -> {
            final HasComponents randomChild = (HasComponents) getRandomChild(component);
            if (!((Component) randomChild).getChildren().findAny().isPresent())
                Notification.show("Randomly selected empty layout; nothing to remove. Please try again.", NotificationConstants.NOTIFICATION_TIME, Notification.Position.BOTTOM_END);
            else
                randomChild.remove(getRandomChild(randomChild));
        });

        callback.accept(new Component[]{active, new HorizontalLayout(addText, addTextField, addDivAndText, removeRandom)});
    }
}
