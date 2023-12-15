package org.vaadin.miki.superfields.collections;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.function.SerializableSupplier;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;
import org.vaadin.miki.superfields.text.LabelField;

import java.util.Map;
import java.util.function.Supplier;

/**
 * A field for representing a {@link Map.Entry}. Allows customisation of layout, key component and value component.
 *
 * It uses {@link #DEFAULT_LAYOUT_PROVIDER} and {@link LabelField} by default.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 *
 * @author miki
 * @since 2022-04-08
 */
@Tag("map-entry-field")
@JsModule("./map-entry-field.js")
public class MapEntryField<K, V> extends CustomField<Map.Entry<K, V>> {

    /**
     * Default layout provider used by {@link MapEntryField}.
     */
    public static final SerializableSupplier<FlexLayout> DEFAULT_LAYOUT_PROVIDER = FlexLayoutHelpers::row;

    private SerializableSupplier<HasValue<?, K>> keyComponentSupplier;
    private SerializableSupplier<HasValue<?, V>> valueComponentSupplier;
    private SerializableSupplier<HasComponents> layoutSupplier;

    private HasComponents layout;
    private HasValue<?, K> keyComponent;
    private HasValue<?, V> valueComponent;

    private boolean valueChangeInProgress = false;

    /**
     * Creates a {@link MapEntryField} with given providers for layout, key component and value component.
     * @param layoutSupplier Supplier of a layout to put key and value components in, in that order.
     * @param keyComponentSupplier Provides a field to display a key of the entry.
     * @param valueComponentSupplier Provides a field to display a value of the entry.
     * @param <L> Ensures layout provider is both a {@link Component} and {@link HasComponents}.
     * @param <KC> Ensures key component provider is both a {@link Component} and {@link HasValue} of type {@code K}.
     * @param <VC> Ensures value component provider is both a {@link Component} and {@link HasValue} of type {@code V}.
     */
    @SuppressWarnings({"unchecked", "squid:S119"}) // one letter generics can be ignored for better clarity
    public <L extends Component & HasComponents, KC extends Component & HasValue<?, K>, VC extends Component & HasValue<?, V>> MapEntryField(SerializableSupplier<L> layoutSupplier, SerializableSupplier<KC> keyComponentSupplier, SerializableSupplier<VC> valueComponentSupplier) {
        this.layoutSupplier = (SerializableSupplier<HasComponents>) (SerializableSupplier<?>) layoutSupplier;
        this.keyComponentSupplier = (SerializableSupplier<HasValue<?,K>>) (SerializableSupplier<?>) keyComponentSupplier;
        this.valueComponentSupplier = (SerializableSupplier<HasValue<?,V>>) (SerializableSupplier<?>) valueComponentSupplier;
        this.repaintLayout();
        this.repaintComponents();
        this.updateValue();
    }

    /**
     * Creates a {@link MapEntryField} with given providers for key and value component, and with {@link #DEFAULT_LAYOUT_PROVIDER}.
     * @param keyComponentSupplier Provides a field to display a key of the entry.
     * @param valueComponentSupplier Provides a field to display a value of the entry.
     * @param <KC> Ensures key component provider is both a {@link Component} and {@link HasValue} of type {@code K}.
     * @param <VC> Ensures value component provider is both a {@link Component} and {@link HasValue} of type {@code V}.
     */
    @SuppressWarnings("squid:S119") // one letter generics can be ignored for better clarity
    public <KC extends Component & HasValue<?, K>, VC extends Component & HasValue<?, V>> MapEntryField(SerializableSupplier<KC> keyComponentSupplier, SerializableSupplier<VC> valueComponentSupplier) {
        this(DEFAULT_LAYOUT_PROVIDER, keyComponentSupplier, valueComponentSupplier);
    }

    /**
     * Creates a {@link MapEntryField} that uses a (non-modifiable via the UI) {@link LabelField} as key and value component and a provided layout supplier.
     * @param layoutSupplier Supplier of a layout to put key and value components in, in that order.
     * @param <L> Ensures layout provider is both a {@link Component} and {@link HasComponents}.
     * @see #setKeyComponentSupplier(SerializableSupplier)
     * @see #setValueComponentSupplier(SerializableSupplier)
     */
    public <L extends Component & HasComponents> MapEntryField(SerializableSupplier<L> layoutSupplier) {
        this(layoutSupplier, LabelField::new, LabelField::new);
    }

    /**
     * Creates a {@link MapEntryField} that uses a (non-modifiable via the UI) {@link LabelField} as key and value component and a {@link #DEFAULT_LAYOUT_PROVIDER}.
     * @see #setLayoutSupplier(SerializableSupplier)
     * @see #setKeyComponentSupplier(SerializableSupplier)
     * @see #setValueComponentSupplier(SerializableSupplier)
     */
    public MapEntryField() {
        this(DEFAULT_LAYOUT_PROVIDER);
    }

    @Override
    protected Map.Entry<K, V> generateModelValue() {
        if(this.keyComponent != null && this.valueComponent != null)
            return Map.entry(this.keyComponent.getValue(), this.valueComponent.getValue());
        else return null;
    }

