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
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private final List<ObjectPropertyDefinition<T, ?>> definitions = new ArrayList<>();

    private final Class<T> dataType;

    private final HasComponents layout;

    private ObjectPropertyDefinitionProvider definitionProvider = new ReflectiveDefinitionProvider();
    private ObjectPropertyComponentBuilder fieldBuilder = new SimplePropertyComponentBuilder();

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
        definition.getSetter().accept(object, (P) component.getValue());
    }

    @SuppressWarnings("unchecked") // should be safe
    private <P> void showPropertyOfObject(T object, ObjectPropertyDefinition<T, P> definition, HasValue<?, ?> component) {
        ((HasValue<?, P>)component).setValue(definition.getGetter().apply(object));
    }

    @Override
    protected T generateModelValue() {
        final var result = this.emptyObjectSupplier.get();
        this.properties.forEach((def, field) -> this.setPropertyOfObject(result, def, field));
        return result;
    }

    private void prepareComponents(T t) {
        LOGGER.info("preparing components for {}", t);
        // get definitions for the object
        final var newDefinitions = this.getDefinitionProvider().getObjectPropertyDefinitions(this.dataType, t);
        // if the definitions are different from the last used ones, repaint the whole component
        if(!this.definitions.equals(newDefinitions)) {
            LOGGER.info("refreshing properties to {}", newDefinitions);
            this.layout.remove(this.properties.values().stream().map(Component.class::cast).toArray(Component[]::new));
            this.properties.clear();
            this.definitions.clear();
            this.definitions.addAll(newDefinitions);
            this.definitions.forEach(def -> {
                final var component = this.fieldBuilder.buildPropertyField(def);
                this.properties.put(def, component);
                this.layout.add(component);
            });
        }
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

    /**
     * Returns current map of definitions and components.
     * For testing purposes only.
     * @return A non-{@code null} map.
     */
    Map<ObjectPropertyDefinition<T, ?>, HasValue<?, ?>> getPropertiesAndComponents() {
        return properties;
    }
}
