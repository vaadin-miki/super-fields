package org.vaadin.miki.demo.providers;

import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.gridselect.GridSelect;

/**
 * Provides {@link GridSelect}.
 * @author miki
 * @since 2020-11-18
 */
@Order(120)
public class GridSelectProvider implements ComponentProvider<GridSelect<SuperFieldsGridItem>> {

    @Override
    public GridSelect<SuperFieldsGridItem> getComponent() {
        final GridSelect<SuperFieldsGridItem> gridSelect = new GridSelect<>(SuperFieldsGridItem.class, true, SuperFieldsGridItem.getAllRegisteredProviders());
        gridSelect.getGrid().getColumnByKey("nameLength").setAutoWidth(true);
        return gridSelect;
    }
}
