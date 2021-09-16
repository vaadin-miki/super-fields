package org.vaadin.miki.superfields.gridselect;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.grid.Grid;
import org.vaadin.miki.markers.WithHelperMixin;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithItemsMixin;
import org.vaadin.miki.markers.WithValueMixin;

import java.util.Collection;
import java.util.Set;

/**
 * A single-selection {@link Grid} that also is a value component that broadcasts value change events.
 * @param <V> Type of value to include in the grid.
 * @author miki
 * @since 2020-08-07
 */
public class GridSelect<V> extends AbstractGridSelect<V, V>
        implements WithIdMixin<GridSelect<V>>, WithItemsMixin<V, GridSelect<V>>, WithHelperMixin<GridSelect<V>>,
                   WithValueMixin<AbstractField.ComponentValueChangeEvent<CustomField<V>, V>, V, GridSelect<V>> {

    /**
     * Creates the component.
     * This requires a subsequent configuration of grid's columns.
     * @param items Items to add to the grid.
     * @see #getGrid()
     */
    @SafeVarargs
    public GridSelect(V... items) {
        super(new RestrictedModeGrid<>());
        this.setItems(items);
    }

    /**
     * Constructs the component. This is the recommended constructor.
     * @param type Type of items displayed in the grid.
     * @param createColumns Whether or not to create default columns.
     * @param items Items to add to the grid.
     */
    @SafeVarargs
    public GridSelect(Class<V> type, boolean createColumns, V... items) {
        super(new RestrictedModeGrid<>(type, createColumns));
        this.setItems(items);
    }

    /**
     * More advanced constructor that allows using predefined grid.
     * It is not public, as usage of this constructor implies you know what you are doing.
     * @param underlyingGrid A grid to use.
     */
    protected GridSelect(Grid<V> underlyingGrid) {
        super(underlyingGrid);
    }

    @Override
    protected V generateModelValue() {
        final Set<V> items = this.getGrid().getSelectedItems();
        return items.isEmpty() ? null : items.iterator().next();
    }

    @Override
    protected void setPresentationValue(V v) {
        this.getGrid().select(v);
    }

    @Override
    public void setItems(Collection<V> collection) {
        this.getGrid().setItems(collection);
        this.getGrid().recalculateColumnWidths();
    }

}
