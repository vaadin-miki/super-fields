package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.shared.HasPrefix;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;

import java.util.function.Consumer;

@Order(112)
public class HasPrefixBuilder implements ContentBuilder<HasPrefix> {
    @Override
    public void buildContent(HasPrefix component, Consumer<Component[]> callback) {
        final ComboBox<String> prefixes = new ComboBox<>("Select a prefix text:", "(none)", "X", "(?)");
        prefixes.addValueChangeListener(event -> component.setPrefixComponent(event.getValue() == null || event.getValue().length() > 5 ? null : new Div(new Text(event.getValue()))));
        callback.accept(new Component[]{prefixes});
    }
}
