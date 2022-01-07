package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;

import java.util.function.Consumer;

/**
 * Builds content for anything that implements {@link HasValueChangeMode}.
 *
 * @author miki
 * @since 2022-01-07
 */
@Order(72)
public class HasValueChangeModeBuilder implements ContentBuilder<HasValueChangeMode> {
    @Override
    public void buildContent(HasValueChangeMode component, Consumer<Component[]> callback) {
        final ComboBox<ValueChangeMode> options = new ComboBox<>("Value change mode", ValueChangeMode.values());
        options.addValueChangeListener(event -> component.setValueChangeMode(event.getValue()));
        callback.accept(new Component[]{options});
    }
}
