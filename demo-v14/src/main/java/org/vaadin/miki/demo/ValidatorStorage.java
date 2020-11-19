package org.vaadin.miki.demo;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.binder.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Stores component-specific {@link Validator}s.
 * @author miki
 * @since 2020-11-18
 */
public class ValidatorStorage {

    private final Map<HasValue<?, ?>, Validator<?>> validators = new HashMap<>();

    public <V, C extends HasValue<?, V>> void registerValidator(C instance, Validator<V> validator) {
        this.validators.put(instance, validator);
    }

    public <V, C extends HasValue<?, V>> Consumer<Validator<V>> registerValidator(final C instance) {
        return validator -> this.validators.put(instance, validator);
    }

    public boolean isValidatorPresent(HasValue<?, ?> component) {
        return this.validators.containsKey(component);
    }

    @SuppressWarnings("unchecked")
    public <V, C extends HasValue<?, V>> Validator<V> getValidator(C instance) {
        if(this.isValidatorPresent(instance))
            return (Validator<V>)this.validators.get(instance);
        else return null;
    }

}
