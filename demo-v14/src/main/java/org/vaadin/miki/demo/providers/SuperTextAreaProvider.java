package org.vaadin.miki.demo.providers;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.superfields.text.SuperTextArea;

/**
 * Provides {@link SuperTextArea}.
 * @author miki
 * @since 2020-11-17
 */
public class SuperTextAreaProvider implements ComponentProvider<SuperTextArea>, Validator<String> {
    @Override
    public SuperTextArea getComponent() {
        return new SuperTextArea("Type a lot of something:").withPlaceholder("(nothing typed)").withId("super-text-area").withHelperText("(anything without ? goes)");
    }

    @Override
    public ValidationResult apply(String s, ValueContext valueContext) {
        return s.contains("?") ? ValidationResult.error("? is not allowed") : ValidationResult.ok();
    }
}
