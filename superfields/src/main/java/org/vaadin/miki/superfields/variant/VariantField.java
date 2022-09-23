package org.vaadin.miki.superfields.variant;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.function.SerializableSupplier;
import org.vaadin.miki.markers.WithHelperMixin;
import org.vaadin.miki.markers.WithHelperPositionableMixin;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithLabelPositionableMixin;
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
@CssImport(value = "./styles/label-positions.css", themeFor = "vaadin-custom-field")
public class VariantField extends CustomField<Object> implements HasStyle,
        WithLabelMixin<VariantField>,
        WithHelperMixin<VariantField>, WithHelperPositionableMixin<VariantField>,
        WithValueMixin<AbstractField.ComponentValueChangeEvent<CustomField<Object>, Object>, Object, VariantField>,
        WithIdMixin<VariantField>, WithLabelPositionableMixin<VariantField> {

    public static final SerializableSupplier<Component> DEFAULT_NULL_COMPONENT_PROVIDER = LabelField::new;

    private final List<TypedFieldProvider<?, ?>> fieldProviders = new ArrayList<>();

    private SerializableSupplier<Component> nullComponentProvider = DEFAULT_NULL_COMPONENT_PROVIDER;

    private Component field = new LabelField<>();

    private Class<?> currentType;

    /**
     * Creates a {@link VariantField} with a given label, null component provider and given {@link TypedFieldProvider}s.
     *
     * @param label Label.
     * @param nullComponentProvider A component provider when value is {@code null}.
     * @param providers Providers to use.
     */
    public VariantField(String label, SerializableSupplier<Component> nullComponentProvider, TypedFieldProvider<?, ?>... providers) {
        this();
        this.withTypedFieldProvider(providers).withNullComponentProvider(nullComponentProvider).setLabel(label);
    }

    /**
     * Creates a {@link VariantField} with a given label and given {@link TypedFieldProvider}s.
     * Uses {@link #DEFAULT_NULL_COMPONENT_PROVIDER} as {@link #setNullComponentProvider(SerializableSupplier)}.
     *
     * @param label Label.
     * @param providers Providers to use.
     */
    public VariantField(String label, TypedFieldProvider<?, ?>... providers) {
        this(label, DEFAULT_NULL_COMPONENT_PROVIDER, providers);
    }

    /**
     * Creates a {@link VariantField}.
     */
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
                ((HasValue<?, ?>) this.field).setReadOnly(this.isReadOnly());
            }
            // make sure the new component looks similar enough to the old one (label, icon, helper, etc. - all declared in interfaces)
            ComponentTools.copyProperties(oldField, this.field);
            this.add(this.field);
        }
    }

    /**
     * Adds one or more {@link TypedFieldProvider}s to this component.
     * Has no effect on the currently used value component.
     * @param fieldProviders Providers to use.
     */
    public void addTypedFieldProvider(TypedFieldProvider<?, ?>... fieldProviders) {
        this.fieldProviders.addAll(Arrays.asList(fieldProviders));
    }

    /**
     * Removes a given {@link TypedFieldProvider} if it was registered.
     * Has no effect on the currently used value component, even if the removed provider was used to create it.
     * @param provider Provider to remove.
     */
    public void removeTypeFieldProvider(TypedFieldProvider<?, ?> provider) {
        this.fieldProviders.remove(provider);
    }

    /**
     * Chains {@link #addTypedFieldProvider(TypedFieldProvider[])} and returns itself.
     * @param fieldProviders Providers to use.
     * @return This.
     */
    public final VariantField withTypedFieldProvider(TypedFieldProvider<?, ?>... fieldProviders) {
        this.addTypedFieldProvider(fieldProviders);
        return this;
    }

    /**
     * Returns current provider of components used to show {@code null}.
     * @return A supplier of a component that is used to display {@code null}.
     */
    public SerializableSupplier<Component> getNullComponentProvider() {
        return nullComponentProvider;
    }

    /**
     * Sets a provider of a component used to show {@code null}.
     * When the current value is {@code null} the provider will be invoked to replace the currently shown component.
     * @param nullComponentProvider Provider.
     */
    public void setNullComponentProvider(SerializableSupplier<Component> nullComponentProvider) {
        this.nullComponentProvider = nullComponentProvider;
        // ensure that if the value is null, the component is repainted
        if(this.getCurrentType() == null)
            this.setPresentationValue(null);
    }

    /**
     * Chains {@link #setNullComponentProvider(SerializableSupplier)} and returns itself.
     * @param nullComponentProvider Provider.
     * @return This.
     */
    public final VariantField withNullComponentProvider(SerializableSupplier<Component> nullComponentProvider) {
        this.setNullComponentProvider(nullComponentProvider);
        return this;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        if(this.field instanceof HasValue)
            ((HasValue<?, ?>) this.field).setReadOnly(readOnly);
    }

    /**
     * Returns the type of the current value.
     * Used for tests.
     * @return Type of current value, or {@code null} if the value is {@code null}.
     */
    Class<?> getCurrentType() {
        return currentType;
    }

    /**
     * Returns the field that is currently displaying the value.
     * Used for tests.
     * @return A component that is currently showing the value of this object.
     */
    Component getField() {
        return field;
    }
}
