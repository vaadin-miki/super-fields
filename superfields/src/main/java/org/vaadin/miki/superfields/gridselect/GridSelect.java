package org.vaadin.miki.superfields.gridselect;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.shared.Registration;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithItemsMixin;
import org.vaadin.miki.markers.WithValueMixin;

import java.util.Collection;
import java.util.HashSet;
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

    private final Set<Registration> registrations = new HashSet<>();

    public GridSelect() {
        this(new Grid<>());
    }

    @SafeVarargs
    public GridSelect(Class<V> type, boolean createColumns, V... items) {
        this(new Grid<>(type, createColumns));
        this.setItems(items);
    }

    protected GridSelect(Grid<V> underlyingGrid) {
        this.grid = underlyingGrid;
        this.configureGrid(this.grid);
        this.setSizeFull();
    }

    protected void configureGrid(Grid<V> grid) {
        this.add(grid);
        grid.addClassName("grid-select-inner-grid");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        this.registrations.add(grid.addSelectionListener(this::onGridSelected));
        this.registrations.add(grid.addFocusListener(gridFocusEvent -> this.fireEvent(new FocusEvent<>(this, gridFocusEvent.isFromClient()))));
        this.registrations.add(grid.addBlurListener(gridBlurEvent -> this.fireEvent(new BlurEvent<>(this, gridBlurEvent.isFromClient()))));
        super.onAttach(attachEvent);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        this.registrations.forEach(Registration::remove);
        this.registrations.clear();
        super.onDetach(detachEvent);
    }

    private void onGridSelected(SelectionEvent<Grid<V>, V> event) {
        this.updateValue();
    }

    /**
     * Returns the underlying {@link Grid}. Use with caution. Please do not mess with grid's selection.
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
        this.grid.setSelectionMode(readOnly ? Grid.SelectionMode.NONE : Grid.SelectionMode.SINGLE);
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
