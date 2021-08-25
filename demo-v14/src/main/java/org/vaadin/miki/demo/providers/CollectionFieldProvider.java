package org.vaadin.miki.demo.providers;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.collections.CollectionComponentProvider;
import org.vaadin.miki.superfields.collections.CollectionField;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a {@link CollectionField} for {@code List<String>}s.
 * @author miki
 * @since 2021-08-25
 */
@Order(145)
public class CollectionFieldProvider implements ComponentProvider<CollectionField<String, List<String>>>, Validator<List<String>> {
    @Override
    public CollectionField<String, List<String>> getComponent() {
        return new CollectionField<>(
                ArrayList::new,
                controller -> {
                    final VerticalLayout layout = new VerticalLayout();
                    layout.add(new HorizontalLayout(
                            new Button("Clear", buttonClickEvent -> controller.removeAll()),
                            new Button("Add", buttonClickEvent -> controller.add())
                    ));
                    return layout;
                },
                (CollectionComponentProvider<String, TextField>) (index, controller) -> new TextField(String.format("value at index %02d", index))
        );
    }

    @Override
    public ValidationResult apply(List<String> strings, ValueContext valueContext) {
        return strings.size() != 3 ? ValidationResult.error("the list must have exactly 3 elements") : ValidationResult.ok();
    }
}
