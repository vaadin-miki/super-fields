package org.vaadin.miki.superfields.collections;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.function.SerializableSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Basic field to support values that are {@link Map}s.
 *
 * This basically forwards everything to a {@link CollectionField} and wraps the value in a {@link Map}.
 *
 * @author miki
 * @since 2022-04-08
 */
public class MapField<K, V> extends CustomField<Map<K, V>> {

    private final SerializableSupplier<Map<K, V>> emptyMapSupplier;

    private final CollectionField<Map.Entry<K, V>, List<Map.Entry<K, V>>> collectionField;

    public MapField(SerializableSupplier<Map<K, V>> emptyMapSupplier, CollectionLayoutProvider<?> mainLayoutProvider, CollectionValueComponentProvider<Map.Entry<K, V>, ?> entryValueComponentProvider) {
        super(emptyMapSupplier.get());
        this.emptyMapSupplier = emptyMapSupplier;

        this.collectionField = new CollectionField<>(ArrayList::new, mainLayoutProvider, entryValueComponentProvider);
        this.collectionField.addClassName("map-field-entry-collection");
        this.collectionField.addValueChangeListener(event -> this.updateValue());

        this.add(this.collectionField);
    }

    @Override
    protected Map<K, V> generateModelValue() {
        final Map<K, V> result = this.emptyMapSupplier.get();
        this.collectionField.getValue().stream()
                .filter(Objects::nonNull)
                .forEach(entry -> result.put(entry.getKey(), entry.getValue()));
        return result;
    }

    @Override
    protected void setPresentationValue(Map<K, V> map) {
        this.collectionField.setValue(new ArrayList<>(map.entrySet()));
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        this.collectionField.setReadOnly(readOnly);
    }

    @Override
    public void focus() {
        this.collectionField.focus();
    }
}
