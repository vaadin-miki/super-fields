package org.vaadin.miki.superfields.itemgrid;

import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vaadin.miki.DomClickTester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ItemGridTest {

  private BrowserlessUIContext window;

  private ItemGrid<String> grid;

  private int eventCounter;

  @BeforeEach
  public void setUp() {
    this.window = BrowserlessUIContext.forComponent(() -> {
      this.grid = new ItemGrid<>();
      return this.grid;
    });
    this.eventCounter = 0;
    this.grid.addValueChangeListener(event -> eventCounter++);
  }

  @AfterEach
  public void closeWindow() {
    if (this.window != null) {
      this.window.close();
    }
  }

  /**
   * Clicks the cell at given coordinates the way a user would. Nothing happens when there is no such cell.
   */
  private void clickCell(int row, int column) {
    this.grid.getCellInformation(row, column)
        .map(CellInformation::getComponent)
        .ifPresent(component -> new DomClickTester(component).click());
  }

  @Test
  public void testNothingOnStartup() {
    Assertions.assertEquals(0, this.grid.size());
    Assertions.assertEquals(0, this.grid.getRowCount());
    Assertions.assertEquals(3, this.grid.getColumnCount());
    Assertions.assertEquals(0, this.grid.getCellComponents().count());
    Assertions.assertTrue(this.grid.getCellInformation().isEmpty());
    Assertions.assertNull(this.grid.getValue());
    Assertions.assertEquals(0, this.eventCounter);
  }

  private void assertCellSelectionAndStyles(String value) {
    Assertions.assertEquals(value, this.grid.getValue());
    Assertions.assertTrue(this.grid.getCellInformation(value).isPresent(), "no cell found for value");
    Assertions.assertTrue(this.grid.getCellInformation(value).get().getComponent().getElement().getClassList().contains(ItemGrid.DEFAULT_SELECTED_ITEM_CLASS_NAME));
    Assertions.assertTrue(this.grid.getCellInformation().stream().filter(info -> !value.equals(info.getValue())).noneMatch(info -> info.getComponent().getElement().getClassList().contains(ItemGrid.DEFAULT_SELECTED_ITEM_CLASS_NAME)));
  }

  @Test
  public void testOneFullRowOfItemsServerSide() {
    final String one = "one", two = "two", three = "three";
    this.grid.setItems(one, two, three);
    Assertions.assertEquals(0, this.eventCounter);
    Assertions.assertEquals(3, this.grid.size());
    Assertions.assertEquals(1, this.grid.getRowCount());
    Assertions.assertEquals(3, this.grid.getColumnCount());
    Assertions.assertNull(this.grid.getValue());
    Assertions.assertEquals(3, this.grid.getCellInformation().size());
    // by default, all spans
    Assertions.assertTrue(this.grid.getCellComponents().allMatch(Span.class::isInstance), "all cells should be spans by default");
    Assertions.assertTrue(this.grid.getCellComponents().noneMatch(component -> ((Span) component).getClassNames().contains(ItemGrid.DEFAULT_SELECTED_ITEM_CLASS_NAME)), "no cell should be selected when adding items");

    // select something
    this.grid.setValue(two);
    Assertions.assertEquals(1, this.eventCounter);
    this.assertCellSelectionAndStyles(two);

    // select that same something again
    this.grid.setValue(two);
    Assertions.assertEquals(1, this.eventCounter, "value was not changed, so event should not fire");
    this.assertCellSelectionAndStyles(two);

    // select some other value
    this.grid.setValue(one);
    Assertions.assertEquals(2, this.eventCounter, "value was changed, so event should fire");
    this.assertCellSelectionAndStyles(one);

    // select nothing
    this.grid.setValue(null);
    Assertions.assertEquals(3, this.eventCounter);
    Assertions.assertNull(this.grid.getValue());
    Assertions.assertTrue(this.grid.getCellComponents().noneMatch(component -> component.getElement().getClassList().contains(ItemGrid.DEFAULT_SELECTED_ITEM_CLASS_NAME)), "no cell should be selected when selecting null");
  }

  @Test
  public void threeRowsOfItemsSimulateClicks() {
    final String[] items = new String[]{"zero", "one", "two", "three", "four", "five", "six", "seven"};
    this.grid.setItems(items);

    Assertions.assertEquals(8, this.grid.size());
    Assertions.assertEquals(3, this.grid.getColumnCount());
    Assertions.assertEquals(3, this.grid.getRowCount());
    Assertions.assertNull(this.grid.getValue());

    // click cell in 2nd row, 3rd column ("five")
    this.clickCell(1, 2);
    Assertions.assertEquals(1, this.eventCounter);
    Assertions.assertEquals(items[5], this.grid.getValue());

    // click that cell again to deselect it
    this.clickCell(1, 2);
    Assertions.assertEquals(2, this.eventCounter);
    Assertions.assertNull(this.grid.getValue());

    // click a cell in top row, 2nd column ("one")
    this.clickCell(0, 1);
    Assertions.assertEquals(3, this.eventCounter);
    Assertions.assertEquals(items[1], this.grid.getValue());

    // click a cell in 3rd row, 1st column ("six")
    this.clickCell(2, 0);
    Assertions.assertEquals(4, this.eventCounter);
    Assertions.assertEquals(items[6], this.grid.getValue());

    // clicking a cell totally outside does nothing
    this.clickCell(-1, -1);
    Assertions.assertEquals(4, this.eventCounter);

    // after all this, only one cell should be selected
    List<CellInformation<String>> selection = this.grid.getCellInformation().stream().filter(cell -> cell.getComponent().getElement().getClassList().contains(ItemGrid.DEFAULT_SELECTED_ITEM_CLASS_NAME)).toList();
    Assertions.assertEquals(1, selection.size());
    Assertions.assertEquals(items[6], selection.get(0).getValue());
    Assertions.assertEquals(2, selection.get(0).getRow());
    Assertions.assertEquals(0, selection.get(0).getColumn());
  }

  @Test
  public void tenItemsChangingColumnCount() {
    final String[] items = new String[]{"item0", "item1", "item2", "item3", "item4", "item5", "item6", "item7", "item8", "item9"};
    this.grid.setItems(items);
    // default column count is 3 and there is no selection
    Assertions.assertNull(this.grid.getValue());
    Assertions.assertEquals(4, this.grid.getRowCount());
    Assertions.assertEquals(3, this.grid.getColumnCount());
    Assertions.assertEquals(10, this.grid.size());

    // click value in the second row, third column ("item5")
    this.clickCell(1, 2);
    Assertions.assertEquals(1, this.eventCounter);
    Assertions.assertEquals(items[5], this.grid.getValue());
    Assertions.assertEquals(this.grid.getCellInformation(1, 2), this.grid.getCellInformation(items[5]));

    // change column count to 5
    this.grid.setColumnCount(5);
    Assertions.assertEquals(1, this.eventCounter, "changing column size should not trigger value change");
    Assertions.assertEquals(2, this.grid.getRowCount(), "10 items in 5 columns should be arranged in 2 rows");
    Assertions.assertEquals(5, this.grid.getColumnCount());
    Assertions.assertEquals(10, this.grid.size());
    Assertions.assertEquals(items[5], this.grid.getValue());
    Assertions.assertNotEquals(this.grid.getCellInformation(1, 2), this.grid.getCellInformation(items[5]));

    // clicking the same coordinates should result in different value ("item7")
    this.clickCell(1, 2);
    Assertions.assertEquals(2, this.eventCounter);
    Assertions.assertEquals(items[7], this.grid.getValue());

    this.clickCell(1, 2);
    Assertions.assertEquals(3, this.eventCounter);
    Assertions.assertNull(this.grid.getValue());

    // change column count to 15, all should fit in one row
    this.grid.setColumnCount(15);
    Assertions.assertNull(this.grid.getValue());
    Assertions.assertEquals(15, this.grid.getColumnCount());
    Assertions.assertEquals(10, this.grid.size());
    Assertions.assertEquals(1, this.grid.getRowCount());

    // changing column count to less than 1 should result in 1
    this.grid.setColumnCount(-5);
    Assertions.assertNull(this.grid.getValue());
    Assertions.assertEquals(1, this.grid.getColumnCount());
    Assertions.assertEquals(10, this.grid.getRowCount());
    Assertions.assertEquals(10, this.grid.size());
  }

  @Test
  public void testFiveItemsChangingCellGenerator() {
    final String[] items = new String[]{"A", "B", "C", "D", "E"};
    this.grid.setItems(items);

    this.grid.setValue(items[1]);
    Assertions.assertEquals(items[1], this.grid.getValue());
    // by default, the component is a span with text that corresponds to the text
    Assertions.assertTrue(this.grid.getSelectedCellInformation().isPresent());
    Assertions.assertTrue(this.grid.getCellInformation().stream().allMatch(info -> info.getComponent() instanceof Span));
    Assertions.assertEquals(items[1], ((Span) this.grid.getSelectedCellInformation().get().getComponent()).getText());
    Assertions.assertTrue(this.grid.getSelectedCellInformation().get().getComponent().getElement().getClassList().contains(ItemGrid.DEFAULT_SELECTED_ITEM_CLASS_NAME));
    this.eventCounter = 0;

    // change the cell generator
    this.grid.setCellGenerator((value, row, column) -> new Paragraph(value));
    Assertions.assertEquals(0, this.eventCounter);
    Assertions.assertEquals(items[1], this.grid.getValue());
    // the components now should be paragraphs
    Assertions.assertTrue(this.grid.getSelectedCellInformation().isPresent());
    Assertions.assertTrue(this.grid.getCellInformation().stream().allMatch(info -> info.getComponent() instanceof Paragraph));
    Assertions.assertEquals(items[1], ((Paragraph) this.grid.getSelectedCellInformation().get().getComponent()).getText());
    // but the selection handler should still be the same, just adding class names
    Assertions.assertTrue(this.grid.getSelectedCellInformation().get().getComponent().getElement().getClassList().contains(ItemGrid.DEFAULT_SELECTED_ITEM_CLASS_NAME));
  }

  @Test
  public void testFiveItemsChangingSelectionHandler() {
    final List<String> log = new ArrayList<>();
    final String[] items = new String[]{"A", "B", "C", "D", "E"};
    this.grid.setItems(items);

    this.grid.setValue(items[1]);
    this.grid.setCellSelectionHandler(event -> log.add((event.isSelected() ? "+" : "-") + event.getCellInformation().getValue()));

    // as value is already selected, setting selection handler should leave messages (whole component is repainted)
    Assertions.assertEquals(5, log.size());
    Assertions.assertEquals(Arrays.asList("-A", "+B", "-C", "-D", "-E"), log, "initially all components must be redrawn");

    log.clear();
    this.grid.setValue(items[4]);
    Assertions.assertEquals(Arrays.asList("-B", "+E"), log);

    log.clear();
    this.clickCell(1, 1);
    Assertions.assertEquals(Collections.singletonList("-E"), log, "deselection should require an extra call to handler");

    log.clear();
    this.grid.setValue(items[3]);
    this.grid.setValue(items[0]);
    Assertions.assertEquals(Arrays.asList("+D", "-D", "+A"), log, "selection handler should be called in order");

    log.clear();
    this.grid.setValue(null);
    Assertions.assertEquals(Collections.singletonList("-A"), log, "setting null should not need an extra call to handler");

    // old selection handler should not be invoked
    Assertions.assertTrue(this.grid.getCellInformation().stream().noneMatch(info -> info.getComponent().getElement().getClassList().contains(ItemGrid.DEFAULT_SELECTED_ITEM_CLASS_NAME)));
  }

  @Test
  public void testPaddingStrategyChanges() {
    final String[] items = new String[]{"item-1", "item-2", "item-3", "item-4", "item-5", "item-6", "item-7", "item-8", "item-9"};
    this.grid.setItems(items);
    this.grid.setColumnCount(4);

    Assertions.assertEquals(3, this.grid.getRowCount(), "9 elements in 4 columns - that is 3 rows");

    this.grid.setRowPaddingStrategy(RowPaddingStrategies.LAST_ROW_FILL_END);

    Assertions.assertEquals(3, this.grid.getRowCount(), "padded 9 elements in 4 columns - that is 3 rows");
    Assertions.assertEquals(12, this.grid.size(), "with padding there should be 12 elements");
    Assertions.assertEquals(3, this.grid.getCellInformation().stream().filter(cell -> !cell.isValueCell()).count(), "only 3 padding cells should be there");
    for (int zmp1 = 1; zmp1 <= 3; zmp1++)
      Assertions.assertFalse(this.grid.getCellInformation(2, zmp1).map(CellInformation::isValueCell).orElse(false), "last three cells in last row must not be value cells");

    // this makes the grid effectively 2 columns, with padding column on each side
    this.grid.setRowPaddingStrategy((rowNumber, gridColumns, itemsLeft) -> new RowPadding(1, 1));

    Assertions.assertEquals(5, this.grid.getRowCount(), "9 elements, 4 columns with 2 padding cells - 5 rows");
    Assertions.assertEquals(19, this.grid.size(), "weird padding should have 19 cells in total");

    for (int zmp1 = 0; zmp1 < 4; zmp1++)
      Assertions.assertEquals(4, this.grid.getRowCellInformation(zmp1).size(), "four rows with 4 columns");
    Assertions.assertEquals(3, this.grid.getRowCellInformation(4).size(), "last row with only 3 columns");
  }

  @Test
  public void testPaddingStrategyMustNotTakeAllColumns() {
    final String[] items = new String[]{"A", "B", "C", "D"};
    this.grid.setColumnCount(2);
    this.grid.setRowPaddingStrategy((rowNumber, gridColumns, itemsLeft) -> new RowPadding(1, 1));
    Assertions.assertThrows(IllegalStateException.class, () -> this.grid.setItems(items)); // now this must fail
  }

  @Test
  public void testPaddingStrategyMustNotTakeMoreColumns() {
    final String[] items = new String[]{"A", "B", "C", "D"};
    this.grid.setColumnCount(3);
    this.grid.setItems(items);
    Assertions.assertThrows(IllegalStateException.class, () -> this.grid.setRowPaddingStrategy((rowNumber, gridColumns, itemsLeft) -> new RowPadding(2, 2)));
  }

  @Test
  public void clickPaddingCells() {
    final String[] items = new String[]{"one", "two"};
    this.grid.setItems(items);
    this.grid.setRowPaddingStrategy(RowPaddingStrategies.FIRST_ROW_FILL_BEGINNING);
    final Optional<CellInformation<String>> perhapsCell = this.grid.getCellInformation(0, 0);
    Assertions.assertTrue(perhapsCell.isPresent(), "there should be cell at (0, 0)");
    Assertions.assertFalse(perhapsCell.get().isValueCell(), "cell at (0, 0) must not be a value cell");
    this.clickCell(0, 0);
    Assertions.assertEquals(0, this.eventCounter, "padding cells are not clickable by default");
    this.clickCell(0, 1);
    Assertions.assertEquals(1, this.eventCounter, "value cells are clickable");
    this.grid.setPaddingCellsClickable(true);
    this.clickCell(0, 0);
    Assertions.assertEquals(2, this.eventCounter, "padding cells should now be clickable");
    this.clickCell(0, 1);
    Assertions.assertEquals(3, this.eventCounter, "value cells are still clickable");
  }

}