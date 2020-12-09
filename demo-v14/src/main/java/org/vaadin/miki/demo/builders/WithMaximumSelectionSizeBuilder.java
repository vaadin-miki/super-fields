package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.markers.HasMaximumSelectionSize;
import org.vaadin.miki.markers.WithMaximumSelectionSize;

import java.util.function.Consumer;

/**
 * Builds content for {@link WithMaximumSelectionSize}.
 * @author miki
 * @since 2020-12-09
 */
@Order(160)
public class WithMaximumSelectionSizeBuilder implements ContentBuilder<WithMaximumSelectionSize<?>> {

    @Override
    public void buildContent(WithMaximumSelectionSize<?> component, Consumer<Component[]> callback) {
        final ComboBox<Integer> selectionSize = new ComboBox<>("Maximum allowed selection size:",
                HasMaximumSelectionSize.UNLIMITED, 1, 2, 3, 4, 5, 6);
        selectionSize.addValueChangeListener(event -> component.setMaximumSelectionSize(event.getValue()));
        callback.accept(new Component[]{selectionSize});
    }
}
