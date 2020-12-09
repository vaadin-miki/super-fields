package org.vaadin.miki.superfields.gridselect;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.grid.Grid;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithItemsMixin;
import org.vaadin.miki.markers.WithValueMixin;

import java.util.Collection;
import java.util.Set;

/**
 * A multi-selection {@link GridSelect}. Holds a value that is a {@link Set}<{@code V}>.
 * @param <V> Value to store in the {@link Grid}.
 * @author miki
 * @since 2020-12-09
 */
public class GridMultiSelect<V> extends AbstractGridSelect<V, Set<V>>
        implements WithIdMixin<GridMultiSelect<V>>, WithItemsMixin<V, GridMultiSelect<V>>,
        WithValueMixin<AbstractField.ComponentValueChangeEvent<CustomField<Set<V>>, Set<V>>, Set<V>, GridMultiSelect<V>> {

    /**
     * Creates the component.
     * This requires a subsequent configuration of grid's columns.
     * @param items Items to add to the grid.
     * @see #getGrid()
     */
    @SafeVarargs
    public GridMultiSelect(V... items) {
        super(new RestrictedModeGrid<>(Grid.SelectionMode.MULTI));
        this.setItems(items);
    }

    /**
     * Constructs the component. This is the recommended constructor.
     * @param type Type of items displayed in the grid.
     * @param createColumns Whether or not to create default columns.
     * @param items Items to add to the grid.
     */
    @SafeVarargs
    public GridMultiSelect(Class<V> type, boolean createColumns, V... items) {
        super(new RestrictedModeGrid<>(type, createColumns, Grid.SelectionMode.MULTI));
        this.setItems(items);
    }

    /**
     * More advanced constructor that allows using predefined grid.
     * It is not public, as usage of this constructor implies you know what you are doing.
     * @param underlyingGrid A grid to use.
     */
    protected GridMultiSelect(Grid<V> underlyingGrid) {
        super(underlyingGrid);
    }

    @Override
    protected Set<V> generateModelValue() {
        return this.getGrid().getSelectedItems();
    }

    @Override
    protected void setPresentationValue(Set<V> vs) {
        // one way is casting grid's selection model to GridMultiSelectionModel<V>
        // another is the one below
        this.getGrid().asMultiSelect().updateSelection(vs, this.getGrid().getSelectedItems());
    }

    @Override
    public void setItems(Collection<V> collection) {
        this.getGrid().setItems(collection);
    }
}
