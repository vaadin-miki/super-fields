package org.vaadin.miki.demo.providers;

import org.atteo.classindex.ClassIndex;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.gridselect.GridSelect;

import java.util.stream.StreamSupport;

/**
 * Provides {@link GridSelect}.
 * @author miki
 * @since 2020-11-18
 */
@Order(120)
public class GridSelectProvider implements ComponentProvider<GridSelect<SuperFieldsGridItem>> {

    @Override
    public GridSelect<SuperFieldsGridItem> getComponent() {
        final GridSelect<SuperFieldsGridItem> gridSelect = new GridSelect<>(SuperFieldsGridItem.class, true,
                // scans all providers (as that should be all showcased components) and adds them to the grid
                StreamSupport.stream(ClassIndex.getSubclasses(ComponentProvider.class).spliterator(), false)
                    .filter(type -> !type.isInterface())
                    .map(Class::getSimpleName)
                    .map(s -> s.endsWith("Provider") ? s.substring(0, s.length()-8) : s)
                    .map(SuperFieldsGridItem::new)
                    .toArray(SuperFieldsGridItem[]::new));
        gridSelect.getGrid().getColumnByKey("nameLength").setAutoWidth(true);
        return gridSelect;
    }
}
