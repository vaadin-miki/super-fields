package org.vaadin.miki.superfields.gridselect;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.selection.SelectionEvent;

/**
 * Base class for various {@link Grid} selects.
 * @param <V> Type of data in the grid.
 * @param <F> Type of data in the field.
 * @author miki
 * @since 2020-12-09
 */
@CssImport(value = "./styles/label-positions-custom-field.css", themeFor = "vaadin-custom-field")
@CssImport(value = "./styles/label-positions-grids.css", themeFor = "vaadin-custom-field")
public abstract class AbstractGridSelect<V, F> extends CustomField<F> {

    private final Grid<V> grid;

    protected AbstractGridSelect(Grid<V> underlyingGrid) {
        this.grid = underlyingGrid;
        this.configureGrid(this.grid);
        this.setSizeFull();
    }

    /**
     * Configures the grid.
     *
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
    }

    /**
     * This method is called when a grid cell is selected in the client.
     * By default, it calls {@link #updateValue()}.
     * @param event Event with selection details.
     */
    protected void onGridSelected(SelectionEvent<Grid<V>, V> event) {
        this.updateValue();
    }

    /**
     * Returns the underlying {@link Grid}. Use with caution. Please do not mess with grid's selection.
     * Note: unless a protected constructor with predefined grid - {@link GridSelect#GridSelect(Grid)} - is called, the returned grid will be a {@link RestrictedModeGrid}.
     * @return The {@link Grid}. Any changes to the grid will affect this component.
     */
    public Grid<V> getGrid() {
        return this.grid;
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
