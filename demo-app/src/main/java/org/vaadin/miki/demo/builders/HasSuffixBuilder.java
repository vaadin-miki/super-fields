package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.shared.HasSuffix;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;

import java.util.function.Consumer;

@Order(113)
public class HasSuffixBuilder implements ContentBuilder<HasSuffix> {
    @Override
    public void buildContent(HasSuffix component, Consumer<Component[]> callback) {
        final ComboBox<String> suffixes = new ComboBox<>("Select a suffix button:", "(none)", "Click me!");
        suffixes.addValueChangeListener(event -> component.setSuffixComponent(event.getValue() == null || event.getValue().charAt(0) == '(' ? null : new Button(event.getValue(), buttonClickEvent -> Notification.show("Congratulations, you just clicked a button!"))));
        callback.accept(new Component[]{suffixes});
    }
}
