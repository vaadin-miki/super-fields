package org.vaadin.miki.superfields.gridselect;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.grid.Grid;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

public class GridMultiSelectTest {

    private GridMultiSelect<String> grid;

    private int eventCount = 0;

    @Before
    public void setUp() {
        MockVaadin.setup();
        this.grid = new GridMultiSelect<>("this", "is", "a", "test");
        this.grid.addValueChangeListener(event -> eventCount++);
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDisallowChangingSelectionMode() {
        this.grid.getGrid().setSelectionMode(Grid.SelectionMode.SINGLE);
    }

    @Test
    public void testAllowedSelectionModes() {
        Grid<String> underlyingGrid = this.grid.getGrid();
        Assert.assertTrue(underlyingGrid instanceof RestrictedModeGrid);
        Assert.assertSame(Grid.SelectionMode.MULTI, ((RestrictedModeGrid<String>) underlyingGrid).getAllowedSelectionMode());

        this.grid.getGrid().setSelectionMode(Grid.SelectionMode.NONE);
        this.grid.getGrid().setSelectionMode(Grid.SelectionMode.MULTI);
    }

    @Test
    public void testValueChanges() {
        Assert.assertNull(this.grid.getValue());
        Assert.assertTrue(this.grid.getGrid().getSelectedItems().isEmpty());
        this.grid.setValue(Collections.singleton("a"));
        Assert.assertEquals(1, this.eventCount);
        Assert.assertEquals(Collections.singleton("a"), this.grid.getValue());
        Assert.assertEquals(1, this.grid.getGrid().getSelectedItems().size());
        Assert.assertEquals("a", this.grid.getGrid().getSelectedItems().iterator().next());
        this.grid.setValue(Set.of("test", "this"));
        Assert.assertEquals(2, this.eventCount);
        Assert.assertEquals(Set.of("test", "this"), this.grid.getValue());
        Assert.assertEquals(2, this.grid.getGrid().getSelectedItems().size());
        this.grid.setMaximumSelectionSize(1);
        Assert.assertEquals(3, this.eventCount);
        Assert.assertTrue(this.grid.getValue().isEmpty());
        Assert.assertTrue(this.grid.getGrid().getSelectedItems().isEmpty());
    }

}
