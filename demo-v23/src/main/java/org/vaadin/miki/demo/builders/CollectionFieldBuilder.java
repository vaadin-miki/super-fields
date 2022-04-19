package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.collections.CollectionField;
import org.vaadin.miki.superfields.collections.CollectionValueComponentProvider;
import org.vaadin.miki.superfields.buttons.IndexedButton;
import org.vaadin.miki.superfields.text.SuperTextField;
import org.vaadin.miki.superfields.util.CollectionComponentProviders;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Builds content for {@link CollectionField}.
 * @author miki
 * @since 2021-09-10
 */
@Order(5)
public class CollectionFieldBuilder implements ContentBuilder<CollectionField<String, List<String>>> {

    @Override
    public void buildContent(CollectionField<String, List<String>> component, Consumer<Component[]> callback) {
        final Map<CollectionValueComponentProvider<String, ?>, String> providersWithCaptions = new LinkedHashMap<>();
        // three options available
        final CollectionValueComponentProvider<String, SuperTextField> textFields = CollectionComponentProviders::textField;
        providersWithCaptions.put(textFields, "Just text fields");
        providersWithCaptions.put(CollectionComponentProviders.rowWithRemoveButtonFirst(
                CollectionComponentProviders::textField, "Remove"
        ), "Text field with remove button");
        providersWithCaptions.put(CollectionComponentProviders.columnWithHeaderAndFooterRows(
                CollectionComponentProviders::textField,
                Collections.emptyList(),
                Arrays.asList(
                        CollectionComponentProviders.removeButton("Remove"),
                        (index, controller) -> new IndexedButton("Add below", event -> controller.add(index+1))
                )
        ), "Text field with remove and add below buttons");

        // configure the combo box
        final ComboBox<CollectionValueComponentProvider<String, ?>> box = new ComboBox<>("Choose item layout:", providersWithCaptions.keySet());
        box.setItemLabelGenerator(providersWithCaptions::get);
        box.addValueChangeListener(event -> component.setCollectionValueComponentProvider(event.getValue()));
        box.setAllowCustomValue(false);
        box.setValue(textFields);

        callback.accept(new Component[]{box});
    }
}
