package org.vaadin.miki.superfields.collections;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * All-purpose Vaadin for editing and maintaining values that are {@link Collection}s.
 *
 * @param <T> Type of the element in the collection.
 * @param <C> Type of the collection.
 */
public class CollectionField<T, C extends Collection<T>> extends CustomField<C>
        implements CollectionController {

    /**
     * CSS class name that will be added to the main layout of this component.
     */
    public static final String LAYOUT_STYLE_NAME = "collection-field-main-layout";

    private final List<HasValue<?, T>> fields = new ArrayList<>();

    private final SerializableSupplier<C> emptyCollectionSupplier;

    private final Map<Component, Registration> eventRegistrations = new HashMap<>();

    private final HasComponents layout;

    private ComponentProvider<T, ?> componentProvider;

    private boolean valueUpdateInProgress = false;

    /**
     * Creates new field.
     * @param emptyCollectionSupplier Provides an empty collection of elements.
     * @param layoutProvider Source of root layout for this component.
     */
    public CollectionField(SerializableSupplier<C> emptyCollectionSupplier, LayoutProvider<?> layoutProvider) {
        // default value is empty collection
        super(emptyCollectionSupplier.get());

        this.emptyCollectionSupplier = emptyCollectionSupplier;

        this.layout = layoutProvider.provideLayout(this);
        this.add((Component) this.layout);
        if(this.layout instanceof HasStyle)
            ((HasStyle) this.layout).addClassName(LAYOUT_STYLE_NAME);
    }

    @Override
    protected C generateModelValue() {
        return this.fields.stream().map(HasValue::getValue).collect(Collectors.toCollection(this.emptyCollectionSupplier));
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

    protected void updateIndices(int fromIndex) {
        // update index of each component that has it
        for(int zmp1 = fromIndex; zmp1 < this.fields.size(); zmp1++)
            if(this.fields.get(zmp1) instanceof HasIndex)
                ((HasIndex) this.fields.get(zmp1)).setIndex(zmp1);
    }

    @Override
    public void add(int atIndex) {
        final HasValue<?, T> hasValue = this.getComponentProvider().provideComponent(atIndex, this);
        // make sure this component is updated whenever anything changes for single element
        this.eventRegistrations.put((Component) hasValue, hasValue.addValueChangeListener(this::valueChangedInSubComponent));
        this.fields.add(atIndex, hasValue);
        this.layout.addComponentAtIndex(atIndex, (Component) hasValue);
        this.updateIndices(atIndex);
    }

    @Override
    public void remove(int atIndex) {
        final Component removed = (Component) this.fields.remove(atIndex);
        this.layout.remove(removed);
        this.eventRegistrations.remove(removed).remove(); // brilliant line of code
        this.updateIndices(atIndex);
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
    }

    private void valueChangedInSubComponent(ValueChangeEvent<T> o) {
        if(!this.valueUpdateInProgress)
            this.updateValue();
    }

    public void setComponentProvider(ComponentProvider<T, ?> componentProvider) {
        this.componentProvider = componentProvider;
        this.fields.clear();
        this.repaintFields(this.getValue());
    }

    public ComponentProvider<T, ?> getComponentProvider() {
        return componentProvider;
    }

    public final CollectionField<T, C> withComponentProvider(ComponentProvider<T, ?> componentProvider) {
        this.setComponentProvider(componentProvider);
        return this;
    }

}
