package org.vaadin.miki.superfields.collections;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.function.SerializableSupplier;
import org.vaadin.miki.markers.WithHelperMixin;
import org.vaadin.miki.markers.WithHelperPositionableMixin;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithLabelPositionableMixin;
import org.vaadin.miki.markers.WithValueMixin;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Basic field to support values that are {@link Map}s.
 * This basically forwards everything to a {@link CollectionField} and wraps the value in a {@link Map}. It also expects
 * a component capable of displaying values of type {@link Map.Entry}.
 *
 * @see MapEntryField
 * @see CollectionField
 *
 * @author miki
 * @since 2022-04-08
 */
@CssImport(value = "./styles/label-positions.css", themeFor = "map-field")
@Tag("map-field")
@JsModule("./map-field.js")
public class MapField<K, V> extends CustomField<Map<K, V>> implements HasStyle,
        WithIdMixin<MapField<K, V>>, WithValueMixin<AbstractField.ComponentValueChangeEvent<CustomField<Map<K, V>>, Map<K, V>>, Map<K, V>, MapField<K, V>>,
        WithHelperPositionableMixin<MapField<K, V>>, WithHelperMixin<MapField<K, V>>,
        WithLabelMixin<MapField<K, V>>, WithCollectionValueComponentProviderMixin<Map.Entry<K, V>, MapField<K, V>>,
        WithCollectionElementFilterMixin<Map.Entry<K, V>, MapField<K, V>>,
        WithLabelPositionableMixin<MapField<K, V>> {

    /**
     * Produces a non-null entry filter (entry, its key and its value must be non-{@code null}).
     * @param <X> Key type.
     * @param <Y> Value type.
     * @return Predicate.
     */
    public static <X, Y> SerializablePredicate<Map.Entry<X, Y>> nonNullEntryFilter() {
        return e -> e != null && e.getKey() != null && e.getValue() != null;
    }

    private final SerializableSupplier<Map<K, V>> emptyMapSupplier;

    private final CollectionField<Map.Entry<K, V>, List<Map.Entry<K, V>>> collectionField;

    private SerializablePredicate<Map.Entry<K, V>> entryFilter = nonNullEntryFilter();

    /**
     * Creates a new {@link MapField} with given empty map supplier, layout provider and entry component provider.
     * @param emptyMapSupplier A supplier of empty maps.
     * @param layoutProvider Provider of the main layout of the component.
     * @param entryComponentProvider Provides component to display entries of the map.
     * @param <L> Ensures layout provider produces a {@link Component} that {@link HasComponents}.
     * @param <C> Ensures entry component provider produces a {@link Component} that {@link HasValue} of type {@link Map.Entry}{@code <K, V>}.
     * @see MapEntryField
     */
    public <L extends Component & HasComponents, C extends Component & HasValue<?, Map.Entry<K, V>>> MapField(SerializableSupplier<Map<K, V>> emptyMapSupplier, Supplier<L> layoutProvider, SerializableSupplier<C> entryComponentProvider) {
        this(emptyMapSupplier, (index, controller) -> layoutProvider.get(), (CollectionValueComponentProvider<Map.Entry<K,V>, C>) (index, controller) -> entryComponentProvider.get());
    }

    /**
     * Creates a new {@link MapField} with given layout provider and entry component provider. The produced map will be a {@link LinkedHashMap}.
     * @param layoutProvider Provider of the main layout of the component.
     * @param entryComponentProvider Provides component to display entries of the map.
     * @param <L> Ensures layout provider produces a {@link Component} that {@link HasComponents}.
     * @param <C> Ensures entry component provider produces a {@link Component} that {@link HasValue} of type {@link Map.Entry}{@code <K, V>}.
     * @see MapEntryField
     */
    public <L extends Component & HasComponents, C extends Component & HasValue<?, Map.Entry<K, V>>> MapField(Supplier<L> layoutProvider, SerializableSupplier<C> entryComponentProvider) {
        this(LinkedHashMap::new, layoutProvider, entryComponentProvider);
    }

    /**
     * Creates a new {@link MapField} with given entry component provider. Uses {@link FlexLayoutHelpers#column()} as layout provider. The produced map will be a {@link LinkedHashMap}.
     * @param entryComponentProvider Provides component to display entries of the map.
     * @param <C> Ensures entry component provider produces a {@link Component} that {@link HasValue} of type {@link Map.Entry}{@code <K, V>}.
     */
    public <C extends Component & HasValue<?, Map.Entry<K, V>>> MapField(SerializableSupplier<C> entryComponentProvider) {
        this(FlexLayoutHelpers::column, entryComponentProvider);
    }

    /**
     * Creates a new {@link MapField} using given empty map provider, and layout and entry component providers for the underlying {@link CollectionField}.
     * @param emptyMapSupplier A supplier of empty maps.
     * @param mainLayoutProvider Provider of the main layout for the underlying {@link CollectionField}.
     * @param entryValueComponentProvider Provider of entry components displayed in the underlying {@link CollectionField}.
     */
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
                .filter(this.getCollectionElementFilter())
                .forEach(entry -> result.put(entry.getKey(), entry.getValue()));
        return result;
    }

    @Override
    protected void setPresentationValue(Map<K, V> map) {
        this.collectionField.setValue(map == null ? Collections.emptyList() : new ArrayList<>(map.entrySet()));
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

    @Override
    public void setCollectionValueComponentProvider(CollectionValueComponentProvider<Map.Entry<K, V>, ?> provider) {
        this.collectionField.setCollectionValueComponentProvider(provider);
    }

    @Override
    public <W extends Component & HasValue<?, Map.Entry<K, V>>> CollectionValueComponentProvider<Map.Entry<K, V>, W> getCollectionValueComponentProvider() {
        return this.collectionField.getCollectionValueComponentProvider();
    }

    @Override
    public void setCollectionElementFilter(SerializablePredicate<Map.Entry<K, V>> collectionElementFilter) {
        this.entryFilter = collectionElementFilter == null ? e -> true : collectionElementFilter;
    }

    @Override
    public SerializablePredicate<Map.Entry<K, V>> getCollectionElementFilter() {
        return this.entryFilter;
    }
}
