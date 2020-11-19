package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.markers.WithNullValueOptionallyAllowed;

import java.util.function.Consumer;

/**
 * Builds content for {@link WithNullValueOptionallyAllowed}.
 * @author miki
 * @since 2020-11-19
 */
public class WithNullValueOptionallyAllowedBuilder implements ContentBuilder<WithNullValueOptionallyAllowed<?, ?, ?>> {

    @Override
    public void buildContent(WithNullValueOptionallyAllowed<?, ?, ?> component, Consumer<Component[]> callback) {
        final Checkbox allow = new Checkbox("Allow empty string as null value?", event -> component.setNullValueAllowed(event.getValue()));
        callback.accept(new Component[]{allow});
    }
}
