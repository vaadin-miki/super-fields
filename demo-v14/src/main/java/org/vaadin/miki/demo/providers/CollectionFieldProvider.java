package org.vaadin.miki.demo.providers;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.markers.HasIndex;
import org.vaadin.miki.superfields.collections.CollectionComponentProvider;
import org.vaadin.miki.superfields.collections.CollectionField;
import org.vaadin.miki.superfields.collections.IndexedButton;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;
import org.vaadin.miki.superfields.layouts.HeaderFooterFieldWrapper;
import org.vaadin.miki.superfields.layouts.HeaderFooterLayoutWrapper;

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
                controller ->
                    new HeaderFooterLayoutWrapper<>(
                            FlexLayoutHelpers::column,
                            FlexLayoutHelpers.row(),
                            FlexLayoutHelpers.column(),
                            FlexLayoutHelpers.row()
                    ).withHeaderComponents(
                            new Button("Clear", buttonClickEvent -> controller.removeAll()),
                            new Button("Add as first", buttonClickEvent -> controller.add(0))
                    ).withFooterComponents(new Button("Add as last", event -> controller.add()))
                ,
                (CollectionComponentProvider<String, CustomField<String>>) (index, controller) ->
                        new HeaderFooterFieldWrapper<>(
                                FlexLayoutHelpers::row,
                                FlexLayoutHelpers.column(),
                                new TextField("value"),
                                FlexLayoutHelpers.column()
                        ).withHeaderComponents(
                                new IndexedButton("X", index, event -> controller.remove(((HasIndex)event.getSource()).getIndex()))
                        )
        );
    }

    @Override
    public ValidationResult apply(List<String> strings, ValueContext valueContext) {
        return strings.size() != 3 ? ValidationResult.error("the list must have exactly 3 elements") : ValidationResult.ok();
    }
}
