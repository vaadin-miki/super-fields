package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.variant.VariantField;

import java.time.LocalDate;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Builds content for {@link VariantField}.
 *
 * @author miki
 * @since 2022-04-11
 */
@Order(80)
public class VariantFieldBuilder implements ContentBuilder<VariantField> {

    @Override
    public void buildContent(VariantField component, Consumer<Component[]> callback) {
        final HorizontalLayout layout = new HorizontalLayout(
                Stream.of(new Object[]{"LocalDate", LocalDate.now()},
                                new Object[]{"number (Integer)", 42},
                                new Object[]{"String", "Pay no mind to the distant thunder"})
                        .map(data -> new Button("Set a " + data[0].toString(), event -> component.setValue(data[1])))
                        .toArray(Component[]::new)
        );
        layout.add(new Button("Set null (clear value)", event -> component.clear()));
        callback.accept(new Component[]{layout});
    }

}
