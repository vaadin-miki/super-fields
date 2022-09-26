package org.vaadin.miki.superfields.object;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.function.SerializableSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.miki.markers.HasReadOnly;
import org.vaadin.miki.markers.WithHelperMixin;
import org.vaadin.miki.markers.WithHelperPositionableMixin;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithLabelPositionableMixin;
import org.vaadin.miki.markers.WithValueMixin;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;
import org.vaadin.miki.superfields.object.builder.SimplePropertyComponentBuilder;
import org.vaadin.miki.superfields.object.reflect.ReflectivePropertyProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A field capable of displaying any object, provided it is known how to construct various elements of it.
 * Construction of the field happens in stages: <ol>
 *     <li>obtain properties of the object</li>
 *     <li>group properties</li>
 *     <li>map each property to a component capable of showing that property</li>
 *     <li>configure components (individually and in defined groups)</li>
 *     <li>set value of each component</li>
 * </ol>
 *
 * Note that the above defines just the overall process. While there are reasonable defaults, they do very little.
 * Please adjust the configuration to suit individual use cases.
 *
 * @author miki
 * @since 2022-05-16
 */
@CssImport(value = "./styles/label-positions.css", themeFor = "vaadin-custom-field")
public class ObjectField<T> extends CustomField<T>
        implements HasStyle, WithHelperMixin<ObjectField<T>>, WithHelperPositionableMixin<ObjectField<T>>,
                   WithIdMixin<ObjectField<T>>, WithLabelMixin<ObjectField<T>>, WithLabelPositionableMixin<ObjectField<T>>,
                   WithValueMixin<AbstractField.ComponentValueChangeEvent<CustomField<T>, T>, T, ObjectField<T>> {

    public static final SerializableSupplier<FlexLayout> DEFAULT_LAYOUT_PROVIDER = FlexLayoutHelpers::column;
    public static final SerializableSupplier<PropertyProvider> DEFAULT_PROPERTY_PROVIDER = ReflectivePropertyProvider::new;
    public static final SerializableSupplier<PropertyComponentBuilder> DEFAULT_COMPONENT_BUILDER = SimplePropertyComponentBuilder::new;
    public static final SerializableSupplier<PropertyGroupingProvider> DEFAULT_GROUPING_PROVIDER = DefaultPropertyGroupingProvider::new;
    public static final SerializableSupplier<PropertyGroupLayoutProvider> DEFAULT_GROUP_LAYOUT_PROVIDER = DefaultPropertyGroupLayoutProvider::new;

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectField.class);

    private final SerializableSupplier<T> emptyObjectSupplier;

    private final Map<Property<T, ?>, HasValue<?, ?>> properties = new LinkedHashMap<>();
    private final Map<String, Component> groupLayouts = new LinkedHashMap<>();
    private final Set<Component> componentsNotInGroups = new LinkedHashSet<>();
    private final List<Property<T, ?>> definitions = new ArrayList<>();
    private final List<ComponentConfigurator<T>> configurators = new ArrayList<>();
    private final List<ComponentGroupConfigurator> groupConfigurators = new ArrayList<>();

    private final Class<T> dataType;

    private final HasComponents layout;

    private boolean reloadNeeded = true;

    private PropertyProvider definitionProvider = DEFAULT_PROPERTY_PROVIDER.get();
    private PropertyComponentBuilder componentBuilder = DEFAULT_COMPONENT_BUILDER.get();
    private PropertyGroupingProvider propertyGroupingProvider = DEFAULT_GROUPING_PROVIDER.get();
    private PropertyGroupLayoutProvider groupLayoutProvider = DEFAULT_GROUP_LAYOUT_PROVIDER.get();

    private boolean valueChangeInProgress = false;

    /**
     * Builds an {@link ObjectField} using {@link #DEFAULT_LAYOUT_PROVIDER} for the main layout.
     *
     * @param dataType Type of objects to display.
     * @param emptyObjectSupplier A method to provide new, empty objects of the given type.
     */
    public ObjectField(Class<T> dataType, SerializableSupplier<T> emptyObjectSupplier) {
        this(dataType, emptyObjectSupplier, DEFAULT_LAYOUT_PROVIDER);
    }

    /**
     * Builds an {@link ObjectField} using a given supplier for the main layout.
     *
     * @param dataType Type of objects to display.
     * @param emptyObjectSupplier A method to provide new, empty objects of the given type.
     * @param layoutSupplier A method to provide the main layout of this component.
     * @param <L> Layout type.
     */
    public <L extends Component & HasComponents> ObjectField(Class<T> dataType, SerializableSupplier<T> emptyObjectSupplier, SerializableSupplier<L> layoutSupplier) {
        super(emptyObjectSupplier.get());
        this.emptyObjectSupplier = emptyObjectSupplier;
        this.dataType = dataType;

        final var mainLayout = layoutSupplier.get();
        this.layout = mainLayout;
        this.add(mainLayout);
    }

    @SuppressWarnings("unchecked") // should be safe
    private <P> void setPropertyOfObject(T object, Property<T, P> definition, HasValue<?, ?> component) {
        if(object != null)
            definition.getSetter().ifPresent(s -> s.accept(object, (P) component.getValue()));
    }

    @SuppressWarnings("unchecked") // should be safe
    private <P> void showPropertyOfObject(T object, Property<T, P> definition, HasValue<?, ?> component) {
        if(object != null)
            definition.getGetter().ifPresent(getter -> ((HasValue<?, P>)component).setValue(getter.apply(object)));
    }

    @Override
    protected T generateModelValue() {
        final var result = this.emptyObjectSupplier.get();
        this.properties.forEach((def, field) -> this.setPropertyOfObject(result, def, field));
        LOGGER.debug("ObjectField<{}> - generated model value: {}", this.getDataType().getSimpleName(), result);
        return result;
    }

    /**
     * Prepares components to display the given value.
     * Does not set any value of any component.
     *
     * @param t Value to be shown.
     */
    private void prepareComponents(T t) {
        // get definitions for the object
        final var newDefinitions = this.getPropertyProvider().getObjectPropertyDefinitions(this.dataType, t);
        LOGGER.debug("> obtained {} definitions for {}", newDefinitions.size(), this.dataType);
        // if the definitions are different from the last used ones, repaint the whole component
        if(!this.definitions.equals(newDefinitions)) {
            LOGGER.debug("refreshing properties to {}", newDefinitions);
            // remove group layouts
            this.groupLayouts.values().forEach(this.layout::remove);
            // remove components not in layouts
            this.componentsNotInGroups.forEach(this.layout::remove);

            this.properties.clear();
            this.groupLayouts.clear();
            this.componentsNotInGroups.clear();
            this.definitions.clear();
            this.definitions.addAll(newDefinitions);
            // make groups
            final Map<String, List<Property<T, ?>>> groupDefinitions = this.getPropertyGroupingProvider().groupDefinitions(this.definitions);
            // build layouts
            groupDefinitions.forEach((groupName, groupContents) ->
                this.getGroupLayoutProvider().buildGroupLayout(groupName, groupContents).ifPresentOrElse(groupLayout -> {
                    this.groupLayouts.put(groupName, groupLayout);
                    final var groupComponents = groupContents.stream().map(definition -> {
                        final var component = this.buildAndConfigureComponentForDefinition(t, definition);
                        groupLayout.add(component);
                        this.properties.put(definition, component);
                        LOGGER.debug("field {} belongs to group {}, added to group layout", definition.getName(), groupName);
                        return component;
                    }).collect(Collectors.toList());
                    // configure an entire group
                    this.groupConfigurators.forEach(configurator -> configurator.configureComponentGroup(t, groupName, groupContents, groupComponents));
                    // add group layout to layout
                    this.layout.add(groupLayout);
                }, () ->
                    groupContents.forEach(definition -> {
                        final var component = this.buildAndConfigureComponentForDefinition(t, definition);
                        this.layout.add(component);
                        this.properties.put(definition, component);
                        this.componentsNotInGroups.add(component);
                        LOGGER.debug("field {} belongs to main layout", definition.getName());
                    })
                )
            );
            // configure all components
            if(!this.groupConfigurators.isEmpty()) {
                final var allComponents = this.definitions.stream().map(this.properties::get).collect(Collectors.toList());
                this.groupConfigurators.forEach(configurator -> configurator.configureComponentGroup(t, null, this.definitions, allComponents));
            }

        }
    }

    private <P, C extends Component & HasValue<?, P>> C buildAndConfigureComponentForDefinition(T t, Property<T, P> definition) {
        final C result = (C) this.getPropertyComponentBuilder().buildPropertyField(definition).orElseThrow(() -> new IllegalArgumentException(String.format("could not construct a component for property %s (of object %s) using %s", definition.getName(), t, this.getPropertyComponentBuilder().getClass().getSimpleName())));
        result.addValueChangeListener(this::valueChangedInSubComponent);
        LOGGER.debug("> running component {} ({}) through {} configurator(s)", definition.getName(), result.getClass().getSimpleName(), this.configurators.size());
        this.configurators.forEach(configurator -> configurator.configureComponent(t, definition, result));
        return result;
    }

    private void valueChangedInSubComponent(ValueChangeEvent<?> event) {
        if(!this.valueChangeInProgress) {
            this.updateValue();
            LOGGER.debug("ObjectField<{}> - current value after update: {}", this.getDataType().getSimpleName(), this.getValue());
        }
    }

    private void reload() {
        this.reloadNeeded = false;
        this.definitions.clear();
    }

    /**
     * Repaints the component with its current value.
     * In some rare cases it makes sense to completely reload the components without changing the value of this field.
     */
    public void repaint() {
        if(this.isReloadNeeded())
            this.reload();
        this.prepareComponents(this.getValue());
    }

    @Override
    protected void setPresentationValue(T t) {
        this.valueChangeInProgress = true;
        // clean up previous information if needed
        if(this.isReloadNeeded())
            this.reload();
        this.prepareComponents(t);
        // set the values of each property
        this.properties.forEach((def, field) -> this.showPropertyOfObject(t, def, field));
        this.valueChangeInProgress = false;
    }

    /**
     * Returns the type of the value returned by this field.
     * @return Type.
     */
    public Class<T> getDataType() {
        return dataType;
    }

    /**
     * Indicates that reload of data and components is needed.
     */
    protected void markReloadNeeded() {
        this.reloadNeeded = true;
    }

    /**
     * Checks if reloading is currently needed.
     * @return When {@code true}, the inner state of the component will be reloaded the next time a value is displayed.
     */
    protected boolean isReloadNeeded() {
        return this.reloadNeeded;
    }

    /**
     * Returns the current {@link PropertyProvider}.
     * @return Current {@link PropertyProvider}.
     */
    public PropertyProvider getPropertyProvider() {
        return definitionProvider;
    }

    /**
     * Sets the new {@link PropertyProvider}.
     * Note that changes will not be reflected until the component is {@link #repaint()}ed or a new value is set.
     * @param propertyProvider A {@link PropertyProvider} to use. If {@code null} is passed, one provided by {@link #DEFAULT_PROPERTY_PROVIDER} will be used.
     */
    public void setPropertyProvider(PropertyProvider propertyProvider) {
        this.definitionProvider = Objects.requireNonNullElseGet(propertyProvider, DEFAULT_PROPERTY_PROVIDER);
        this.markReloadNeeded();
    }

    /**
     * Chains {@link #setPropertyProvider(PropertyProvider)} and returns itself.
     * @param provider A {@link PropertyProvider} to use.
     * @return This.
     * @see #setPropertyProvider(PropertyProvider)
     */
    public final ObjectField<T> withPropertyProvider(PropertyProvider provider) {
        this.setPropertyProvider(provider);
        return this;
    }

    /**
     * Sets the new {@link PropertyComponentBuilder}.
     * Note that changes will not be reflected until the component is {@link #repaint()}ed or a new value is set.
     * @param builder A {@link PropertyComponentBuilder} to use. If {@code null} is passed, one provided by {@link #DEFAULT_COMPONENT_BUILDER} will be used.
     */
    public void setPropertyComponentBuilder(PropertyComponentBuilder builder) {
        this.componentBuilder = Objects.requireNonNullElseGet(builder, DEFAULT_COMPONENT_BUILDER);
        this.markReloadNeeded();
    }

    /**
     * Returns the current {@link PropertyComponentBuilder}.
     * @return A {@link PropertyComponentBuilder}.
     */
    public PropertyComponentBuilder getPropertyComponentBuilder() {
        return this.componentBuilder;
    }

    /**
     * Chains {@link #setPropertyComponentBuilder(PropertyComponentBuilder)} and returns itself.
     * @param builder Builder.
     * @return This.
     * @see #setPropertyComponentBuilder(PropertyComponentBuilder)
     */
    public final ObjectField<T> withPropertyComponentBuilder(PropertyComponentBuilder builder) {
        this.setPropertyComponentBuilder(builder);
        return this;
    }

    /**
     * Sets the new {@link PropertyGroupingProvider}.
     * Note that changes will not be reflected until the component is {@link #repaint()}ed or a new value is set.
     * @param propertyGroupingProvider A {@link PropertyGroupingProvider} to use. If {@code null} is passed, one provided by {@link #DEFAULT_GROUPING_PROVIDER} will be used.
     */
    public void setPropertyGroupingProvider(PropertyGroupingProvider propertyGroupingProvider) {
        this.propertyGroupingProvider = Objects.requireNonNullElseGet(propertyGroupingProvider, DEFAULT_GROUPING_PROVIDER);
        this.markReloadNeeded();
    }

    /**
     * Returns the current {@link PropertyGroupingProvider}.
     * @return A {@link PropertyGroupingProvider}.
     */
    public PropertyGroupingProvider getPropertyGroupingProvider() {
        return propertyGroupingProvider;
    }

    /**
     * Chains {@link #setPropertyGroupingProvider(PropertyGroupingProvider)} and returns itself.
     * @param provider Provider.
     * @return This.
     * @see #setPropertyGroupingProvider(PropertyGroupingProvider)
     */
    public final ObjectField<T> withPropertyGroupingProvider(PropertyGroupingProvider provider) {
        this.setPropertyGroupingProvider(provider);
        return this;
    }

    /**
     * Returns current {@link PropertyGroupLayoutProvider}.
     * @return A {@link PropertyGroupLayoutProvider}.
     */
    public PropertyGroupLayoutProvider getGroupLayoutProvider() {
        return groupLayoutProvider;
    }

    /**
     * Sets the new {@link PropertyGroupLayoutProvider}.
     * Note that changes will not be reflected until the component is {@link #repaint()}ed or a new value is set.
     * @param groupLayoutProvider A {@link PropertyGroupLayoutProvider} to use. If {@code null} is passed, one provided by {@link #DEFAULT_GROUP_LAYOUT_PROVIDER} will be used.
     */
    public void setGroupLayoutProvider(PropertyGroupLayoutProvider groupLayoutProvider) {
        this.groupLayoutProvider = Objects.requireNonNullElseGet(groupLayoutProvider, DEFAULT_GROUP_LAYOUT_PROVIDER);
        this.markReloadNeeded();
    }

    /**
     * Chains {@link #setGroupLayoutProvider(PropertyGroupLayoutProvider)} and returns itself.
     * @param provider Provider.
     * @return This.
     * @see #setGroupLayoutProvider(PropertyGroupLayoutProvider)
     */
    public final ObjectField<T> withGroupLayoutProvider(PropertyGroupLayoutProvider provider) {
        this.setGroupLayoutProvider(provider);
        return this;
    }

    /**
     * Adds given {@link ComponentConfigurator}s. Each configurator receives every component for each property. Configurators are run in the order of adding.
     * Note that changes will not be reflected until the component is {@link #repaint()}ed or a new value is set.
     * @param configurators Configurators to add.
     */
    @SafeVarargs
    public final void addComponentConfigurators(ComponentConfigurator<T>... configurators) {
        this.addComponentConfigurators(Arrays.asList(configurators));
    }

    /**
     * Adds given {@link ComponentConfigurator}s. Each configurator receives every component for each property. Configurators are run in the order of adding.
     * Note that changes will not be reflected until the component is {@link #repaint()}ed or a new value is set.
     * @param configurators Configurators to add.
     */
    public void addComponentConfigurators(Collection<ComponentConfigurator<T>> configurators) {
        this.configurators.addAll(configurators);
        this.markReloadNeeded();
    }

    /**
     * Removes given configurator.
     * @param configurator Configurator to remove.
     */
    public void removeComponentConfigurator(ComponentConfigurator<T> configurator) {
        this.configurators.remove(configurator);
        this.markReloadNeeded();
    }

    /**
     * Chains {@link #addComponentConfigurators(ComponentConfigurator[])} and returns itself.
     * @param configurators Configurators to add.
     * @return This.
     * @see #addComponentConfigurators(ComponentConfigurator[])
     */
    @SafeVarargs
    public final ObjectField<T> withComponentConfigurators(ComponentConfigurator<T>... configurators) {
        this.addComponentConfigurators(configurators);
        return this;
    }

    /**
     * Chains {@link #addComponentConfigurators(Collection)} and returns itself.
     * @param configurators Configurators to add.
     * @return This.
     * @see #addComponentConfigurators(Collection)
     */
    public final ObjectField<T> withComponentConfigurators(Collection<ComponentConfigurator<T>> configurators) {
        this.addComponentConfigurators(configurators);
        return this;
    }

    /**
     * Removes all previously added {@link ComponentConfigurator}.
     * Note that changes will not be reflected until the component is {@link #repaint()}ed or a new value is set.
     */
    public void clearComponentConfigurators() {
        this.configurators.clear();
        this.markReloadNeeded();
    }

    /**
     * Adds given {@link ComponentGroupConfigurator}s. Each configurator receives every group of components after basic {@link ComponentConfigurator}s are executed. Configurators are run in the order of adding.
     * Note that changes will not be reflected until the component is {@link #repaint()}ed or a new value is set.
     * @param configurators Configurators to add.
     */
    public void addComponentGroupConfigurators(ComponentGroupConfigurator... configurators) {
        this.addComponentGroupConfigurators(Arrays.asList(configurators));
    }

    /**
     * Adds given {@link ComponentGroupConfigurator}s. Each configurator receives every group of components after basic {@link ComponentConfigurator}s are executed. Configurators are run in the order of adding.
     * Note that changes will not be reflected until the component is {@link #repaint()}ed or a new value is set.
     * @param configurators Configurators to add.
     */
    public void addComponentGroupConfigurators(Collection<ComponentGroupConfigurator> configurators) {
        this.groupConfigurators.addAll(configurators);
        this.markReloadNeeded();
    }

    /**
     * Removes given {@link ComponentGroupConfigurator}.
     * Note that changes will not be reflected until the component is {@link #repaint()}ed or a new value is set.
     * @param configurator Configurator to remove.
     */
    public void removeComponentGroupConfigurator(ComponentGroupConfigurator configurator) {
        this.groupConfigurators.remove(configurator);
        this.markReloadNeeded();
    }

    /**
     * Chains {@link #addComponentGroupConfigurators(ComponentGroupConfigurator...)} and returns itself.
     * @param configurators Configurators to add.
     * @return This.
     * @see #addComponentGroupConfigurators(ComponentGroupConfigurator...)
     */
    public final ObjectField<T> withComponentGroupConfigurators(ComponentGroupConfigurator... configurators) {
        this.addComponentGroupConfigurators(configurators);
        return this;
    }

    /**
     * Chains {@link #addComponentGroupConfigurators(Collection)} and returns itself.
     * @param configurators Configurators to add.
     * @return This.
     * @see #addComponentGroupConfigurators(Collection)
     */
    public final ObjectField<T> withComponentGroupConfigurators(Collection<ComponentGroupConfigurator> configurators) {
        this.addComponentGroupConfigurators(configurators);
        return this;
    }

    /**
     * Removes all previously added {@link ComponentGroupConfigurator}s.
     * Note that changes will not be reflected until the component is {@link #repaint()}ed or a new value is set.
     */
    public void clearComponentGroupConfigurators() {
        this.groupConfigurators.clear();
        this.markReloadNeeded();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        HasReadOnly.setReadOnly(readOnly, (Component) this.layout);
    }

    @Override
    public void focus() {
        // forwards the focus to the first focusable component
        if(!this.getPropertiesAndComponents().isEmpty())
            this.getPropertiesAndComponents().values().stream()
                    .filter(Focusable.class::isInstance)
                    .findFirst()
                    .map(Focusable.class::cast)
                    .ifPresent(Focusable::focus);
        super.focus();
    }

    /**
     * Returns current map of definitions and components.
     * For testing purposes only.
     * @return A non-{@code null} map, matching a definition with a corresponding component.
     */
    @SuppressWarnings("squid:S1452") // this is for testing purposes anyway
    Map<Property<T, ?>, HasValue<?, ?>> getPropertiesAndComponents() {
        return properties;
    }

    /**
     * Returns the map of group layouts. Even though values are declared as {@link Component}, each should implement {@link HasComponents}.
     * For testing purposes only.
     * @return A non-{@code null} map matching a group name with its layout.
     */
    Map<String, Component> getGroupLayouts() {
        return groupLayouts;
    }

    /**
     * Returns the set of components not put in groups. Each should implement {@link HasValue} in addition to being a {@link Component}.
     * For testing purposes only.
     * @return A non-{@code null} set.
     */
    Set<Component> getComponentsNotInGroups() {
        return componentsNotInGroups;
    }

}
