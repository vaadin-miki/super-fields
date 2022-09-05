package org.vaadin.miki.superfields.variant.builder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.function.SerializableSupplier;
import org.slf4j.LoggerFactory;
import org.vaadin.miki.markers.HasLabel;
import org.vaadin.miki.superfields.text.LabelField;
import org.vaadin.miki.superfields.variant.ObjectPropertyComponentFactory;
import org.vaadin.miki.superfields.variant.ObjectPropertyDefinition;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Reference implementation of {@link ObjectPropertyComponentFactory}.
 *
 * @author miki
 * @since 2022-06-06
 */
public class SimplePropertyComponentFactory implements ObjectPropertyComponentFactory {

    private final Map<Class<?>, ComponentBuilder<?>> typeBuilders = new HashMap<>();

    private final Supplier<? extends Component> defaultBuilder = LabelField::new;

    private boolean defaultLabel = true;

    @SuppressWarnings("unchecked") // fine, I guess?
    @Override
    public <P, C extends Component & HasValue<?, P>> C buildPropertyField(ObjectPropertyDefinition<?, P> property) {
        final C result;
        if(this.typeBuilders.containsKey(property.getType()))
            result = (C) ((ComponentBuilder<P>) this.typeBuilders.get(property.getType())).buildPropertyField(property);
        else result = (C) this.defaultBuilder.get();

        if((result instanceof HasLabel || result instanceof com.vaadin.flow.component.HasLabel) && this.isDefaultLabel()) {
            // regexp by polygenelubricants (what a username!) https://stackoverflow.com/a/2560017/384484
            String fieldLabel = property.getName().replaceAll("(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])", " ");
            fieldLabel = fieldLabel.substring(0, 1).toUpperCase(Locale.ROOT) + fieldLabel.substring(1);
            if(result instanceof HasLabel)
                ((HasLabel) result).setLabel(fieldLabel);
            else ((com.vaadin.flow.component.HasLabel) result).setLabel(fieldLabel);
            LoggerFactory.getLogger(this.getClass()).info("default label for {} ({}): {}", property.getName(), result.getClass().getSimpleName(), fieldLabel);
        }
        return result;
    }

    public <P, C extends Component & HasValue<?, P>> void registerType(Class<P> valueType, SerializableSupplier<C> componentSupplier) {
        this.typeBuilders.put(valueType, (ComponentBuilder<P>) def -> componentSupplier.get());
    }

    public final <P, C extends Component & HasValue<?, P>> SimplePropertyComponentFactory withRegisteredType(Class<P> valueType, SerializableSupplier<C> componentSupplier) {
        this.registerType(valueType, componentSupplier);
        return this;
    }

    public <P> void registerBuilder(Class<P> valueType, ComponentBuilder<P> builder) {
        this.typeBuilders.put(valueType, builder);
    }

    public final <P> SimplePropertyComponentFactory withRegisteredBuilder(Class<P> valueType, ComponentBuilder<P> builder) {
        this.registerBuilder(valueType, builder);
        return this;
    }

    public void unregisterType(Class<?> valueType) {
        this.typeBuilders.remove(valueType);
    }

    public boolean isDefaultLabel() {
        return defaultLabel;
    }

    public void setDefaultLabel(boolean defaultLabel) {
        this.defaultLabel = defaultLabel;
    }

    public final SimplePropertyComponentFactory withDefaultLabel() {
        return this.withDefaultLabel(true);
    }

    public final SimplePropertyComponentFactory withoutDefaultLabel() {
        return this.withDefaultLabel(false);
    }

    public final SimplePropertyComponentFactory withDefaultLabel(boolean state) {
        this.setDefaultLabel(state);
        return this;
    }

}
