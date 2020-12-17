package org.vaadin.miki.superfields.itemgrid;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemGridTest {

    private ItemGrid<String> grid;

    private int eventCounter;

    @Before
    public void setUp() {
        MockVaadin.setup();
        this.grid = new ItemGrid<>();
        this.eventCounter = 0;
        this.grid.addValueChangeListener(event -> eventCounter++);
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void testNothingOnStartup() {
        Assert.assertEquals(0, this.grid.size());
        Assert.assertEquals(0, this.grid.getRowCount());
        Assert.assertEquals(3, this.grid.getColumnCount());
        Assert.assertEquals(0, this.grid.getCellComponents().count());
        Assert.assertTrue(this.grid.getCellInformation().isEmpty());
        Assert.assertNull(this.grid.getValue());
        Assert.assertEquals(0, this.eventCounter);
    }

    @Test
    public void testOneFullRowOfItemsServerSide() {
        final String one = "one", two = "two", three = "three";
        this.grid.setItems(one, two, three);
        Assert.assertEquals(0, this.eventCounter);
        Assert.assertEquals(3, this.grid.size());
        Assert.assertEquals(1, this.grid.getRowCount());
        Assert.assertEquals(3, this.grid.getColumnCount());
        Assert.assertNull(this.grid.getValue());
        Assert.assertEquals(3, this.grid.getCellInformation().size());
        // by default, all spans
        Assert.assertTrue("all cells should be spans by default", this.grid.getCellComponents().allMatch(c -> c instanceof Span));
        Assert.assertTrue("no cell should be selected when adding items", this.grid.getCellComponents().noneMatch(component -> ((Span)component).getClassNames().contains(ItemGrid.DEFAULT_SELECTED_ITEM_CLASS_NAME)));

        // select something
        this.grid.setValue(two);
        Assert.assertEquals(1, this.eventCounter);
        Assert.assertEquals(two, this.grid.getValue());
        Assert.assertTrue("no cell found for value", this.grid.getCellInformation(two).isPresent());
        Assert.assertTrue(this.grid.getCellInformation(two).get().getComponent().getElement().getClassList().contains(ItemGrid.DEFAULT_SELECTED_ITEM_CLASS_NAME));
        Assert.assertTrue(this.grid.getCellInformation().stream().filter(info -> !two.equals(info.getValue())).noneMatch(info -> info.getComponent().getElement().getClassList().contains(ItemGrid.DEFAULT_SELECTED_ITEM_CLASS_NAME)));

        // select that same something again
        this.grid.setValue(two);
        Assert.assertEquals("value was not changed, so event should not fire", 1, this.eventCounter);
        Assert.assertEquals(two, this.grid.getValue());
        Assert.assertTrue("no cell found for value", this.grid.getCellInformation(two).isPresent());
        Assert.assertTrue(this.grid.getCellInformation(two).get().getComponent().getElement().getClassList().contains(ItemGrid.DEFAULT_SELECTED_ITEM_CLASS_NAME));
        Assert.assertTrue(this.grid.getCellInformation().stream().filter(info -> !two.equals(info.getValue())).noneMatch(info -> info.getComponent().getElement().getClassList().contains(ItemGrid.DEFAULT_SELECTED_ITEM_CLASS_NAME)));

        // select some other value
        this.grid.setValue(one);
        Assert.assertEquals("value was changed, so event should fire", 2, this.eventCounter);
        Assert.assertEquals(one, this.grid.getValue());
        Assert.assertTrue("no cell found for value", this.grid.getCellInformation(one).isPresent());
        Assert.assertTrue(this.grid.getCellInformation(one).get().getComponent().getElement().getClassList().contains(ItemGrid.DEFAULT_SELECTED_ITEM_CLASS_NAME));
        Assert.assertTrue(this.grid.getCellInformation().stream().filter(info -> !one.equals(info.getValue())).noneMatch(info -> info.getComponent().getElement().getClassList().contains(ItemGrid.DEFAULT_SELECTED_ITEM_CLASS_NAME)));

        // select nothing
        this.grid.setValue(null);
        Assert.assertEquals(3, this.eventCounter);
        Assert.assertNull(this.grid.getValue());
        Assert.assertTrue("no cell should be selected when selecting null", this.grid.getCellComponents().noneMatch(component -> component.getElement().getClassList().contains(ItemGrid.DEFAULT_SELECTED_ITEM_CLASS_NAME)));
    }

    @Test
    public void threeRowsOfItemsSimulateClicks() {
        final String[] items = new String[]{"zero", "one", "two", "three", "four", "five", "six", "seven"};
        this.grid.setItems(items);

        Assert.assertEquals(8, this.grid.size());
        Assert.assertEquals(3, this.grid.getColumnCount());
        Assert.assertEquals(3, this.grid.getRowCount());
        Assert.assertNull(this.grid.getValue());

        // click cell in 2nd row, 3rd column ("five")
        this.grid.simulateCellClick(1, 2);
        Assert.assertEquals(1, this.eventCounter);
        Assert.assertEquals(items[5], this.grid.getValue());

        // click that cell again to deselect it
        this.grid.simulateCellClick(1, 2);
        Assert.assertEquals(2, this.eventCounter);
        Assert.assertNull(this.grid.getValue());

        // click a cell in top row, 2nd column ("one")
        this.grid.simulateCellClick(0, 1);
        Assert.assertEquals(3, this.eventCounter);
        Assert.assertEquals(items[1], this.grid.getValue());

        // click a cell in 3rd row, 1st column ("six")
        this.grid.simulateCellClick(2, 0);
        Assert.assertEquals(4, this.eventCounter);
        Assert.assertEquals(items[6], this.grid.getValue());

        // clicking a cell totally outside does nothing
        this.grid.simulateCellClick(-1, -1);
        Assert.assertEquals(4, this.eventCounter);

        // after all this, only one cell should be selected
        List<CellInformation<String>> selection = this.grid.getCellInformation().stream().filter(cell -> cell.getComponent().getElement().getClassList().contains(ItemGrid.DEFAULT_SELECTED_ITEM_CLASS_NAME)).collect(Collectors.toList());
        Assert.assertEquals(1, selection.size());
        Assert.assertEquals(items[6], selection.get(0).getValue());
        Assert.assertEquals(2, selection.get(0).getRow());
        Assert.assertEquals(0, selection.get(0).getColumn());
    }

    @Test
    public void tenItemsChangingColumnCount() {
        final String[] items = new String[]{"item0", "item1", "item2", "item3", "item4", "item5", "item6", "item7", "item8", "item9"};
        this.grid.setItems(items);
        // default column count is 3 and there is no selection
        Assert.assertNull(this.grid.getValue());
        Assert.assertEquals(4, this.grid.getRowCount());
        Assert.assertEquals(3, this.grid.getColumnCount());
        Assert.assertEquals(10, this.grid.size());

        // click value in the second row, third column ("item5")
        this.grid.simulateCellClick(1, 2);
        Assert.assertEquals(1, this.eventCounter);
        Assert.assertEquals(items[5], this.grid.getValue());
        Assert.assertEquals(this.grid.getCellInformation(1, 2), this.grid.getCellInformation(items[5]));

        // change column count to 5
        this.grid.setColumnCount(5);
        Assert.assertEquals("changing column size should not trigger value change", 1, this.eventCounter);
        Assert.assertEquals("10 items in 5 columns should be arranged in 2 rows", 2, this.grid.getRowCount());
        Assert.assertEquals(5, this.grid.getColumnCount());
        Assert.assertEquals(10, this.grid.size());
        Assert.assertEquals(items[5], this.grid.getValue());
        Assert.assertNotEquals(this.grid.getCellInformation(1, 2), this.grid.getCellInformation(items[5]));

        // clicking the same coordinates should result in different value ("item7")
        this.grid.simulateCellClick(1, 2);
        Assert.assertEquals(2, this.eventCounter);
        Assert.assertEquals(items[7], this.grid.getValue());

        this.grid.simulateCellClick(1, 2);
        Assert.assertEquals(3, this.eventCounter);
        Assert.assertNull(this.grid.getValue());

        // change column count to 15, all should fit in one row
        this.grid.setColumnCount(15);
        Assert.assertNull(this.grid.getValue());
        Assert.assertEquals(15, this.grid.getColumnCount());
        Assert.assertEquals(10, this.grid.size());
        Assert.assertEquals(1, this.grid.getRowCount());

        // changing column count to less than 1 should result in 1
        this.grid.setColumnCount(-5);
        Assert.assertNull(this.grid.getValue());
        Assert.assertEquals(1, this.grid.getColumnCount());
        Assert.assertEquals(10, this.grid.getRowCount());
        Assert.assertEquals(10, this.grid.size());
    }

    @Test
    public void testFiveItemsChangingCellGenerator() {
        final String[] items = new String[]{"A", "B", "C", "D", "E"};
        this.grid.setItems(items);

        this.grid.setValue(items[1]);
        Assert.assertEquals(items[1], this.grid.getValue());
        // by default, the component is a span with text that corresponds to the text
        Assert.assertTrue(this.grid.getSelectedCellInformation().isPresent());
        Assert.assertTrue(this.grid.getCellInformation().stream().allMatch(info -> info.getComponent() instanceof Span));
        Assert.assertEquals(items[1], ((Span) this.grid.getSelectedCellInformation().get().getComponent()).getText());
        Assert.assertTrue(this.grid.getSelectedCellInformation().get().getComponent().getElement().getClassList().contains(ItemGrid.DEFAULT_SELECTED_ITEM_CLASS_NAME));
        this.eventCounter = 0;

        // change the cell generator
        this.grid.setCellGenerator((value, row, column) -> new Paragraph(value));
        Assert.assertEquals(0, this.eventCounter);
        Assert.assertEquals(items[1], this.grid.getValue());
        // the components now should be paragraphs
        Assert.assertTrue(this.grid.getSelectedCellInformation().isPresent());
        Assert.assertTrue(this.grid.getCellInformation().stream().allMatch(info -> info.getComponent() instanceof Paragraph));
        Assert.assertEquals(items[1], ((Paragraph) this.grid.getSelectedCellInformation().get().getComponent()).getText());
        // but the selection handler should still be the same, just adding class names
        Assert.assertTrue(this.grid.getSelectedCellInformation().get().getComponent().getElement().getClassList().contains(ItemGrid.DEFAULT_SELECTED_ITEM_CLASS_NAME));
    }

    @Test
    public void testFiveItemsChangingSelectionHandler() {
        final List<String> log = new ArrayList<>();
        final String[] items = new String[]{"A", "B", "C", "D", "E"};
        this.grid.setItems(items);

        this.grid.setValue(items[1]);
        this.grid.setCellSelectionHandler(event -> log.add((event.isSelected() ? "+" : "-")+event.getCellInformation().getValue()));

        // as value is already selected, setting selection handler should leave messages (whole component is repainted)
        Assert.assertEquals(5, log.size());
        Assert.assertEquals("initially all components must be redrawn", Arrays.asList("-A", "+B", "-C", "-D", "-E"), log);

        log.clear();
        this.grid.setValue(items[4]);
        Assert.assertEquals(Arrays.asList("-B", "+E"), log);

        log.clear();
        this.grid.simulateCellClick(1, 1);
        Assert.assertEquals("deselection should require an extra call to handler", Collections.singletonList("-E"), log);

        log.clear();
        this.grid.setValue(items[3]);
        this.grid.setValue(items[0]);
        Assert.assertEquals("selection handler should be called in order", Arrays.asList("+D", "-D", "+A"), log);

        log.clear();
        this.grid.setValue(null);
        Assert.assertEquals("setting null should not need an extra call to handler", Collections.singletonList("-A"), log);

        // old selection handler should not be invoked
        Assert.assertTrue(this.grid.getCellInformation().stream().noneMatch(info -> info.getComponent().getElement().getClassList().contains(ItemGrid.DEFAULT_SELECTED_ITEM_CLASS_NAME)));
    }

    @Test
    public void testPaddingStrategyChanges() {
        final String[] items = new String[]{"item-1", "item-2", "item-3", "item-4", "item-5", "item-6", "item-7", "item-8", "item-9"};
        this.grid.setItems(items);
        this.grid.setColumnCount(4);

        Assert.assertEquals("9 elements in 4 columns - that is 3 rows", 3, this.grid.getRowCount());

        this.grid.setRowPaddingStrategy(RowPaddingStrategies.LAST_ROW_FILL_END);

        Assert.assertEquals("padded 9 elements in 4 columns - that is 3 rows", 3, this.grid.getRowCount());
        Assert.assertEquals("with padding there should be 12 elements", 12, this.grid.size());
        Assert.assertEquals("only 3 padding cells should be there", 3, this.grid.getCellInformation().stream().filter(cell -> !cell.isValueCell()).count());
        for(int zmp1=1; zmp1<=3; zmp1++)
            Assert.assertFalse("last three cells in last row must not be value cells", this.grid.getCellInformation(2, zmp1).map(CellInformation::isValueCell).orElse(false));

        // this makes the grid effectively 2 columns, with padding column on each side
        this.grid.setRowPaddingStrategy((rowNumber, gridColumns, itemsLeft) -> new RowPadding(1, 1));

        Assert.assertEquals("9 elements, 4 columns with 2 padding cells - 5 rows", 5, this.grid.getRowCount());
        Assert.assertEquals("weird padding should have 19 cells in total", 19, this.grid.size());

        for(int zmp1=0; zmp1<4; zmp1++)
            Assert.assertEquals("four rows with 4 columns", 4, this.grid.getRowCellInformation(zmp1).size());
        Assert.assertEquals("last row with only 3 columns", 3, this.grid.getRowCellInformation(4).size());
    }

    @Test(expected = IllegalStateException.class)
    public void testPaddingStrategyMustNotTakeAllColumns() {
        final String[] items = new String[]{"A", "B", "C", "D"};
        this.grid.setColumnCount(2);
        this.grid.setRowPaddingStrategy((rowNumber, gridColumns, itemsLeft) -> new RowPadding(1, 1));
        this.grid.setItems(items); // now this must fail
    }

    @Test(expected = IllegalStateException.class)
    public void testPaddingStrategyMustNotTakeMoreColumns() {
        final String[] items = new String[]{"A", "B", "C", "D"};
        this.grid.setColumnCount(3);
        this.grid.setItems(items);
        this.grid.setRowPaddingStrategy((rowNumber, gridColumns, itemsLeft) -> new RowPadding(2, 2));
    }

}