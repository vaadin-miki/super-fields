package org.vaadin.miki.superfields.variant;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.function.SerializableSupplier;

import java.util.Optional;

/**
 * Marker interface for objects that provide components based on a given value.
 *
 * @param <T> Type of value.
 * @param <C> Returned component.
 *
 * @author miki
 * @since 2022-04-11
 */
@FunctionalInterface
public interface TypedFieldProvider<T, C extends Component & HasValue<?, T>> {

    /**
     * Produces a component for a given value.
     * The returned component should be able to accept that object of the given type when its {@code setValue()} method is called.
     *
     * @param valueType Type of the value. Never {@code null}.
     * @return Perhaps a {@link Component}. The result will be empty when this provider cannot produce a result for a given value.
     */
    Optional<C> provideComponent(Class<?> valueType);

    /**
     * Produces a {@link TypedFieldProvider} for a given type and component.
     * @param type Type to accept.
     * @param supplier Component to produce.
     * @param <V> Value type.
     * @param <W> Component type.
     * @return If asked type is a subtype of the given type returns the component, otherwise returns an empty optional.
     */
    static <V, W extends Component & HasValue<?, V>> TypedFieldProvider<V, W> of(Class<V> type, SerializableSupplier<W> supplier) {
        return valueType -> type.isAssignableFrom(valueType) ? Optional.of(supplier.get()) : Optional.empty();
    }

}
