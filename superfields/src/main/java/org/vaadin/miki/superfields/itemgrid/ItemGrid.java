package org.vaadin.miki.superfields.itemgrid;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.binder.HasItems;
import org.vaadin.miki.markers.WithHelperMixin;
import org.vaadin.miki.markers.WithHelperPositionableMixin;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithItemsMixin;
import org.vaadin.miki.markers.WithValueMixin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Grid of items, with defined number of columns.
 * Each cell in the grid corresponds to a single element from the underlying collection of data.
 *
 * Note: currently this is not lazy-loading the data and not reacting to changes in the underlying data set.
 *
 * @param <T> Type of item stored in the grid.
 *
 * @author miki
 * @since 2020-04-14
 */
@Tag("item-grid")
public class ItemGrid<T>
        extends CustomField<T>
        implements HasItems<T>, HasStyle, WithItemsMixin<T, ItemGrid<T>>, WithIdMixin<ItemGrid<T>>,
                   WithHelperMixin<ItemGrid<T>>, WithHelperPositionableMixin<ItemGrid<T>>,
                   WithValueMixin<AbstractField.ComponentValueChangeEvent<CustomField<T>, T>, T, ItemGrid<T>> {

    /**
     * Default number of columns.
     */
    public static final int DEFAULT_COLUMN_COUNT = 3;

    /**
     * Style that by default indicates a selected item.
     * This is only needed when default {@link #setCellSelectionHandler(CellSelectionHandler)} is used.
     */
    public static final String DEFAULT_SELECTED_ITEM_CLASS_NAME = "item-grid-selected-cell";

    /**
     * Default {@link CellSelectionHandler}. It adds or removes {@link #DEFAULT_SELECTED_ITEM_CLASS_NAME} to the element of the component.
     * @param event Event.
     * @param <V> Value type.
     */
    public static <V> void defaultCellSelectionHandler(CellSelectionEvent<V> event) {
        if(event.isSelected())
            event.getCellInformation().getComponent().getElement().getClassList().add(DEFAULT_SELECTED_ITEM_CLASS_NAME);
        else
            event.getCellInformation().getComponent().getElement().getClassList().remove(DEFAULT_SELECTED_ITEM_CLASS_NAME);
    }

    /**
     * Default supplier for the main layout of the grid.
     * @return A {@link Div}.
     */
    public static Div defaultMainContainerSupplier() {
        Div result = new Div();
        result.addClassName("item-grid-contents");
        return result;
    }

    /**
     * Default {@link CellGenerator}. It produces a {@link Span} with {@link String#valueOf(Object)} called on {@code item}.
     * @param item Item to generate component for.
     * @param row Row in the grid.
     * @param col Column in the grid.
     * @param <V> Item type.
     * @return A {@link Span}: {@code <span>item</span>}.
     */
    public static <V> Component defaultCellGenerator(V item, int row, int col) {
        Span result = new Span(String.valueOf(item));
        result.addClassNames("item-grid-cell", "item-grid-cell-column-"+evenOrOdd(col), "item-grid-cell-row-"+evenOrOdd(row));
        return result;
    }

    /**
     * Default {@link RowComponentGenerator}. It produces a {@link Div}.
     * @param rowNumber Number of the row to create.
     * @return A {@link Div}.
     */
    public static Div defaultRowComponentGenerator(int rowNumber) {
        Div row = new Div();
        row.addClassNames("item-grid-row", "item-grid-row-"+evenOrOdd(rowNumber));
        return row;
    }

    /**
     * Defines whether a number is even or odd. Used to vary class names of various elements.
     * Internal use only.
     * @param number Number to check.
     * @return {@code "even"} when the {@code number} is even, otherwise {@code "odd"}.
     * @see #defaultRowComponentGenerator(int)
     * @see #defaultCellGenerator(Object, int, int)
     */
    private static String evenOrOdd(int number) {
        return number%2 == 0 ? "even" : "odd";
    }

    private final HasComponents contents;

    private final List<CellInformation<T>> cells = new ArrayList<>();

    private CellInformation<T> markedAsSelected;

    private transient CellGenerator<T> cellGenerator;

    private transient CellGenerator<T> paddingCellGenerator;

    private boolean paddingCellsClickable = false;

    private transient CellSelectionHandler<T> cellSelectionHandler;

    private transient RowComponentGenerator<?> rowComponentGenerator = ItemGrid::defaultRowComponentGenerator;

    private RowPaddingStrategy rowPaddingStrategy = RowPaddingStrategies.NO_PADDING;

    private int columnCount = DEFAULT_COLUMN_COUNT;

    /**
     * Creates the component with given items, using default {@link CellGenerator} and {@link CellSelectionHandler}.
     * @param items Items to add to the component.
     */
    @SafeVarargs
    public ItemGrid(T... items) {
        this(null, null, null, items);
    }

    /**
     * Creates the component with given items and {@link CellGenerator}, but with default {@link CellSelectionHandler}.
     * @param generator {@link CellGenerator} to use.
     * @param items Items to add to the component.
     */
    @SafeVarargs
    public ItemGrid(CellGenerator<T> generator, T... items) {
        this(null, generator, null, items);
    }

    /**
     * Creates the component with given {@link CellGenerator}, {@link CellSelectionHandler} and items.
     * @param generator {@link CellGenerator} to use.
     * @param handler {@link CellSelectionHandler} to use.
     * @param items Items to add to the component.
     */
    @SafeVarargs
    public ItemGrid(CellGenerator<T> generator, CellSelectionHandler<T> handler, T... items) {
        this(null, generator, handler, items);
    }

    /**
     * Creates the component with given {@link CellGenerator}, {@link CellSelectionHandler} and items, overriding default (empty) value.
     * @param defaultValue Default (empty) value.
     * @param generator {@link CellGenerator} to use.
     * @param handler {@link CellSelectionHandler} to use.
     * @param items Items to add to the component.
     */
    @SafeVarargs
    public ItemGrid(T defaultValue, CellGenerator<T> generator, CellSelectionHandler<T> handler, T... items) {
        this(defaultValue, ItemGrid::defaultMainContainerSupplier, generator, handler, items);
    }

    /**
     * Creates the component with given {@link CellGenerator}, {@link CellSelectionHandler} and items, overriding default (empty) value.
     * @param defaultValue Default (empty) value.
     * @param mainContainerSupplier Method to generate main container component.
     * @param generator {@link CellGenerator} to use.
     * @param handler {@link CellSelectionHandler} to use.
     * @param items Items to add to the component.
     * @param <C> Type parameter to ensure {@code mainContainerSupplier} provides a {@link Component} that also {@link HasComponents}.
     */
    @SafeVarargs
    public <C extends Component & HasComponents> ItemGrid(T defaultValue, Supplier<C> mainContainerSupplier, CellGenerator<T> generator, CellSelectionHandler<T> handler, T... items) {
        super(defaultValue);
        this.contents = mainContainerSupplier.get();
        this.add((Component)this.contents);
        this.setCellGenerator(generator);
        this.setCellSelectionHandler(handler);
        this.setItems(items);
    }

    /**
     * Repaints all current items.
     */
    protected final void repaintAllItems() {
        this.repaintAllItems(this.cells.stream().filter(CellInformation::isValueCell).map(CellInformation::getValue).collect(Collectors.toList()));
    }

    protected final CellInformation<T> buildPaddingCell(int row, int column) {
        final Component itemComponent = this.getPaddingCellGenerator().generateComponent(null, row, column);
        return new CellInformation<>(row, column, itemComponent);
    }

    protected final CellInformation<T> buildValueCell(T item, int row, int column) {
        final Component itemComponent = this.getCellGenerator().generateComponent(item, row, column);
        return new CellInformation<>(row, column, item, itemComponent);
    }

    /**
     * Repaints all items in the collection.
     * @param itemCollection Collection with items to repaint.
     */
    protected void repaintAllItems(Collection<T> itemCollection) {
        final T currentValue = this.getValue();

        this.contents.removeAll();
        this.cells.clear();

        // do all passed items
        int row = 0;

        int itemsLeft = itemCollection.size(); // used for padding

        final Iterator<T> iterator = itemCollection.iterator();
        final ArrayList<CellInformation<T>> currentRow = new ArrayList<>();

        RowPadding padding = this.getRowPaddingStrategy().getRowPadding(row, this.getColumnCount(), itemsLeft);

        while (itemsLeft > 0) {
            // there must be space for at least one cell from the data set
            if(padding.getBeginning() + padding.getEnd() >= this.getColumnCount())
                throw new IllegalStateException(String.format("row padding requires %d and %d cells - that is too many, as there are %d columns", padding.getBeginning(), padding.getEnd(), this.getColumnCount()));

            currentRow.clear();

            int column = 0;
            // first, the padding at the beginning
            for(; column < padding.getBeginning(); column++)
                currentRow.add(this.buildPaddingCell(row, column));

            // then, items
            for(; iterator.hasNext() && column < this.getColumnCount()-padding.getEnd() && itemsLeft-- >= 0; column++)
                currentRow.add(this.buildValueCell(iterator.next(), row, column));

            // end padding only when the padding is defined
            if(padding.getEnd() > 0)
                for(int zmp1 = column; zmp1 < Math.min(this.getColumnCount(), column + padding.getEnd()); zmp1++)
                    currentRow.add(this.buildPaddingCell(row, zmp1));

            // do processing and add to row component.
            final HasComponents rowContainer = this.getRowComponentGenerator().generateRowComponent(row);
            currentRow.forEach(cellInformation -> {
                this.processCell(cellInformation, currentValue);
                rowContainer.add(cellInformation.getComponent());
            });

            row += 1;
            this.contents.add((Component)rowContainer);
            padding = this.getRowPaddingStrategy().getRowPadding(row, this.getColumnCount(), itemsLeft);
        }
    }

    /**
     * Adds events to the cell and marks it as selected, if needed.
     * @param cellInformation Cell information.
     * @param currentValue Current value of the component. If the passed cell contains this value, the cell will be marked as selected.
     */
    private void processCell(CellInformation<T> cellInformation, T currentValue) {
        final boolean selected = cellInformation.isValueCell() && Objects.equals(cellInformation.getValue(), currentValue);
        this.getCellSelectionHandler().cellSelectionChanged(new CellSelectionEvent<>(cellInformation, selected));
        this.registerClickEvents(cellInformation);
        this.cells.add(cellInformation);

        if (selected)
            this.markedAsSelected = cellInformation;
    }

    /**
     * Adds a click listener to the dom element of the {@link Component} inside given {@link CellInformation}.
     * This click listener will select or deselect a cell and update the value of this grid.
     *
     * Note: when overriding this method, please remember to call {@code super}.
     * @param information Information. Never {@code null}.
     */
    protected void registerClickEvents(CellInformation<T> information) {
        information.getComponent().getElement().addEventListener("click", event -> this.clickCellAndUpdateValue(information));
    }

    private void clickCellAndUpdateValue(CellInformation<T> information) {
        if(this.arePaddingCellsClickable() || information.isValueCell()) {
            this.clickCell(information);
            this.updateValue();
        }
    }

    /**
     * Reacts to cell being clicked in the browser.
     * This method is invoked for padding cells if {@link #arePaddingCellsClickable()} is {@code true}.
     * @param information Information about the clicked cell.
     */
    protected void clickCell(CellInformation<T> information) {
        // if there is no selection at all, mark and remember component as selected
        if(this.markedAsSelected == null) {
            this.markedAsSelected = information;
            this.getCellSelectionHandler().cellSelectionChanged(new CellSelectionEvent<>(this.markedAsSelected, true));
        }
        // if the same cell is already selected, deselect it and do nothing else
        else if(Objects.equals(this.markedAsSelected, information)) {
            this.getCellSelectionHandler().cellSelectionChanged(new CellSelectionEvent<>(information, false));
            this.markedAsSelected = null;
        }
        // otherwise deselect existing value and select new value
        else {
            this.getCellSelectionHandler().cellSelectionChanged(new CellSelectionEvent<>(this.markedAsSelected, false));
            this.markedAsSelected = information;
            this.getCellSelectionHandler().cellSelectionChanged(new CellSelectionEvent<>(this.markedAsSelected, true));
        }
    }

    @Override
    protected T generateModelValue() {
        return this.markedAsSelected == null ? this.getEmptyValue() : this.markedAsSelected.getValue();
    }

    @Override
    protected void setPresentationValue(T t) {
        if(Objects.equals(t, this.getEmptyValue()) && this.markedAsSelected != null)
            this.clickCell(this.markedAsSelected);
        else if(!Objects.equals(t, this.getEmptyValue()))
            this.getCellInformation(t).ifPresent(this::clickCell);
    }

    /**
     * Sets new {@link CellGenerator}. Repaints all items.
     * @param cellGenerator Cell generator. If {@code null} is passed, {@link #defaultCellGenerator(Object, int, int)} will be used.
     */
    public void setCellGenerator(CellGenerator<T> cellGenerator) {
        this.cellGenerator = Optional.ofNullable(cellGenerator).orElse(ItemGrid::defaultCellGenerator);
        this.repaintAllItems();
    }

    /**
     * Returns current {@link CellGenerator} used to generate cells.
     * @return A {@link CellGenerator}. Never {@code null}.
     */
    public CellGenerator<T> getCellGenerator() {
        return this.cellGenerator;
    }

    /**
     * Chains {@link #setCellGenerator(CellGenerator)} and returns itself.
     * @param generator {@link CellGenerator} to use.
     * @return This.
     * @see #setCellGenerator(CellGenerator)
     */
    public ItemGrid<T> withCellGenerator(CellGenerator<T> generator) {
        this.setCellGenerator(generator);
        return this;
    }

    /**
     * Sets new {@link CellSelectionHandler}. Repaints all items.
     * @param cellSelectionHandler Cell selection handler. If {@code null} is passed, {@link #defaultCellSelectionHandler(CellSelectionEvent)} will be used.
     */
    public void setCellSelectionHandler(CellSelectionHandler<T> cellSelectionHandler) {
        this.cellSelectionHandler = Optional.ofNullable(cellSelectionHandler).orElse(ItemGrid::defaultCellSelectionHandler);
        this.repaintAllItems();
    }

    /**
     * Returns current {@link CellSelectionHandler} used to react to selection changes.
     * @return A {@link CellSelectionHandler}. Never {@code null}.
     */
    public CellSelectionHandler<T> getCellSelectionHandler() {
        return this.cellSelectionHandler;
    }

    /**
     * Chains {@link #setCellSelectionHandler(CellSelectionHandler)} and returns itself.
     * @param handler {@link CellSelectionHandler} to use.
     * @return This.
     * @see #setCellSelectionHandler(CellSelectionHandler)
     */
    public ItemGrid<T> withCellSelectionHandler(CellSelectionHandler<T> handler) {
        this.setCellSelectionHandler(handler);
        return this;
    }

    /**
     * Returns the number of rows (even incomplete) currently in the grid.
     * @return The number of rows. This will change if new items are added or the number of columns changes.
     * @see #setItems(Collection)
     * @see #setItems(Object[])
     * @see #setItems(Stream)
     * @see #setColumnCount(int)
     */
    public long getRowCount() {
        return ((Component)this.contents).getChildren().count();
    }

    /**
     * Returns the current number of columns.
     * @return The number of columns in the grid.
     */
    public int getColumnCount() {
        return this.columnCount;
    }

    /**
     * Sets the new number of columns. Repaints all items.
     * @param columnCount Number of columns. Values less than {@code 1} are replaced with {@code 1} instead,
     */
    public void setColumnCount(int columnCount) {
        this.columnCount = Math.max(1, columnCount);
        this.repaintAllItems();
    }

    /**
     * Chains {@link #setColumnCount(int)} and returns itself.
     * @param columnCount Number of columns.
     * @return This.
     * @see #setColumnCount(int)
     */
    public ItemGrid<T> withColumnCount(int columnCount) {
        this.setColumnCount(columnCount);
        return this;
    }

    @Override
    public void setItems(Collection<T> collection) {
        this.repaintAllItems(collection);
    }

    /**
     * Returns the number of cells.
     * @return Number of cells.
     */
    public int size() {
        return this.cells.size();
    }

    /**
     * Returns a list with information about each cell.
     * @return A list with {@link CellInformation}. Never {@code null}. Changes to the resulting object do not affect the grid.
     */
    public List<CellInformation<T>> getCellInformation() {
        return new ArrayList<>(this.cells);
    }

    /**
     * Returns {@link CellInformation} about currently selected cell.
     * @return A {@link CellInformation}, if any cell is currently selected.
     */
    public Optional<CellInformation<T>> getSelectedCellInformation() {
        return Optional.ofNullable(this.markedAsSelected);
    }

    /**
     * Returns {@link CellInformation} that corresponds to the provided value.
     * @param value A value to look for.
     * @return A {@link CellInformation}, if a cell corresponding to the provided value is available.
     * @see #setItems(Object[])
     * @see #setItems(Collection)
     * @see #setItems(Stream)
     */
    public Optional<CellInformation<T>> getCellInformation(T value) {
        return this.cells.stream().filter(cell -> Objects.equals(cell.getValue(), value)).findFirst();
    }

    /**
     * Returns {@link CellInformation} that corresponds to the cell of given coordinates.
     * @param row Row number (0-based).
     * @param column Column number (0-based).
     * @return A {@link CellInformation}, if a cell corresponding to the given coordinates is available.
     * @see #setColumnCount(int)
     * @see #getColumnCount()
     * @see #getRowCount()
     */
    public Optional<CellInformation<T>> getCellInformation(int row, int column) {
        return this.cells.stream().filter(cell -> cell.getRow() == row && cell.getColumn() == column).findFirst();
    }

    /**
     * Returns a list with {@link CellInformation} for each cell in given row.
     * @param row Row number, 0-based.
     * @return An unmodifiable list. May be empty.
     */
    public List<CellInformation<T>> getRowCellInformation(int row) {
        return this.cells.stream().filter(cell -> cell.getRow() == row).collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns a list with {@link CellInformation} for each cell in given column.
     * @param column Column number, 0-based.
     * @return An unmodifiable list. May be empty.
     */
    public List<CellInformation<T>> getColumnCellInformation(int column) {
        return this.cells.stream().filter(cell -> cell.getColumn() == column).collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns a {@link Stream} of all {@link Component}s in the cells.
     * @return A {@link Stream}. Never {@code null}.
     * @see #setCellGenerator(CellGenerator)
     */
    public Stream<Component> getCellComponents() {
        return this.cells.stream().map(CellInformation::getComponent);
    }

    /**
     * Sets new {@link RowComponentGenerator} invoked every time a new row for grid cells is needed.
     * @param rowComponentGenerator {@link RowComponentGenerator} to use. If {@code null} is passed, then {@link #defaultRowComponentGenerator(int)} will be used.
     */
    public void setRowComponentGenerator(RowComponentGenerator<?> rowComponentGenerator) {
        if(rowComponentGenerator == null)
            this.rowComponentGenerator = ItemGrid::defaultRowComponentGenerator;
        else this.rowComponentGenerator = rowComponentGenerator;
        this.repaintAllItems();
    }

    /**
     * Returns current {@link RowComponentGenerator}.
     * @return A {@link RowComponentGenerator}. Never {@code null}.
     */
    @SuppressWarnings("squid:S1452") // the result is Component that HasComponents, should be no need to worry about it
    public RowComponentGenerator<?> getRowComponentGenerator() {
        return rowComponentGenerator;
    }

    /**
     * Chains {@link #setRowComponentGenerator(RowComponentGenerator)} and returns itself.
     * @param generator A {@link RowComponentGenerator} to use.
     * @return This.
     * @see #setRowComponentGenerator(RowComponentGenerator)
     */
    public ItemGrid<T> withRowComponentGenerator(RowComponentGenerator<?> generator) {
        this.setRowComponentGenerator(generator);
        return this;
    }

    /**
     * Sets new {@link RowPaddingStrategy}. Repaints all items.
     * @param rowPaddingStrategy An implementation of {@link RowPaddingStrategy}. If {@code null} is passed, {@link RowPaddingStrategies#NO_PADDING} will be used.
     * @see RowPaddingStrategies
     */
    public void setRowPaddingStrategy(RowPaddingStrategy rowPaddingStrategy) {
        this.rowPaddingStrategy = Objects.requireNonNullElse(rowPaddingStrategy, RowPaddingStrategies.NO_PADDING);
        this.repaintAllItems();
    }

    /**
     * Returns current {@link RowPaddingStrategy}.
     * @return A {@link RowPaddingStrategy} currently assigned to this object. Defaults to {@link RowPaddingStrategies#NO_PADDING}.
     * @see RowPaddingStrategies
     */
    public RowPaddingStrategy getRowPaddingStrategy() {
        return rowPaddingStrategy;
    }

    /**
     * Chains {@link #setRowPaddingStrategy(RowPaddingStrategy)} and returns itself.
     * @param rowPaddingStrategy A {@link RowPaddingStrategy} to use.
     * @return This.
     * @see #setRowPaddingStrategy(RowPaddingStrategy)
     * @see RowPaddingStrategies
     */
    public ItemGrid<T> withRowPaddingStrategy(RowPaddingStrategy rowPaddingStrategy) {
        this.setRowPaddingStrategy(rowPaddingStrategy);
        return this;
    }

    /**
     * Returns the cell generator used for constructing padding cells. If none present, {@link #getCellGenerator()} will be used.
     * @return A generator for padding cells. Is used only when there is some {@link RowPaddingStrategy} set.
     * @see #setRowPaddingStrategy(RowPaddingStrategy)
     */
    public CellGenerator<T> getPaddingCellGenerator() {
        return Objects.requireNonNullElse(this.paddingCellGenerator, this.getCellGenerator());
    }

    /**
     * Sets cell generator for padding cells. If {@code null} is passed, {@link #getCellGenerator()} will be used.
     * @param paddingCellGenerator A generator for padding cells. Is used only when there is some {@link RowPaddingStrategy} set.
     * @see #setRowPaddingStrategy(RowPaddingStrategy)
     */
    public void setPaddingCellGenerator(CellGenerator<T> paddingCellGenerator) {
        this.paddingCellGenerator = paddingCellGenerator;
    }

    /**
     * Chains {@link #setPaddingCellGenerator(CellGenerator)} and returns itself.
     * @param paddingCellGenerator A generator for padding cells.
     * @return This.
     * @see #setPaddingCellGenerator(CellGenerator)
     */
    public ItemGrid<T> withPaddingCellGenerator(CellGenerator<T> paddingCellGenerator) {
        this.setPaddingCellGenerator(paddingCellGenerator);
        return this;
    }

    /**
     * Checks whether the padding cells (if any present) are clickable.
     * @return Whether or not padding cells can be clicked. Defaults to {@code false}.
     */
    public boolean arePaddingCellsClickable() {
        return paddingCellsClickable;
    }

    /**
     * Enables or disables click events on padding cells. If {@code true}, clicking a padding cell turns value to {@code null}.
     * @param paddingCellsClickable Whether or not to allow padding cells to react to clicks.
     */
    public void setPaddingCellsClickable(boolean paddingCellsClickable) {
        this.paddingCellsClickable = paddingCellsClickable;
    }

    /**
     * Chains {@link #setPaddingCellsClickable(boolean)} and returns itself.
     * @param paddingCellsClickable Whether or not to allow padding cells to react to clicks.
     * @return This.
     * @see #setPaddingCellsClickable(boolean)
     */
    public ItemGrid<T> withPaddingCellsClickable(boolean paddingCellsClickable) {
        this.setPaddingCellsClickable(paddingCellsClickable);
        return this;
    }

    /**
     * Simulates clicking a cell at given coordinates (which means it updates the value).
     * Nothing happens if there is no cell that corresponds to given coordinates.
     *
     * This method is For testing purposes only.
     *
     * @param row Row the cell is in.
     * @param col Column the cell is in.
     */
    void simulateCellClick(int row, int col) {
        this.getCellInformation(row, col).ifPresent(this::clickCellAndUpdateValue);
    }

}
