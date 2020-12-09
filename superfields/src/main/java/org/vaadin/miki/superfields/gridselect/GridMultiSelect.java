package org.vaadin.miki.superfields.gridselect;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.grid.Grid;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithItemsMixin;
import org.vaadin.miki.markers.WithMaximumSelectionSize;
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
        WithMaximumSelectionSize<GridMultiSelect<V>>,
        WithValueMixin<AbstractField.ComponentValueChangeEvent<CustomField<Set<V>>, Set<V>>, Set<V>, GridMultiSelect<V>> {

    private int maximumSelectionSize = UNLIMITED;

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
    protected void configureGrid(Grid<V> grid) {
        super.configureGrid(grid);
        // below JS magic courtesy of Diego Cardoso
        grid.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(grid, context ->
                grid.getElement().executeJs("$0.addEventListener('selected-items-changed', " +
                        "({target: grid}) => {if (grid.selectedItems.length > grid.maximumSelectionSize) {" +
                        "grid.splice('selectedItems', -1);" +
                        "}});")
        ));
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

    @Override
    public void setMaximumSelectionSize(int maximumSelectionSize) {
        this.maximumSelectionSize = maximumSelectionSize;
        this.getGrid().getElement().setProperty("maximumSelectionSize", maximumSelectionSize);
    }

    @Override
    public int getMaximumSelectionSize() {
        return maximumSelectionSize;
    }

}