    private void repaintLayout() {
        if(this.layoutSupplier == null)
            throw new IllegalStateException("cannot create layout, no supplier set! (call setLayoutSupplier() with a non-null value)");
        else {
            this.removeComponentsFromLayout();
            if(this.layout != null)
                this.remove((Component) this.layout);

            this.layout = this.layoutSupplier.get();
            this.add((Component) this.layout);
            if(this.keyComponent != null)
                this.layout.add((Component) this.keyComponent);
            if(this.valueComponent != null)
                this.layout.add((Component) this.valueComponent);
        }
    }

    private void removeComponentsFromLayout() {
        if(this.keyComponent != null)
            this.layout.remove((Component) this.keyComponent);
        if(this.valueComponent != null)
            this.layout.remove((Component) this.valueComponent);
    }

    private void repaintComponents() {
        if(this.keyComponentSupplier == null)
            throw new IllegalStateException("cannot create component for key, no supplier set! (call setKeyComponentSupplier() or use a proper constructor)");
        else if(this.valueComponentSupplier == null)
            throw new IllegalStateException("cannot create component for value, no supplier set! (call setValueComponentSupplier() or use a proper constructor)");
        else {
            this.removeComponentsFromLayout();
            this.keyComponent = this.keyComponentSupplier.get();
            this.keyComponent.addValueChangeListener(this::valueChangedInSubComponent);
            this.valueComponent = this.valueComponentSupplier.get();
            this.valueComponent.addValueChangeListener(this::valueChangedInSubComponent);
            this.layout.add((Component) this.keyComponent, (Component) this.valueComponent);
            final Map.Entry<K, V> currentValue = this.getValue();

            if(currentValue != null) {
                this.keyComponent.setValue(currentValue.getKey());
                this.valueComponent.setValue(currentValue.getValue());
            }
        }
    }

    private void valueChangedInSubComponent(ValueChangeEvent<?> o) {
        if(!this.valueChangeInProgress)
            this.updateValue();
    }

    @Override
    protected void setPresentationValue(Map.Entry<K, V> entry) {
        this.valueChangeInProgress = true;
        if(this.keyComponent == null || this.valueComponent == null)
            this.repaintComponents();

        this.keyComponent.setValue(entry.getKey());
        this.valueComponent.setValue(entry.getValue());
        this.valueChangeInProgress = false;
    }

    /**
     * Returns the current supplier of key components.
     * @param <C> Ensures the result provides a {@link Component} that {@link HasValue} of type {@code K}.
     * @return A {@link Supplier}.
     */
    @SuppressWarnings("unchecked") // setter ensures types are ok
    public <C extends Component & HasValue<?, K>> Supplier<C> getKeyComponentSupplier() {
        return (Supplier<C>) (Supplier<?>) keyComponentSupplier;
    }

    /**
     * Sets a new key component supplier.
     * @param keyComponentSupplier A supplier to use.
     * @param <C> Ensures the supplier provides a {@link Component} that {@link HasValue} of type {@code K}.
     */
    @SuppressWarnings("unchecked")
    public <C extends Component & HasValue<?, K>> void setKeyComponentSupplier(SerializableSupplier<C> keyComponentSupplier) {
        this.keyComponentSupplier = (SerializableSupplier<HasValue<?,K>>) (SerializableSupplier<?>) keyComponentSupplier;
        this.repaintComponents();
    }

    /**
     * Returns the current supplier of value components.
     * @param <C> Ensures the result supplies a {@link Component} that {@link HasValue} of type {@code V}.
     * @return A {@link Supplier}.
     */
    @SuppressWarnings("unchecked") // setter ensures types are valid
    public <C extends Component & HasValue<?, V>> Supplier<C> getValueComponentSupplier() {
        return (Supplier<C>) (Supplier<?>) valueComponentSupplier;
    }

    /**
     * Sets a new value component supplier.
     * @param valueComponentSupplier A supplier to use.
     * @param <C> Ensures the supplier provides a {@link Component} that {@link HasValue} of type {@code V}.
     */
    @SuppressWarnings("unchecked")
    public <C extends Component & HasValue<?, V>> void setValueComponentSupplier(SerializableSupplier<C> valueComponentSupplier) {
        this.valueComponentSupplier = (SerializableSupplier<HasValue<?,V>>) (SerializableSupplier<?>) valueComponentSupplier;
        this.repaintComponents();
    }

    /**
     * Returns the current supplier of layout.
     * @param <C> Ensures the result provides a {@link Component} that {@link HasComponents}.
     * @return A {@link Supplier}
     */
    @SuppressWarnings("unchecked") // setter forces the cast to be ok
    public <C extends Component & HasComponents> Supplier<C> getLayoutSupplier() {
        return (Supplier<C>) (Supplier<?>) layoutSupplier;
    }

    /**
     * Sets a new supplier of layout.
     * @param layoutSupplier A supplier to use. Key component will be added first, followed by the value component.
     * @param <C> Ensures the supplier provides a {@link Component} that {@link HasComponents}.
     */
    @SuppressWarnings("unchecked")
    public <C extends Component & HasComponents> void setLayoutSupplier(SerializableSupplier<C> layoutSupplier) {
        this.layoutSupplier = (SerializableSupplier<HasComponents>) (SerializableSupplier<?>) layoutSupplier;
        this.repaintLayout();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        if(this.keyComponent != null)
            this.keyComponent.setReadOnly(readOnly);
        if(this.valueComponent != null)
            this.valueComponent.setReadOnly(readOnly);
    }
}
