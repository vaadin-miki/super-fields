package org.vaadin.miki.superfields.gridselect;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.selection.SelectionEvent;
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
public class GridSelect<V> extends CustomField<V>
        implements WithIdMixin<GridSelect<V>>, WithItemsMixin<V, GridSelect<V>>,
                   WithValueMixin<AbstractField.ComponentValueChangeEvent<CustomField<V>, V>, V, GridSelect<V>> {

    private final Grid<V> grid;

    /**
     * Creates the component.
     * This requires a subsequent configuration of grid's columns.
     * @param items Items to add to the grid.
     * @see #getGrid()
     */
    @SafeVarargs
    public GridSelect(V... items) {
        this(new RestrictedModeGrid<>());
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
        this(new RestrictedModeGrid<>(type, createColumns));
        this.setItems(items);
    }

    /**
     * More advanced constructor that allows using predefined grid.
     * It is not public, as usage of this constructor implies you know what you are doing.
     * @param underlyingGrid A grid to use.
     */
    protected GridSelect(Grid<V> underlyingGrid) {
        this.grid = underlyingGrid;
        this.configureGrid(this.grid);
        this.setSizeFull();
    }

    /**
     * Configures the grid.
     * @param grid Grid to configure.
     */
    protected void configureGrid(Grid<V> grid) {
        this.add(grid);

        this.grid.addSelectionListener(this::onGridSelected);
        this.grid.addFocusListener(gridFocusEvent -> this.fireEvent(new FocusEvent<>(this, gridFocusEvent.isFromClient())));
        this.grid.addBlurListener(gridBlurEvent -> this.fireEvent(new BlurEvent<>(this, gridBlurEvent.isFromClient())));

        // this js snippet courtesy of Tomi Virkki
        // prevents selection on-click and on-space-pressed, basically turning the grid into read-only when needed
        this.grid.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this.grid, executionContext ->
                this.grid.getElement().executeJs("this.$.table.addEventListener('click', function(e) {this.preventSelection && e.stopPropagation()}.bind(this)); " +
                        "this.$.table.addEventListener('keydown', function(e){this.preventSelection && e.keyCode === 32 && e.stopPropagation()}.bind(this));")));

        grid.addClassName("grid-select-inner-grid");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
    }

    private void onGridSelected(SelectionEvent<Grid<V>, V> event) {
        this.updateValue();
    }

    /**
     * Returns the underlying {@link Grid}. Use with caution. Please do not mess with grid's selection.
     * Note: unless a protected constructor with predefined grid - {@link GridSelect(Grid)} - is called, the returned grid will be a {@link RestrictedModeGrid}.
     * @return The {@link Grid}. Any changes to the grid will affect this component.
     */
    public Grid<V> getGrid() {
        return this.grid;
    }

    @Override
    protected V generateModelValue() {
        final Set<V> items = this.grid.getSelectedItems();
        return items.isEmpty() ? null : items.iterator().next();
    }

    @Override
    protected void setPresentationValue(V v) {
        this.grid.select(v);
    }

    @Override
    public void setItems(Collection<V> collection) {
        this.grid.setItems(collection);
        this.grid.recalculateColumnWidths();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        this.grid.getElement().setProperty("preventSelection", readOnly);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.grid.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return this.grid.isEnabled();
    }

}
