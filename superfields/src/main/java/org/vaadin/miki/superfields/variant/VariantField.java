package org.vaadin.miki.superfields.variant;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.function.SerializableSupplier;
import org.vaadin.miki.markers.HasIcon;
import org.vaadin.miki.markers.HasLabel;
import org.vaadin.miki.markers.WithHelperMixin;
import org.vaadin.miki.markers.WithIconMixin;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithValueMixin;
import org.vaadin.miki.superfields.text.LabelField;
import org.vaadin.miki.util.ComponentTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * A field that supports {@link Object} as its value and displays a registered field that corresponds to a type of the value actually set by the field.
 *
 * @author miki
 * @since 2022-04-11
 */
public class VariantField extends CustomField<Object> implements WithLabelMixin<VariantField>, WithHelperMixin<VariantField>,
        WithValueMixin<AbstractField.ComponentValueChangeEvent<CustomField<Object>, Object>, Object, VariantField>, WithIdMixin<VariantField>,
        WithIconMixin<VariantField> {

    private final List<TypedFieldProvider<?, ?>> fieldProviders = new ArrayList<>();

    private SerializableSupplier<Component> nullComponentProvider = LabelField::new;

    private Component field = new LabelField<>();

    private Class<?> currentType;

    public VariantField() {
        this.add(this.field);
    }

    @Override
    protected Object generateModelValue() {
        if(this.field instanceof HasValue)
            return ((HasValue<?, ?>) this.field).getValue();
        else return null;
    }

    @Override
    @SuppressWarnings("unchecked") // all should be fine, as providers are type-safe
    protected void setPresentationValue(Object o) {
        // if value is of the same type, simply assign it to the current field
        if(this.currentType != null && this.currentType.isInstance(o))
            ((HasValue<?, Object>) this.field).setValue(o);
        else {
            this.remove(this.field);
            final Component oldField = this.field;
            if(o != null) {
                this.field = this.fieldProviders.stream()
                        .map(provider -> provider.provideComponent(o.getClass()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException(String.format("VariantField cannot display value of type %s - none of the registered field providers is suitable", o.getClass())));
                this.currentType = o.getClass();
            }
            else {
                this.currentType = null;
                this.field = this.getNullComponentProvider().get();
            }
            if(this.field instanceof HasValue) {
                ((HasValue<?, Object>) this.field).setValue(o);
                ((HasValue<?, ?>) this.field).addValueChangeListener(event -> this.updateValue());
            }
            // make sure the new component looks similar enough to the old one (label, icon, helper, etc. - all declared in interfaces)
            ComponentTools.copyProperties(oldField, this.field);
            this.add(this.field);
        }
    }

    public void addTypedFieldProvider(TypedFieldProvider<?, ?>... fieldProviders) {
        this.fieldProviders.addAll(Arrays.asList(fieldProviders));
    }

    public void removeTypeFieldProvider(TypedFieldProvider<?, ?> provider) {
        this.fieldProviders.remove(provider);
    }

    public final VariantField withTypedFieldProvider(TypedFieldProvider<?, ?>... fieldProviders) {
        this.addTypedFieldProvider(fieldProviders);
        return this;
    }

    public SerializableSupplier<Component> getNullComponentProvider() {
        return nullComponentProvider;
    }

    public void setNullComponentProvider(SerializableSupplier<Component> nullComponentProvider) {
        this.nullComponentProvider = nullComponentProvider;
        // ensure that if the value is null, the component is repainted
        if(this.getCurrentType() == null)
            this.setPresentationValue(null);
    }

    public final VariantField withNullComponentProvider(SerializableSupplier<Component> nullComponentProvider) {
        this.setNullComponentProvider(nullComponentProvider);
        return this;
    }

    @Override
    public void setIcon(Icon icon) {
        if(this.field instanceof HasIcon)
            ((HasIcon) this.field).setIcon(icon);
    }

    @Override
    public Icon getIcon() {
        return this.field instanceof HasIcon ? ((HasIcon) this.field).getIcon() : null;
    }

    @Override
    public void setLabel(String label) {
        if(this.field instanceof HasLabel)
            ((HasLabel) this.field).setLabel(label);
        super.setLabel(label);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        if(this.field instanceof HasValue)
            ((HasValue<?, ?>) this.field).setReadOnly(readOnly);

        super.setReadOnly(readOnly);
    }

    Class<?> getCurrentType() {
        return currentType;
    }

    Component getField() {
        return field;
    }
}
