package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.demo.providers.ItemGridGenerators;
import org.vaadin.miki.superfields.itemgrid.ItemGrid;
import org.vaadin.miki.superfields.itemgrid.RowPaddingStrategies;
import org.vaadin.miki.superfields.itemgrid.RowPaddingStrategy;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Builds content for {@link ItemGrid}.
 * @author miki
 * @since 2020-11-19
 */
@Order(60)
@SuppressWarnings("squid:S5411") // no way around boxed values
public class ItemGridBuilder implements ContentBuilder<ItemGrid<?>> {

    private final Map<RowPaddingStrategy, String> strategyCaptions = new LinkedHashMap<>();

    public ItemGridBuilder() {
        this.strategyCaptions.put(RowPaddingStrategies.NO_PADDING, "No padding");
        this.strategyCaptions.put(RowPaddingStrategies.FIRST_ROW_FILL_BEGINNING, "Pad first row at beginning");
        this.strategyCaptions.put(RowPaddingStrategies.LAST_ROW_CENTRE_BEGINNING, "Last row centred");
        this.strategyCaptions.put(RowPaddingStrategies.LAST_ROW_FILL_END, "Pad last row at end");
    }

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

        final ComboBox<RowPaddingStrategy> rowPaddingStrategies = new ComboBox<>("Select row padding strategy:", this.strategyCaptions.keySet());
        rowPaddingStrategies.setItemLabelGenerator(this.strategyCaptions::get);
        rowPaddingStrategies.setHelperText("(row padding strategy decides where to draw empty cells, if at all)");
        rowPaddingStrategies.setWidth("350px");
        rowPaddingStrategies.setValue(RowPaddingStrategies.NO_PADDING);
        rowPaddingStrategies.addValueChangeListener(event -> component.setRowPaddingStrategy(event.getValue()));

        callback.accept(new Component[]{buttons, alternate, rowPaddingStrategies});
    }
}
