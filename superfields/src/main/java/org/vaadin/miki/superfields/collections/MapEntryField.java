package org.vaadin.miki.superfields.collections;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.function.SerializableSupplier;
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
public class MapEntryField<K, V> extends CustomField<Map.Entry<K, V>> {

    public static final SerializableSupplier<HorizontalLayout> DEFAULT_LAYOUT_PROVIDER = HorizontalLayout::new;

    private SerializableSupplier<HasValue<?, K>> keyComponentSupplier;
    private SerializableSupplier<HasValue<?, V>> valueComponentSupplier;
    private SerializableSupplier<HasComponents> layoutSupplier;

    private HasComponents layout;
    private HasValue<?, K> keyComponent;
    private HasValue<?, V> valueComponent;

    @SuppressWarnings("unchecked")
    public <L extends Component & HasComponents, KC extends Component & HasValue<?, K>, VC extends Component & HasValue<?, V>> MapEntryField(SerializableSupplier<L> layoutSupplier, SerializableSupplier<KC> keyComponentSupplier, SerializableSupplier<VC> valueComponentSupplier) {
        this.layoutSupplier = (SerializableSupplier<HasComponents>) (SerializableSupplier<?>) layoutSupplier;
        this.keyComponentSupplier = (SerializableSupplier<HasValue<?,K>>) (SerializableSupplier<?>) keyComponentSupplier;
        this.valueComponentSupplier = (SerializableSupplier<HasValue<?,V>>) (SerializableSupplier<?>) valueComponentSupplier;
        this.repaintLayout();
        this.repaintComponents();
    }

    public <KC extends Component & HasValue<?, K>, VC extends Component & HasValue<?, V>> MapEntryField(SerializableSupplier<KC> keyComponentSupplier, SerializableSupplier<VC> valueComponentSupplier) {
        this(DEFAULT_LAYOUT_PROVIDER, keyComponentSupplier, valueComponentSupplier);
    }

    public <L extends Component & HasComponents> MapEntryField(SerializableSupplier<L> layoutSupplier) {
        this(layoutSupplier, LabelField::new, LabelField::new);
    }

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
            this.valueComponent = this.valueComponentSupplier.get();
            this.layout.add((Component) this.keyComponent, (Component) this.valueComponent);
            final Map.Entry<K, V> currentValue = this.getValue();

            if(currentValue != null) {
                this.keyComponent.setValue(currentValue.getKey());
                this.valueComponent.setValue(currentValue.getValue());
            }
        }
    }

    @Override
    protected void setPresentationValue(Map.Entry<K, V> entry) {
        if(this.keyComponent == null || this.valueComponent == null)
            this.repaintComponents();

        this.keyComponent.setValue(entry.getKey());
        this.valueComponent.setValue(entry.getValue());
    }

    @SuppressWarnings("squid:S1452")
    public Supplier<HasValue<?, K>> getKeyComponentSupplier() {
        return keyComponentSupplier;
    }

    @SuppressWarnings("unchecked")
    public <C extends Component & HasValue<?, K>> void setKeyComponentSupplier(SerializableSupplier<C> keyComponentSupplier) {
        this.keyComponentSupplier = (SerializableSupplier<HasValue<?,K>>) (SerializableSupplier<?>) keyComponentSupplier;
        this.repaintComponents();
    }

    @SuppressWarnings("squid:S1452")
    public Supplier<HasValue<?, V>> getValueComponentSupplier() {
        return valueComponentSupplier;
    }

    @SuppressWarnings("unchecked")
    public <C extends Component & HasValue<?, V>> void setValueComponentSupplier(SerializableSupplier<C> valueComponentSupplier) {
        this.valueComponentSupplier = (SerializableSupplier<HasValue<?,V>>) (SerializableSupplier<?>) valueComponentSupplier;
        this.repaintComponents();
    }

    public Supplier<HasComponents> getLayoutSupplier() {
        return layoutSupplier;
    }

    @SuppressWarnings("unchecked")
    public <C extends Component & HasComponents> void setLayoutSupplier(SerializableSupplier<C> layoutSupplier) {
        this.layoutSupplier = (SerializableSupplier<HasComponents>) (SerializableSupplier<?>) layoutSupplier;
        this.repaintLayout();
    }
}
