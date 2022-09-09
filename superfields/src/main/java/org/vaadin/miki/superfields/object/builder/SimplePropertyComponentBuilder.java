package org.vaadin.miki.superfields.object.builder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.function.SerializableSupplier;
import org.slf4j.LoggerFactory;
import org.vaadin.miki.markers.HasLabel;
import org.vaadin.miki.superfields.object.Property;
import org.vaadin.miki.superfields.object.PropertyComponentBuilder;
import org.vaadin.miki.superfields.text.LabelField;
import org.vaadin.miki.util.StringTools;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Reference implementation of {@link PropertyComponentBuilder}.
 * In general, it allows building components based on {@link Predicate}<{@link Property}> and provides some helper methods for common use cases.
 *
 * @author miki
 * @since 2022-06-06
 */
@SuppressWarnings("squid:S1452") // not really sure how to properly remove <?> in return types and allow type safety at the same time; any help appreciated
public class SimplePropertyComponentBuilder implements PropertyComponentBuilder {

    /**
     * Default field builder used when no registered predicate matches given property.
     * It always produces a result.
     */
    public static final FieldBuilder<?> DEFAULT_BUILDER = property -> new LabelField<>();

    /**
     * Returns a {@link Predicate} that checks if a property is exactly of given type.
     * @param type Type to check.
     * @return A {@link Predicate} that checks if the given {@code type} is equal to the property's type.
     */
    public static Predicate<Property<?, ?>> isExactlyType(Class<?> type) {
        return def -> Objects.equals(type, def.getType());
    }

    /**
     * Returns a {@link Predicate} that checks if a property is of the given type (i.e. is a subclass or implementation of it).
     * @param type Type to check.
     * @return A {@link Predicate} that evaluates to {@code type.isAssignableFrom(property.getType())}.
     */
    public static Predicate<Property<?, ?>> isOfType(Class<?> type) {
        return def -> type.isAssignableFrom(def.getType());
    }

    private final Map<Predicate<Property<?, ?>>, FieldBuilder<?>> registeredBuilders = new LinkedHashMap<>();

    private FieldBuilder<?> defaultBuilder = DEFAULT_BUILDER;

    private boolean defaultLabel = true;

    @SuppressWarnings("unchecked") // fine, I guess?
    @Override
    public <P, C extends Component & HasValue<?, P>> Optional<C> buildPropertyField(Property<?, P> property) {
        final C result;

        final FieldBuilder<P> fieldBuilder = this.registeredBuilders.keySet().stream()
                .filter(predicate -> predicate.test(property))
                .findFirst()
                .map(this.registeredBuilders::get)
                .map(builder -> (FieldBuilder<P>) builder)
                .orElse((FieldBuilder<P>) this.defaultBuilder);
        result = (C) fieldBuilder.buildPropertyField(property);

        if((result instanceof HasLabel || result instanceof com.vaadin.flow.component.HasLabel) && this.isDefaultLabel()) {
            final String fieldLabel = StringTools.humanReadable(property.getName());
            if(result instanceof HasLabel)
                ((HasLabel) result).setLabel(fieldLabel);
            else ((com.vaadin.flow.component.HasLabel) result).setLabel(fieldLabel);
            LoggerFactory.getLogger(this.getClass()).info("default label for {} ({}): {}", property.getName(), result.getClass().getSimpleName(), fieldLabel);
        }
        return Optional.ofNullable(result);
    }

    /**
     * Returns the builders registered so far. Modifications of the returned map will affect this object.
     * @return A non-{@code null}, but possibly empty, {@link Map} of registered {@link FieldBuilder}s with matching {@link Predicate}s.
     */
    public Map<Predicate<Property<?, ?>>, FieldBuilder<?>> getRegisteredBuilders() {
        return registeredBuilders;
    }

    /**
     * Registers a builder for a given {@link Component} for properties of exactly the given {@code valueType}.
     * @param valueType Type of the property.
     * @param componentSupplier Supplier of component capable of displaying value of the given type.
     * @param <P> Value type.
     * @param <C> Component type.
     */
    @SuppressWarnings("unchecked") // should be fine
    public <P, C extends Component & HasValue<?, P>> void registerType(Class<P> valueType, SerializableSupplier<C> componentSupplier) {
        this.registerBuilder((Predicate<Property<?, P>>)(Predicate<?>) isExactlyType(valueType), def -> componentSupplier.get());
    }

    /**
     * Chains {@link #registerType(Class, SerializableSupplier)} and returns itself.
     * @param valueType Type of the property.
     * @param componentSupplier Supplier of component capable of displaying value of the given type.
     * @return This.
     * @param <P> Value type.
     * @param <C> Component type.
     * @see #registerType(Class, SerializableSupplier)
     */
    public final <P, C extends Component & HasValue<?, P>> SimplePropertyComponentBuilder withRegisteredType(Class<P> valueType, SerializableSupplier<C> componentSupplier) {
        this.registerType(valueType, componentSupplier);
        return this;
    }

