package org.vaadin.miki.superfields.object;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.miki.superfields.object.builder.FieldBuilder;

public class TextFieldBuilder implements FieldBuilder<String> {

    public static final String TITLE_TEXT = "I am created in a builder!";

    @Override
    public HasValue<?, String> buildPropertyField(Property<?, String> property) {
        final TextField result = new TextField();
        result.setTitle(TITLE_TEXT);
        return result;
    }
}
