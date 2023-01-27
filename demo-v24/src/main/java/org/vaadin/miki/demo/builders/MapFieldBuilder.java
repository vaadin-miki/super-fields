package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.collections.CollectionValueComponentProvider;
import org.vaadin.miki.superfields.collections.MapEntryField;
import org.vaadin.miki.superfields.collections.MapField;
import org.vaadin.miki.superfields.numbers.SuperIntegerField;
import org.vaadin.miki.superfields.text.SuperTextField;
import org.vaadin.miki.superfields.util.CollectionComponentProviders;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Builds content for {@link MapField}.
 *
 * @author miki
 * @since 2022-04-12
 */
@Order(5)
public class MapFieldBuilder implements ContentBuilder<MapField<String, Integer>> {
    @Override
    public void buildContent(MapField<String, Integer> component, Consumer<Component[]> callback) {
        final Map<CollectionValueComponentProvider<Map.Entry<String, Integer>, ?>, String> providersWithCaptions = new LinkedHashMap<>();

        final CollectionValueComponentProvider<Map.Entry<String, Integer>, MapEntryField<String, Integer>> fieldsOnly = CollectionComponentProviders.mapEntryField(SuperTextField::new, SuperIntegerField::new);
        providersWithCaptions.put(fieldsOnly, "Unlabelled text and integer fields");
        providersWithCaptions.put(CollectionComponentProviders.rowWithRemoveButtonFirst(
                CollectionComponentProviders.mapEntryField("Key", SuperTextField::new, "Value", SuperIntegerField::new)
                , "Remove"
        ), "Remove button and labelled text and integer fields");

        // configure the combo box
        final ComboBox<CollectionValueComponentProvider<Map.Entry<String, Integer>, ?>> box = new ComboBox<>("Choose item layout:", providersWithCaptions.keySet());
        box.setItemLabelGenerator(providersWithCaptions::get);
        box.addValueChangeListener(event -> component.setCollectionValueComponentProvider(event.getValue()));
        box.setAllowCustomValue(false);
        box.setValue(fieldsOnly);

        callback.accept(new Component[]{box});

    }
}
