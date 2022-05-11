package org.vaadin.miki.superfields.collections;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.shared.Registration;
import org.vaadin.miki.markers.HasIndex;
import org.vaadin.miki.markers.HasReadOnly;
import org.vaadin.miki.markers.WithHelperMixin;
import org.vaadin.miki.markers.WithHelperPositionableMixin;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithValueMixin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * All-purpose field for editing and maintaining values that are {@link Collection}s.
 * Allows filtering individual elements through {@link #setCollectionElementFilter(SerializablePredicate)} (by default filters out all {@code null}s).
 *
 * @param <T> Type of the element in the collection.
 * @param <C> Type of the collection.
 */
public class CollectionField<T, C extends Collection<T>> extends CustomField<C>
        implements CollectionController, WithIdMixin<CollectionField<T, C>>, HasStyle,
        WithCollectionValueComponentProviderMixin<T, CollectionField<T, C>>,
        WithHelperMixin<CollectionField<T, C>>, WithHelperPositionableMixin<CollectionField<T, C>>,
        WithValueMixin<AbstractField.ComponentValueChangeEvent<CustomField<C>, C>, C, CollectionField<T, C>>,
        WithCollectionElementFilterMixin<T, CollectionField<T, C>> {

    /**
     * CSS class name that will be added to the main layout of this component.
     */
    public static final String LAYOUT_STYLE_NAME = "collection-field-main-layout";

    /**
     * Integer value indicating no index being passed.
     */
    public static final int NO_ITEM_INDEX = -1;

    /**
     * Default layout provider. Produces a column-based {@link FlexLayout}.
     */
    public static final CollectionLayoutProvider<FlexLayout> DEFAULT_LAYOUT_PROVIDER = ((index, controller) -> {
        final FlexLayout result = new FlexLayout();
        result.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        return result;
    });

    private final List<HasValue<?, T>> fields = new ArrayList<>();

    private final SerializableSupplier<C> emptyCollectionSupplier;

    private final Map<Component, Registration> eventRegistrations = new HashMap<>();

    private HasComponents layout;

    private CollectionValueComponentProvider<T, ?> collectionValueComponentProvider;

    private boolean valueUpdateInProgress = false;

    private SerializablePredicate<T> collectionElementFilter = Objects::nonNull;

    /**
     * Creates new field using {@link #DEFAULT_LAYOUT_PROVIDER} as root layout provider.
     * @param emptyCollectionSupplier Provides an empty collection of elements.
     * @param fieldSupplier Method to call when a component for an element is needed.
     * @param <F> Type of the field used.
     */
    public <F extends Component & HasValue<?, T>> CollectionField(SerializableSupplier<C> emptyCollectionSupplier, SerializableSupplier<F> fieldSupplier) {
        this(emptyCollectionSupplier, DEFAULT_LAYOUT_PROVIDER, (CollectionValueComponentProvider<T, F>) (index, controller) -> fieldSupplier.get());
    }

    /**
     * Creates new field using {@link #DEFAULT_LAYOUT_PROVIDER} as root layout provider.
     * @param emptyCollectionSupplier Provides an empty collection of elements.
     * @param collectionValueComponentProvider Provider for components for each element in the component.
     */
    public CollectionField(SerializableSupplier<C> emptyCollectionSupplier, CollectionValueComponentProvider<T, ?> collectionValueComponentProvider) {
        this(emptyCollectionSupplier, DEFAULT_LAYOUT_PROVIDER, collectionValueComponentProvider);
    }

        /**
         * Creates new field.
         * @param emptyCollectionSupplier Provides an empty collection of elements.
         * @param collectionLayoutProvider Source of root layout for this component.
         * @param fieldSupplier Method to call when a component for an element is needed.
         * @param <F> Type of the field used.
         */
    public <F extends Component & HasValue<?, T>> CollectionField(SerializableSupplier<C> emptyCollectionSupplier, CollectionLayoutProvider<?> collectionLayoutProvider, SerializableSupplier<F> fieldSupplier) {
        this(emptyCollectionSupplier, collectionLayoutProvider, (CollectionValueComponentProvider<T, F>) (index, controller) -> fieldSupplier.get());
    }

    /**
     * Creates new field.
     * @param emptyCollectionSupplier Provides an empty collection of elements.
     * @param collectionLayoutProvider Source of root layout for this component.
     * @param collectionValueComponentProvider Provider for components for each element in the component.
     */
    public CollectionField(SerializableSupplier<C> emptyCollectionSupplier, CollectionLayoutProvider<?> collectionLayoutProvider, CollectionValueComponentProvider<T, ?> collectionValueComponentProvider) {
        // default value is empty collection
        super(emptyCollectionSupplier.get());

        this.emptyCollectionSupplier = emptyCollectionSupplier;
        this.updateLayout(collectionLayoutProvider.provideComponent(NO_ITEM_INDEX, this));

        this.setCollectionValueComponentProvider(collectionValueComponentProvider);
    }

    private <L extends Component & HasComponents> void updateLayout(L newLayout) {
        if(this.layout != null)
            this.remove((Component) this.layout);
        this.layout = newLayout;
        this.add(newLayout);
        if(this.layout instanceof HasStyle)
            ((HasStyle) this.layout).addClassName(LAYOUT_STYLE_NAME);
    }

    /**
     * Sets new layout from a given provider and repaints all fields.
     * @param collectionLayoutProvider A provider to use. Defaults to {@link #DEFAULT_LAYOUT_PROVIDER}.
     */
    public final void setCollectionLayoutProvider(CollectionLayoutProvider<?> collectionLayoutProvider) {
        this.updateLayout((collectionLayoutProvider == null ? DEFAULT_LAYOUT_PROVIDER : collectionLayoutProvider).provideComponent(NO_ITEM_INDEX, this));
        this.repaintFields(this.getValue());
    }

    /**
     * Chains {@link #setCollectionLayoutProvider(CollectionLayoutProvider)} and returns itself.
     * @param collectionLayoutProvider A provider to use. Defaults to {@link #DEFAULT_LAYOUT_PROVIDER}.
     * @return This.
     * @see #setCollectionLayoutProvider(CollectionLayoutProvider)
     */
    public final CollectionField<T, C> withCollectionLayoutProvider(CollectionLayoutProvider<?> collectionLayoutProvider) {
        this.setCollectionLayoutProvider(collectionLayoutProvider);
        return this;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        // if the layout itself can be in read-only, just call the layout
        if(this.layout instanceof HasReadOnly)
            ((HasReadOnly) this.layout).setReadOnly(readOnly);
        // otherwise, figure out which components can be read-only and set those
        else ((Component)this.layout).getChildren()
                .forEach(component -> HasReadOnly.setReadOnly(readOnly, component));
        // finally, pass that to every field
        this.fields.forEach(field -> field.setReadOnly(readOnly));
    }

    @Override
    protected C generateModelValue() {
        return this.fields.stream()
                .map(HasValue::getValue)
                .filter(this.getCollectionElementFilter()) // fixes #374
                .collect(Collectors.toCollection(this.emptyCollectionSupplier));
    }

    @Override
    protected void updateValue() {
        if(!this.valueUpdateInProgress) {
            super.updateValue();
            // when using sets, it is possible for duplicates to appear, and they should not be possible
            if (this.fields.size() != super.getValue().size())
                this.repaintFields(super.getValue());
        }
    }

    @Override
    protected void setPresentationValue(C ts) {
        if(ts == null)
            ts = this.emptyCollectionSupplier.get();
        this.repaintFields(ts);
    }

    @Override
    public int size() {
        return this.fields.size();
    }

    /**
     * Updates all indices on all fields that implement {@link HasIndex}.
     * This method is called after a component is removed or added, but before the value of the component is changed.
     * @param fromIndex Index, from which the reindexing should occur.
     */
    protected void updateIndices(int fromIndex) {
        // update index of each component that has it
        for(int zmp1 = fromIndex; zmp1 < this.fields.size(); zmp1++)
            if(this.fields.get(zmp1) instanceof HasIndex)
                ((HasIndex) this.fields.get(zmp1)).setIndex(zmp1);
    }

    @Override
    public void add(int atIndex) {
        final HasValue<?, T> hasValue = this.getCollectionValueComponentProvider().provideComponent(atIndex, this);
        // make sure this component is updated whenever anything changes for single element
        this.eventRegistrations.put((Component) hasValue, hasValue.addValueChangeListener(this::valueChangedInSubComponent));
        this.fields.add(atIndex, hasValue);
        this.layout.addComponentAtIndex(atIndex, (Component) hasValue);
        this.updateIndices(atIndex);
        this.updateValue();
    }

    @Override
    public void remove(int atIndex) {
        final Component removed = (Component) this.fields.remove(atIndex);
        this.layout.remove(removed);
        this.eventRegistrations.remove(removed).remove(); // brilliant line of code
        this.updateIndices(atIndex);
        this.updateValue();
    }

    @Override
    public void removeAll() {
        this.clear();
    }

    /**
     * Paints all fields that need repainting and assigns values from the collection to each of them.
     * @param ts Collection with elements.
     */
    protected void repaintFields(C ts) {
        this.valueUpdateInProgress = true;
        // if there are more fields than elements, remove the excess ones
        while(this.fields.size() > ts.size())
            this.remove();
        // if there are more elements than fields, create additional ones
        while(this.fields.size() < ts.size())
            this.add();
        // now, both collections are of the same size, so update all components to show elements in collection
        int zmp1 = 0;
        for(T t: ts)
            this.fields.get(zmp1++).setValue(t);
        this.valueUpdateInProgress = false;
        this.updateValue();
    }

    private void valueChangedInSubComponent(ValueChangeEvent<T> o) {
        if(!this.valueUpdateInProgress)
            this.updateValue();
    }

    @Override
    public final void setCollectionValueComponentProvider(CollectionValueComponentProvider<T, ?> collectionValueComponentProvider) {
        this.collectionValueComponentProvider = collectionValueComponentProvider;
        this.valueUpdateInProgress = true; // setting it here
        this.layout.remove(this.fields.stream().map(Component.class::cast).toArray(Component[]::new));
        this.fields.clear();
        this.repaintFields(this.getValue());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <W extends Component & HasValue<?, T>> CollectionValueComponentProvider<T, W> getCollectionValueComponentProvider() {
        return (CollectionValueComponentProvider<T, W>) collectionValueComponentProvider;
    }

    @Override
    public void setCollectionElementFilter(SerializablePredicate<T> collectionElementFilter) {
        this.collectionElementFilter = collectionElementFilter == null ? t -> true : collectionElementFilter;
    }

    @Override
    public SerializablePredicate<T> getCollectionElementFilter() {
        return collectionElementFilter;
    }

    @Override
    public void focus() {
        if(!this.fields.isEmpty() && this.fields.get(0) instanceof Focusable<?>)
            ((Focusable<?>) this.fields.get(0)).focus(); // fixes #360 by forwarding focusing to the first component in the list if available
        super.focus();
    }

    /**
     * Gets the component at specified position. No range checking.
     * For testing purposes only.
     * @param index Index to get a component at.
     * @return A non-{@code null} component at a given index.
     */
    @SuppressWarnings("squid:S1452") // only the type is relevant here, could not find a way to narrow down the event type
    final HasValue<?, T> getField(int index) {
        return this.fields.get(index);
    }

}
