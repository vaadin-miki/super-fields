package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.markers.HasLabelPositionable;
import org.vaadin.miki.shared.labels.LabelPosition;

import java.util.function.Consumer;

/**
 * @author miki
 * @since 2022-09-23
 */
@Order(33)
public class HasPositionableLabelBuilder implements ContentBuilder<HasLabelPositionable> {
    @Override
    public void buildContent(HasLabelPositionable component, Consumer<Component[]> callback) {
        final ComboBox<LabelPosition> positions = new ComboBox<>("Pick label position:", LabelPosition.values());
        positions.setAllowCustomValue(false);
        positions.addValueChangeListener(event -> component.setLabelPosition(event.getValue()));
        callback.accept(new Component[]{positions});
    }
}