    /**
     * Registers a given {@code builder} with a predicate that checks for a given (not exact) type.
     * @param valueType Type to check.
     * @param builder Builder to register.
     * @param <P> Property value type.
     */
    @SuppressWarnings("unchecked") // should be fine
    public <P> void registerBuilder(Class<P> valueType, FieldBuilder<P> builder) {
        this.registerBuilder((Predicate<Property<?, P>>) (Predicate<?>) isOfType(valueType), builder);
    }

    /**
     * Registers a given {@link Predicate} and its matching {@link FieldBuilder}.
     * @param predicate Predicate.
     * @param builder Builder.
     * @param <P> Property value type.
     */
    @SuppressWarnings("unchecked") // should be fine
    public <P> void registerBuilder(Predicate<Property<?, P>> predicate, FieldBuilder<P> builder) {
        this.registeredBuilders.put((Predicate<Property<?,?>>) (Predicate<?>) predicate, builder);
    }

    /**
     * Chains {@link #registerBuilder(Class, FieldBuilder)} and returns itself.
     * @param valueType Value type to check.
     * @param builder Builder.
     * @return This.
     * @param <P> Property value type.
     * @see #registerBuilder(Class, FieldBuilder)
     */
    public final <P> SimplePropertyComponentBuilder withRegisteredBuilder(Class<P> valueType, FieldBuilder<P> builder) {
        this.registerBuilder(valueType, builder);
        return this;
    }

    /**
     * Chains {@link #registerBuilder(Predicate, FieldBuilder)} and returns itself.
     * @param predicate Predicate to check.
     * @param builder Builder.
     * @return This.
     * @param <P> Property value type.
     * @see #registerBuilder(Predicate, FieldBuilder)
     */
    public final <P> SimplePropertyComponentBuilder withRegisteredBuilder(Predicate<Property<?, P>> predicate, FieldBuilder<P> builder) {
        this.registerBuilder(predicate, builder);
        return this;
    }

    /**
     * Checks if the produced component should have its label set to default.
     * @return When {@code true} (default), the created component will have its label set to human-readable version of the property name, if it is possible.
     */
    public boolean isDefaultLabel() {
        return defaultLabel;
    }

    /**
     * Controls whether the produced component should have its label set to default.
     * @param defaultLabel When {@code true}, the created component will have its label set to human-readable version of the property name, if it is possible.
     */
    public void setDefaultLabel(boolean defaultLabel) {
        this.defaultLabel = defaultLabel;
    }

    /**
     * Chains {@link #setDefaultLabel(boolean)} with {@code true} and returns itself.
     * @return This.
     * @see #setDefaultLabel(boolean)
     * @see #withoutDefaultLabel()
     * @see #withDefaultLabel(boolean)
     */
    public final SimplePropertyComponentBuilder withDefaultLabel() {
        return this.withDefaultLabel(true);
    }

    /**
     * Chains {@link #setDefaultLabel(boolean)} with {@code false} and returns itself.
     * @return This.
     * @see #setDefaultLabel(boolean)
     * @see #withDefaultLabel()
     * @see #withDefaultLabel(boolean)
     */
    public final SimplePropertyComponentBuilder withoutDefaultLabel() {
        return this.withDefaultLabel(false);
    }

    /**
     * Chains {@link #setDefaultLabel(boolean)} and returns itself.
     * @param state When {@code true}, the created component will have its label set to human-readable version of the property name, if it is possible.
     * @return This.
     */
    public final SimplePropertyComponentBuilder withDefaultLabel(boolean state) {
        this.setDefaultLabel(state);
        return this;
    }

    /**
     * Sets a new default {@link FieldBuilder}, i.e. the builder used when none of the registered builders are suitable.
     * @param defaultBuilder New builder. When {@code null} is passed, {@link #DEFAULT_BUILDER} will be used instead.
     */
    public void setDefaultBuilder(FieldBuilder<?> defaultBuilder) {
        this.defaultBuilder = Objects.requireNonNullElse(defaultBuilder, DEFAULT_BUILDER);
    }

    /**
     * Returns the current default {@link FieldBuilder}, i.e. the builder used when none of the registered builders are suitable.
     * @return The current default {@link FieldBuilder}. Never {@code null}.
     */
    public final FieldBuilder<?> getDefaultBuilder() {
        return defaultBuilder;
    }

    /**
     * Chains {@link #setDefaultBuilder(FieldBuilder)} and returns itself.
     * @param builder Builder to use.
     * @return This.
     * @see #setDefaultBuilder(FieldBuilder)
     */
    public final SimplePropertyComponentBuilder withDefaultBuilder(FieldBuilder<?> builder) {
        this.setDefaultBuilder(builder);
        return this;
    }
}
