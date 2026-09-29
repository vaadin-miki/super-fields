package org.vaadin.miki.superfields.gridselect;

import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.grid.Grid;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

public class GridMultiSelectTest {

    private BrowserlessUIContext window;

    private GridMultiSelect<String> grid;

    private int eventCount = 0;

    @BeforeEach
    public void setUp() {
        this.window = BrowserlessUIContext.forComponent(() -> {
            this.grid = new GridMultiSelect<>("this", "is", "a", "test");
            return this.grid;
        });
        this.grid.addValueChangeListener(event -> eventCount++);
    }

    @AfterEach
    public void closeWindow() {
        if (this.window != null) {
            this.window.close();
        }
    }

    @Test
    public void testDisallowChangingSelectionMode() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.grid.getGrid().setSelectionMode(Grid.SelectionMode.SINGLE));
    }

    @Test
    public void testAllowedSelectionModes() {
        Grid<String> underlyingGrid = this.grid.getGrid();
        Assertions.assertTrue(underlyingGrid instanceof RestrictedModeGrid);
        Assertions.assertSame(Grid.SelectionMode.MULTI, ((RestrictedModeGrid<String>) underlyingGrid).getAllowedSelectionMode());

        this.grid.getGrid().setSelectionMode(Grid.SelectionMode.NONE);
        this.grid.getGrid().setSelectionMode(Grid.SelectionMode.MULTI);
    }

    @Test
    public void testValueChanges() {
        Assertions.assertNull(this.grid.getValue());
        Assertions.assertTrue(this.grid.getGrid().getSelectedItems().isEmpty());
        this.grid.setValue(Collections.singleton("a"));
        Assertions.assertEquals(1, this.eventCount);
        Assertions.assertEquals(Collections.singleton("a"), this.grid.getValue());
        Assertions.assertEquals(1, this.grid.getGrid().getSelectedItems().size());
        Assertions.assertEquals("a", this.grid.getGrid().getSelectedItems().iterator().next());
        this.grid.setValue(Set.of("test", "this"));
        Assertions.assertEquals(2, this.eventCount);
        Assertions.assertEquals(Set.of("test", "this"), this.grid.getValue());
        Assertions.assertEquals(2, this.grid.getGrid().getSelectedItems().size());
        this.grid.setMaximumSelectionSize(1);
        Assertions.assertEquals(3, this.eventCount);
        Assertions.assertTrue(this.grid.getValue().isEmpty());
        Assertions.assertTrue(this.grid.getGrid().getSelectedItems().isEmpty());
    }

}
