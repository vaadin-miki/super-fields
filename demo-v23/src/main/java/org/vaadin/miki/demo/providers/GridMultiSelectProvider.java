package org.vaadin.miki.demo.providers;

import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.gridselect.GridMultiSelect;

/**
 * Provides a {@link GridMultiSelect}.
 * @author miki
 * @since 2020-12-09
 */
@Order(125)
public class GridMultiSelectProvider implements ComponentProvider<GridMultiSelect<SuperFieldsGridItem>> {

    @Override
    public GridMultiSelect<SuperFieldsGridItem> getComponent() {
        final GridMultiSelect<SuperFieldsGridItem> gridSelect = new GridMultiSelect<>(SuperFieldsGridItem.class, true, SuperFieldsGridItem.getAllRegisteredProviders())
                .withHelperText("(you can select multiple rows)")
                .withLabel("Choose as many as you like:")
                ;
        gridSelect.getGrid().getColumnByKey("nameLength").setAutoWidth(true);
        return gridSelect;
    }
}
