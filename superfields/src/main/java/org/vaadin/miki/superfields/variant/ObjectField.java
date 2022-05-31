package org.vaadin.miki.superfields.variant;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.function.SerializableSupplier;

import java.util.HashMap;
import java.util.Map;

/**
 * A field capable of displaying any object, provided it is known how to construct various elements of it.
 *
 * @author miki
 * @since 2022-05-16
 */
public class ObjectField<T> extends CustomField<T> {

    private final SerializableSupplier<T> emptyObjectSupplier;

    private final Map<ObjectPropertyDefinition<T, ?>, HasValue<?, ?>> properties = new HashMap<>();

    private final Class<T> dataType;

    private ObjectPropertyDefinitionProvider<T> definitionProvider;
    private ObjectPropertyFieldBuilder<T> fieldBuilder;

    public ObjectField(Class<T> dataType, SerializableSupplier<T> emptyObjectSupplier) {
        this.emptyObjectSupplier = emptyObjectSupplier;
        this.dataType = dataType;
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

    @Override
    protected void setPresentationValue(T t) {
        this.getDefinitionProvider().getObjectPropertyDefinitions(this.dataType, t)
                .forEach(definition -> {
                    this.fieldBuilder.buildPropertyField(definition)
                });

        this.properties.forEach((def, field) -> this.showPropertyOfObject(t, def, field));
    }

    public Class<T> getDataType() {
        return dataType;
    }

    public ObjectPropertyDefinitionProvider<T> getDefinitionProvider() {
        return definitionProvider;
    }

    public void setDefinitionProvider(ObjectPropertyDefinitionProvider<T> definitionProvider) {
        this.definitionProvider = definitionProvider;
    }
}
