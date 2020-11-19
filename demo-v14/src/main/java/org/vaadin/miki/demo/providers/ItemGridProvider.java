package org.vaadin.miki.demo.providers;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.superfields.dates.SuperDatePicker;
import org.vaadin.miki.superfields.dates.SuperDateTimePicker;
import org.vaadin.miki.superfields.itemgrid.ItemGrid;
import org.vaadin.miki.superfields.lazyload.ComponentObserver;
import org.vaadin.miki.superfields.lazyload.LazyLoad;
import org.vaadin.miki.superfields.lazyload.ObservedField;
import org.vaadin.miki.superfields.numbers.SuperBigDecimalField;
import org.vaadin.miki.superfields.numbers.SuperDoubleField;
import org.vaadin.miki.superfields.numbers.SuperIntegerField;
import org.vaadin.miki.superfields.numbers.SuperLongField;
import org.vaadin.miki.superfields.tabs.SuperTabs;
import org.vaadin.miki.superfields.unload.UnloadObserver;

/**
 * Provides an {@link ItemGrid}.
 * @author miki
 * @since 2020-11-18
 */
public class ItemGridProvider implements ComponentProvider<ItemGrid<Class<? extends Component>>> {
    @Override
    public ItemGrid<Class<? extends Component>> getComponent() {
        return new ItemGrid<Class<? extends Component>>(
                null,
                () -> {
                    VerticalLayout result = new VerticalLayout();
                    result.setSpacing(true);
                    result.setPadding(true);
                    result.setAlignItems(FlexComponent.Alignment.STRETCH);
                    result.setWidthFull();
                    return result;
                },
                ItemGridGenerators::generateParagraph,
                event -> {
                    if (event.isSelected())
                        event.getCellInformation().getComponent().getElement().getClassList().add("selected");
                    else event.getCellInformation().getComponent().getElement().getClassList().remove("selected");
                },
                SuperIntegerField.class, SuperLongField.class, SuperDoubleField.class,
                SuperBigDecimalField.class, SuperDatePicker.class, SuperDateTimePicker.class,
                SuperTabs.class, LazyLoad.class, ObservedField.class,
                ComponentObserver.class, UnloadObserver.class, ItemGrid.class
        )
                .withRowComponentGenerator(rowNumber -> {
                    HorizontalLayout result = new HorizontalLayout();
                    result.setSpacing(true);
                    result.setAlignItems(FlexComponent.Alignment.CENTER);
                    result.setPadding(true);
                    return result;
                });
    }
}
