package org.vaadin.miki.superfields.gridselect;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.grid.Grid;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GridSelectTest {

    private GridSelect<String> grid;

    private int eventCount = 0;

    @Before
    public void setUp() {
        MockVaadin.setup();
        this.grid = new GridSelect<>("this", "is", "a", "test");
        this.grid.addValueChangeListener(event -> eventCount++);
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDisallowChangingSelectionMode() {
        this.grid.getGrid().setSelectionMode(Grid.SelectionMode.MULTI);
    }

    @Test
    public void testAllowedSelectionModes() {
        Grid<String> underlyingGrid = this.grid.getGrid();
        Assert.assertTrue(underlyingGrid instanceof RestrictedModeGrid);
        Assert.assertSame(Grid.SelectionMode.SINGLE, ((RestrictedModeGrid<String>) underlyingGrid).getAllowedSelectionMode());

        this.grid.getGrid().setSelectionMode(Grid.SelectionMode.NONE);
        this.grid.getGrid().setSelectionMode(Grid.SelectionMode.SINGLE);
    }

    @Test
    public void testValueChanges() {
        Assert.assertNull(this.grid.getValue());
        Assert.assertTrue(this.grid.getGrid().getSelectedItems().isEmpty());
        this.grid.setValue("a");
        Assert.assertEquals(1, this.eventCount);
        Assert.assertEquals("a", this.grid.getValue());
        Assert.assertEquals(1, this.grid.getGrid().getSelectedItems().size());
        Assert.assertEquals("a", this.grid.getGrid().getSelectedItems().iterator().next());
    }

}