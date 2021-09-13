package org.vaadin.miki.superfields.gridselect;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.data.selection.SelectionEvent;
import org.vaadin.miki.markers.WithHelperMixin;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithItemsMixin;
import org.vaadin.miki.markers.WithMaximumSelectionSize;
import org.vaadin.miki.markers.WithValueMixin;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * A multi-selection {@link GridSelect}. Holds a value that is a {@link Set}<{@code V}>.
 * @param <V> Value to store in the {@link Grid}.
 * @author miki
 * @since 2020-12-09
 */
public class GridMultiSelect<V> extends AbstractGridSelect<V, Set<V>>
        implements WithIdMixin<GridMultiSelect<V>>, WithItemsMixin<V, GridMultiSelect<V>>,
        WithMaximumSelectionSize<GridMultiSelect<V>>, WithHelperMixin<GridMultiSelect<V>>,
        WithValueMixin<AbstractField.ComponentValueChangeEvent<CustomField<Set<V>>, Set<V>>, Set<V>, GridMultiSelect<V>> {

    private int maximumSelectionSize = UNLIMITED;

    // this is here because of 'change' events being fired from the grid, even though selection is prevented
    private boolean doNotUpdateValue;

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
    protected void onGridSelected(SelectionEvent<Grid<V>, V> event) {
        // value must be updated only when then selection is not above the limit
        // however, grid also sends "change" event with the selected item, and that must be ignored as well
        this.doNotUpdateValue = !(this.getMaximumSelectionSize() <= UNLIMITED || event.getAllSelectedItems().size() <= this.getMaximumSelectionSize());
        if(!this.doNotUpdateValue)
            super.onGridSelected(event);
        // otherwise, too many items were selected
    }

    @Override
    protected void configureGrid(Grid<V> grid) {
        super.configureGrid(grid);
        // below JS magic courtesy of Diego Cardoso
        // (prevents client-side item to be selected)
        grid.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(grid, context ->
                grid.getElement().executeJs("$0.addEventListener('selected-items-changed', " +
                        "(e) => {" +
                        "const {target: grid} = e;" +
                        "if (grid.selectedItems.length > grid.maximumSelectionSize && grid.maximumSelectionSize > 0) {" +
                        "console.log('GMS: preventing selection, limit reached'); " +
                        "console.log('GMS: selected '+grid.selectedItems.length+' - limit '+grid.maximumSelectionSize); " +
                        "e.stopPropagation();" +
                        "grid.splice('selectedItems', -1);" +
                        "console.log('GMS: selected items are '+JSON.stringify(grid.selectedItems)); "+
                        "}" +
                        "});")
        ));
    }

    @Override
    protected Set<V> generateModelValue() {
        return this.getGrid().getSelectedItems();
    }

    @Override
    protected void updateValue() {
        if(!this.doNotUpdateValue)
            super.updateValue();
        else {
            // this code gets executed when selection was prevented on the client
            // so the item is not really selected, but the server-side still considers it to be selected
            // the only way to fix it is to fake a call and just deselect it
            final Set<V> value = super.getValue();
            this.getGrid().getSelectedItems().stream()
                    .filter(v -> value == null || !value.contains(v))
                    .forEach(v -> this.getGrid().getSelectionModel().deselect(v));
        }
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
        // when current selection is too much, everything will be deselected
        if(maximumSelectionSize > UNLIMITED && this.getGrid().getSelectedItems().size() > maximumSelectionSize)
            this.setValue(Collections.emptySet());
        // selecting/deselecting all makes no sense when there is a limit
        ((GridMultiSelectionModel<V>)this.getGrid().getSelectionModel())
            .setSelectAllCheckboxVisibility(maximumSelectionSize <= UNLIMITED
              ? GridMultiSelectionModel.SelectAllCheckboxVisibility.VISIBLE
              : GridMultiSelectionModel.SelectAllCheckboxVisibility.HIDDEN);
    }

    @Override
    public int getMaximumSelectionSize() {
        return maximumSelectionSize;
    }

}
