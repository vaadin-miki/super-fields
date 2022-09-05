package org.vaadin.miki.superfields.variant;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.function.SerializableSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.miki.superfields.variant.builder.SimplePropertyComponentFactory;
import org.vaadin.miki.superfields.variant.reflect.ReflectiveDefinitionProvider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A field capable of displaying any object, provided it is known how to construct various elements of it.
 *
 * @author miki
 * @since 2022-05-16
 */
public class ObjectField<T> extends CustomField<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectField.class);

    private final SerializableSupplier<T> emptyObjectSupplier;

    private final Map<ObjectPropertyDefinition<T, ?>, HasValue<?, ?>> properties = new LinkedHashMap<>();
    private final Map<String, Component> groupLayouts = new LinkedHashMap<>();
    private final Set<Component> componentsNotInGroups = new LinkedHashSet<>();
    private final List<ObjectPropertyDefinition<T, ?>> definitions = new ArrayList<>();
    private final List<ObjectPropertyComponentConfigurator<T>> configurators = new ArrayList<>();
    private final List<ObjectPropertyComponentGroupConfigurator> groupConfigurators = new ArrayList<>();

    private final Class<T> dataType;

    private final HasComponents layout;

    private boolean reloadNeeded = true;

    private ObjectPropertyDefinitionProvider definitionProvider = new ReflectiveDefinitionProvider();
    private ObjectPropertyComponentFactory componentFactory = new SimplePropertyComponentFactory();
    private ObjectPropertyDefinitionGroupingProvider definitionGroupingProvider = new DefaultObjectPropertyDefinitionGroupingProvider();
    private ObjectPropertyGroupLayoutProvider groupLayoutProvider = new DefaultObjectPropertyGroupLayoutProvider();

    public <L extends Component & HasComponents> ObjectField(Class<T> dataType, SerializableSupplier<T> emptyObjectSupplier, SerializableSupplier<L> layoutSupplier) {
        super(emptyObjectSupplier.get());
        this.emptyObjectSupplier = emptyObjectSupplier;
        this.dataType = dataType;

        final var mainLayout = layoutSupplier.get();
        this.layout = mainLayout;
        this.add(mainLayout);
    }

    @SuppressWarnings("unchecked") // should be safe
    private <P> void setPropertyOfObject(T object, ObjectPropertyDefinition<T, P> definition, HasValue<?, ?> component) {
        definition.getSetter().ifPresent(s -> s.accept(object, (P) component.getValue()));
    }

    @SuppressWarnings("unchecked") // should be safe
    private <P> void showPropertyOfObject(T object, ObjectPropertyDefinition<T, P> definition, HasValue<?, ?> component) {
        definition.getGetter().ifPresent(getter -> ((HasValue<?, P>)component).setValue(getter.apply(object)));
    }

    @Override
    protected T generateModelValue() {
        final var result = this.emptyObjectSupplier.get();
        this.properties.forEach((def, field) -> this.setPropertyOfObject(result, def, field));
        return result;
    }

    private void prepareComponents(T t) {
        // get definitions for the object
        final var newDefinitions = this.getDefinitionProvider().getObjectPropertyDefinitions(this.dataType, t);
        LOGGER.info("> obtained {} definitions for {}", newDefinitions.size(), this.dataType);
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
            final Map<String, List<ObjectPropertyDefinition<T, ?>>> groupDefinitions = this.getDefinitionGroupingProvider().groupDefinitions(this.definitions);
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

    private <P, C extends Component & HasValue<?, P>> C buildAndConfigureComponentForDefinition(T t, ObjectPropertyDefinition<T, P> definition) {
        final C result = this.getObjectPropertyComponentFactory().buildPropertyField(definition);
        LOGGER.info("> running component {} ({}) through {} configurator(s)", definition.getName(), result.getClass().getSimpleName(), this.configurators.size());
        this.configurators.forEach(configurator -> configurator.configureComponent(t, definition, result));
        return result;
    }

    public void reload() {
        this.reloadNeeded = false;
        this.definitions.clear();
    }

    public void repaint() {
        this.prepareComponents(this.getValue());
    }

    @Override
    protected void setPresentationValue(T t) {
        LOGGER.info("> setting presentation value; reload needed? {}", this.isReloadNeeded());
        // clean up previous information if needed
        if(this.isReloadNeeded())
            this.reload();
        this.prepareComponents(t);
        // set the values of each property
        this.properties.forEach((def, field) -> this.showPropertyOfObject(t, def, field));
    }

    public Class<T> getDataType() {
        return dataType;
    }

    protected void markReloadNeeded() {
        this.reloadNeeded = true;
    }

    protected boolean isReloadNeeded() {
        return this.reloadNeeded;
    }

    public ObjectPropertyDefinitionProvider getDefinitionProvider() {
        return definitionProvider;
    }

    public void setDefinitionProvider(ObjectPropertyDefinitionProvider definitionProvider) {
        this.definitionProvider = definitionProvider;
        this.markReloadNeeded();
    }

    public void setObjectPropertyComponentFactory(ObjectPropertyComponentFactory builder) {
        this.componentFactory = Objects.requireNonNullElseGet(builder, SimplePropertyComponentFactory::new);
        this.markReloadNeeded();
    }

    public ObjectPropertyComponentFactory getObjectPropertyComponentFactory() {
        return this.componentFactory;
    }

    public void setDefinitionGroupingProvider(ObjectPropertyDefinitionGroupingProvider definitionGroupingProvider) {
        this.definitionGroupingProvider = definitionGroupingProvider;
        this.markReloadNeeded();
    }

    public ObjectPropertyDefinitionGroupingProvider getDefinitionGroupingProvider() {
        return definitionGroupingProvider;
    }

    public ObjectPropertyGroupLayoutProvider getGroupLayoutProvider() {
        return groupLayoutProvider;
    }

    public void setGroupLayoutProvider(ObjectPropertyGroupLayoutProvider groupLayoutProvider) {
        this.groupLayoutProvider = groupLayoutProvider;
        this.markReloadNeeded();
    }

    public void addComponentConfigurator(ObjectPropertyComponentConfigurator<T> configurator) {
        this.configurators.add(configurator);
        this.markReloadNeeded();
    }

    public void removeComponentConfigurator(ObjectPropertyComponentConfigurator<T> configurator) {
        this.configurators.remove(configurator);
        this.markReloadNeeded();
    }

    public void clearComponentConfigurators() {
        this.configurators.clear();
        this.markReloadNeeded();
    }

    public void addComponentGroupConfigurator(ObjectPropertyComponentGroupConfigurator configurator) {
        this.groupConfigurators.add(configurator);
        this.markReloadNeeded();
    }

    public void removeComponentGroupConfigurator(ObjectPropertyComponentGroupConfigurator configurator) {
        this.groupConfigurators.remove(configurator);
        this.markReloadNeeded();
    }

    public void clearComponentGroupConfigurators() {
        this.groupConfigurators.clear();
        this.markReloadNeeded();
    }

    /**
     * Returns current map of definitions and components.
     * For testing purposes only.
     * @return A non-{@code null} map, matching a definition with a corresponding component.
     */
    Map<ObjectPropertyDefinition<T, ?>, HasValue<?, ?>> getPropertiesAndComponents() {
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
