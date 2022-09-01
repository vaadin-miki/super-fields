package org.vaadin.miki.superfields.variant;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.function.SerializableSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.miki.superfields.variant.builder.SimplePropertyComponentBuilder;
import org.vaadin.miki.superfields.variant.reflect.ReflectiveDefinitionProvider;

import java.util.ArrayList;
import java.util.HashMap;
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

    private final Map<ObjectPropertyDefinition<T, ?>, HasValue<?, ?>> properties = new HashMap<>();
    private final Map<String, Component> groupLayouts = new LinkedHashMap<>();
    private final Set<Component> componentsNotInGroups = new LinkedHashSet<>();
    private final List<ObjectPropertyDefinition<T, ?>> definitions = new ArrayList<>();
    private final List<ObjectPropertyComponentConfigurator> configurators = new ArrayList<>();
    private final List<ObjectPropertyComponentGroupConfigurator> groupConfigurators = new ArrayList<>();

    private final Class<T> dataType;

    private final HasComponents layout;

    private ObjectPropertyDefinitionProvider definitionProvider = new ReflectiveDefinitionProvider();
    private ObjectPropertyComponentBuilder fieldBuilder = new SimplePropertyComponentBuilder();
    private ObjectPropertyDefinitionGroupingProvider definitionGroupingProvider = new DefaultObjectPropertyDefinitionGroupingProvider();
    private ObjectPropertyGroupLayoutProvider groupLayoutProvider = new DefaultObjectPropertyGroupLayoutProvider();

    public <L extends Component & HasComponents> ObjectField(Class<T> dataType, SerializableSupplier<T> emptyObjectSupplier, SerializableSupplier<L> layoutSupplier) {
        super(emptyObjectSupplier.get());
        this.emptyObjectSupplier = emptyObjectSupplier;
        this.dataType = dataType;

        final var mainLayout = layoutSupplier.get();
        this.layout = mainLayout;
        this.add(mainLayout);
        this.prepareComponents(emptyObjectSupplier.get());
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
        // if the definitions are different from the last used ones, repaint the whole component
        if(!this.definitions.equals(newDefinitions)) {
            LOGGER.info("refreshing properties to {}", newDefinitions);
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
                        LOGGER.info("field {} belongs to group {}, added to group layout", definition.getName(), groupName);
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
                        LOGGER.info("field {} belongs to main layout", definition.getName());
                    })
                )
            );
            // configure all components
            if(!this.groupConfigurators.isEmpty()) {
                final var allComponents = this.definitions.stream().map(this.properties::get).collect(Collectors.toList());
                this.groupConfigurators.forEach(configurator -> configurator.configureComponentGroup(t, null, this.definitions, allComponents));
            }

            LOGGER.info("there are {} properties", this.definitions.size());
        }
    }

    private <P, C extends Component & HasValue<?, P>> C buildAndConfigureComponentForDefinition(T t, ObjectPropertyDefinition<T, P> definition) {
        final C result = this.getObjectPropertyComponentBuilder().buildPropertyField(definition);
        this.configurators.forEach(configurator -> configurator.configureComponent(t, definition, result));
        return result;
    }

    @Override
    protected void setPresentationValue(T t) {
        this.prepareComponents(t);
        LOGGER.info("updating {} components", this.properties.size());
        // set the values of each property
        this.properties.forEach((def, field) -> this.showPropertyOfObject(t, def, field));
    }

    public Class<T> getDataType() {
        return dataType;
    }

    public ObjectPropertyDefinitionProvider getDefinitionProvider() {
        return definitionProvider;
    }

    public void setDefinitionProvider(ObjectPropertyDefinitionProvider definitionProvider) {
        this.definitionProvider = definitionProvider;
    }

    public void setObjectPropertyComponentBuilder(ObjectPropertyComponentBuilder builder) {
        this.fieldBuilder = Objects.requireNonNullElseGet(builder, SimplePropertyComponentBuilder::new);
    }

    public ObjectPropertyComponentBuilder getObjectPropertyComponentBuilder() {
        return this.fieldBuilder;
    }

    public void setDefinitionGroupingProvider(ObjectPropertyDefinitionGroupingProvider definitionGroupingProvider) {
        this.definitionGroupingProvider = definitionGroupingProvider;
    }

    public ObjectPropertyDefinitionGroupingProvider getDefinitionGroupingProvider() {
        return definitionGroupingProvider;
    }

    public ObjectPropertyGroupLayoutProvider getGroupLayoutProvider() {
        return groupLayoutProvider;
    }

    public void setGroupLayoutProvider(ObjectPropertyGroupLayoutProvider groupLayoutProvider) {
        this.groupLayoutProvider = groupLayoutProvider;
    }

    public void addComponentConfigurator(ObjectPropertyComponentConfigurator configurator) {
        this.configurators.add(configurator);
    }

    public void removeComponentConfigurator(ObjectPropertyComponentConfigurator configurator) {
        this.configurators.remove(configurator);
    }

    public void clearComponentConfigurators() {
        this.configurators.clear();
    }

    public void addComponentGroupConfigurator(ObjectPropertyComponentGroupConfigurator configurator) {
        this.groupConfigurators.add(configurator);
    }

    public void removeComponentGroupConfigurator(ObjectPropertyComponentGroupConfigurator configurator) {
        this.groupConfigurators.remove(configurator);
    }

    public void clearComponentGroupConfigurators() {
        this.groupConfigurators.clear();
    }

    /**
     * Returns current map of definitions and components.
     * For testing purposes only.
     * @return A non-{@code null} map.
     */
    Map<ObjectPropertyDefinition<T, ?>, HasValue<?, ?>> getPropertiesAndComponents() {
        return properties;
    }
}
