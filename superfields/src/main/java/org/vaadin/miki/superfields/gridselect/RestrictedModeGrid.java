package org.vaadin.miki.superfields.gridselect;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSelectionModel;

/**
 * A grid with fixed selection mode. Changing selection mode through {@link #setSelectionMode(SelectionMode)}
 * to anything else than {@link SelectionMode#NONE} or the value defined during construction will result in
 * throwing an {@link IllegalArgumentException}.
 *
 * @param <T> Type of item in the grid.
 * @author miki
 * @since 2020-09-03
 */
public class RestrictedModeGrid<T> extends Grid<T> {

    private final SelectionMode allowedSelectionMode;

    public RestrictedModeGrid() {
        this(SelectionMode.SINGLE);
    }

    public RestrictedModeGrid(SelectionMode allowedSelectionMode) {
        super();
        this.allowedSelectionMode = allowedSelectionMode;
        this.setSelectionMode(allowedSelectionMode);
    }

    public RestrictedModeGrid(int pageSize, SelectionMode allowedSelectionMode) {
        super(pageSize);
        this.allowedSelectionMode = allowedSelectionMode;
        this.setSelectionMode(allowedSelectionMode);
    }

    public RestrictedModeGrid(Class<T> beanType, boolean autoCreateColumns, SelectionMode allowedSelectionMode) {
        super(beanType, autoCreateColumns);
        this.allowedSelectionMode = allowedSelectionMode;
        this.setSelectionMode(allowedSelectionMode);
    }

    public RestrictedModeGrid(Class<T> beanType, boolean autoCreateColumns) {
        this(beanType, autoCreateColumns, SelectionMode.SINGLE);
    }

    public RestrictedModeGrid(Class<T> beanType, SelectionMode allowedSelectionMode) {
        this(beanType, true, allowedSelectionMode);
    }

    /**
     * Gets the current allowed selection mode, defined at construction time. Only the value returned by this method or {@link SelectionMode#NONE} are allowed by {@link #setSelectionMode(SelectionMode)}.
     * @return Current allowed selection mode. This property is read-only.
     */
    public final SelectionMode getAllowedSelectionMode() {
        return allowedSelectionMode;
    }

    @Override
    public GridSelectionModel<T> setSelectionMode(SelectionMode selectionMode) {
        if(selectionMode == SelectionMode.NONE || selectionMode == this.getAllowedSelectionMode())
            return super.setSelectionMode(selectionMode);
        else throw new IllegalArgumentException("this grid only allows NONE or "+this.getAllowedSelectionMode().name()+" as selection modes, not "+selectionMode.name());
    }
}
