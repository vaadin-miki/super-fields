package org.vaadin.miki.superfields.collections;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.function.SerializableSupplier;
import org.vaadin.miki.superfields.util.CollectionComponentProviders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author miki
 * @since 2022-04-08
 */
public class MapField<K, V> extends CustomField<Map<K, V>> {

    private final SerializableSupplier<Map<K, V>> emptyMapSupplier;

    private final CollectionField<Map.Entry<K, V>, List<Map.Entry<K, V>>> collectionField;

    @SuppressWarnings("squid:S2293") // with method references it is impossible to use <>, as the compiler complains
    public <KC extends Component & HasValue<?, K>, VC extends Component & HasValue<?, V>> MapField(SerializableSupplier<Map<K, V>> emptyMapSupplier, SerializableSupplier<KC> keyComponentSupplier, SerializableSupplier<VC> valueComponentSupplier) {
        super(emptyMapSupplier.get());
        this.emptyMapSupplier = emptyMapSupplier;

        this.collectionField = new CollectionField<>(ArrayList::new,
                CollectionComponentProviders.columnWithHeaderAndFooterRows(
                        Arrays.asList(
                                CollectionComponentProviders.removeAllButton("Clear"),
                                CollectionComponentProviders.addFirstButton("Add as first")
                        ),
                        Collections.singletonList(CollectionComponentProviders.addLastButton("Add as last"))),
                CollectionComponentProviders.rowWithRemoveButtonFirst((i, c) -> new MapEntryField<>(
                        keyComponentSupplier, valueComponentSupplier
                ), "Remove")
        );
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

}
