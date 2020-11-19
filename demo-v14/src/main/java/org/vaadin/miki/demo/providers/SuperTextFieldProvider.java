package org.vaadin.miki.demo.providers;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.superfields.text.SuperTextField;

/**
 * Provides a {@link SuperTextField}.
 * @author miki
 * @since 2020-11-18
 */
public class SuperTextFieldProvider implements ComponentProvider<SuperTextField>, Validator<String> {

    @Override
    public SuperTextField getComponent() {
        return new SuperTextField("Type something:").withPlaceholder("(nothing typed)").withId("super-text-field").withHelperText("(anything without a ! goes)");
    }

    @Override
    public ValidationResult apply(String s, ValueContext valueContext) {
        return s.contains("!") ? ValidationResult.error("! is not allowed") : ValidationResult.ok();
    }
}
