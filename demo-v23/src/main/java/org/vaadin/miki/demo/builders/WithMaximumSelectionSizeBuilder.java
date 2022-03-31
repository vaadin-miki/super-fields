package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.markers.HasMaximumSelectionSize;
import org.vaadin.miki.markers.WithMaximumSelectionSizeMixin;

import java.util.function.Consumer;

/**
 * Builds content for {@link WithMaximumSelectionSizeMixin}.
 * @author miki
 * @since 2020-12-09
 */
@Order(160)
public class WithMaximumSelectionSizeBuilder implements ContentBuilder<WithMaximumSelectionSizeMixin<?>> {

    @Override
    public void buildContent(WithMaximumSelectionSizeMixin<?> component, Consumer<Component[]> callback) {
        final ComboBox<Integer> selectionSize = new ComboBox<>("Maximum allowed selection size:",
                HasMaximumSelectionSize.UNLIMITED, 1, 2, 3, 4, 5, 6);
        selectionSize.addValueChangeListener(event -> component.setMaximumSelectionSize(event.getValue()));
        selectionSize.setWidth("350px");
        selectionSize.setHelperText("0 means unlimited selection");
        callback.accept(new Component[]{selectionSize});
    }
}
