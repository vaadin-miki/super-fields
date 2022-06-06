package org.vaadin.miki.superfields.variant;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.function.SerializableSupplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A field capable of displaying any object, provided it is known how to construct various elements of it.
 *
 * @author miki
 * @since 2022-05-16
 */
public class ObjectField extends CustomField<Object> {

    private final SerializableSupplier<?> emptyObjectSupplier;

    private final Map<ObjectPropertyDefinition<Object, ?>, HasValue<?, ?>> properties = new HashMap<>();
    private final List<ObjectPropertyDefinition<Object, ?>> definitions = new ArrayList<>();

    private final Class<?> dataType;

    private final HasComponents layout;

    private ObjectPropertyDefinitionProvider definitionProvider;
    private ObjectPropertyComponentBuilder fieldBuilder;

    public <T, L extends Component & HasComponents> ObjectField(Class<T> dataType, SerializableSupplier<T> emptyObjectSupplier, SerializableSupplier<L> layoutSupplier) {
        this.emptyObjectSupplier = emptyObjectSupplier;
        this.dataType = dataType;

        final var mainLayout = layoutSupplier.get();
        this.layout = mainLayout;
        this.add(mainLayout);
    }

    @SuppressWarnings("unchecked") // should be safe
    private <P> void setPropertyOfObject(Object object, ObjectPropertyDefinition<Object, P> definition, HasValue<?, ?> component) {
        definition.getSetter().accept(object, (P) component.getValue());
    }

    @SuppressWarnings("unchecked") // should be safe
    private <P> void showPropertyOfObject(Object object, ObjectPropertyDefinition<Object, P> definition, HasValue<?, ?> component) {
        ((HasValue<?, P>)component).setValue(definition.getGetter().apply(object));
    }

    @Override
    protected Object generateModelValue() {
        final var result = this.emptyObjectSupplier.get();
        this.properties.forEach((def, field) -> this.setPropertyOfObject(result, def, field));
        return result;
    }

    @Override
    protected void setPresentationValue(Object t) {
        // get definitions for the object
        @SuppressWarnings("unchecked") // it should be fine, generics are removed at runtime anyway
        final var newDefinitions = this.getDefinitionProvider().getObjectPropertyDefinitions((Class<? super Object>) this.dataType, t);
        // if the definitions are different from the last used ones, repaint the whole component
        if(!this.definitions.equals(newDefinitions)) {
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
        // set the values of each property
        this.properties.forEach((def, field) -> this.showPropertyOfObject(t, def, field));
    }

    public Class<?> getDataType() {
        return dataType;
    }

    public ObjectPropertyDefinitionProvider getDefinitionProvider() {
        return definitionProvider;
    }

    public void setDefinitionProvider(ObjectPropertyDefinitionProvider definitionProvider) {
        this.definitionProvider = definitionProvider;
    }
}
