package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.numbers.AbstractSuperNumberField;

import java.util.function.Consumer;

/**
 * Builds content for {@link AbstractSuperNumberField}.
 * @author miki
 * @since 2020-11-19
 */
@Order(30)
@SuppressWarnings("squid:S5411") // no way around boxed values
public class AbstractSuperNumberFieldBuilder implements ContentBuilder<AbstractSuperNumberField<?, ?>> {

    @Override
    public void buildContent(AbstractSuperNumberField<?, ?> component, Consumer<Component[]> callback) {
        final Checkbox autoselect = new Checkbox("Select automatically on focus?");
        autoselect.addValueChangeListener(event -> component.setAutoselect(event.getValue()));

        final Checkbox separatorHidden = new Checkbox("Hide grouping separator on focus?");
        separatorHidden.addValueChangeListener(event -> component.setGroupingSeparatorHiddenOnFocus(event.getValue()));

        final Checkbox prefix = new Checkbox("Show prefix component?");
        prefix.addValueChangeListener(event -> component.setPrefixComponent(
                event.getValue() ? new Span(">") : null
        ));

        final Checkbox suffix = new Checkbox("Show suffix component?");
        suffix.addValueChangeListener(event -> component.setSuffixComponent(
                event.getValue() ? new Span("€") : null
        ));

        final Checkbox alignRight = new Checkbox("Align text to the right?");
        alignRight.addValueChangeListener(event -> {
                    if(event.getValue())
                        component.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
                    else
                        component.removeThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
                }
        );
        callback.accept(new Component[]{autoselect, separatorHidden, prefix, suffix, alignRight});
    }
}
