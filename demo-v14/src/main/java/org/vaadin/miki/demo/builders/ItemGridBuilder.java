package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.providers.ItemGridGenerators;
import org.vaadin.miki.superfields.itemgrid.ItemGrid;

import java.util.function.Consumer;

/**
 * Builds content for {@link ItemGrid}.
 * @author miki
 * @since 2020-11-19
 */
@SuppressWarnings("squid:S5411")
public class ItemGridBuilder implements ContentBuilder<ItemGrid<?>> {

    @Override
    public void buildContent(ItemGrid<?> component, Consumer<Component[]> callback) {
        final RadioButtonGroup<Integer> buttons = new RadioButtonGroup<>();
        buttons.setItems(1, 2, 3, 4, 5, 6);
        buttons.setLabel("Number of columns:");
        buttons.setValue(ItemGrid.DEFAULT_COLUMN_COUNT);
        buttons.addValueChangeListener(event -> component.setColumnCount(event.getValue()));

        @SuppressWarnings("unchecked")
        final Checkbox alternate = new Checkbox("Display lazy loading cells?", event ->
                ((ItemGrid<Class<? extends Component>>)component).setCellGenerator(
                        event.getValue() ? ItemGridGenerators::generateDiv : ItemGridGenerators::generateParagraph
                )
        );

        callback.accept(new Component[]{buttons, alternate});
    }
}
