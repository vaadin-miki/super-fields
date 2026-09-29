package org.vaadin.miki.superfields.gridselect;

import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.grid.Grid;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GridSelectTest {

  private BrowserlessUIContext window;

  private GridSelect<String> grid;

  private int eventCount = 0;

  @BeforeEach
  void setUp() {
    this.window = BrowserlessUIContext.forComponent(() -> {
      this.grid = new GridSelect<>("this", "is", "a", "test");
      return this.grid;
    });
    this.grid.addValueChangeListener(event -> eventCount++);
  }

  @AfterEach
  void closeWindow() {
    if (this.window != null) {
      this.window.close();
    }
  }

  @Test
  void testDisallowChangingSelectionMode() {
    final Grid<?> underlyingGrid = this.grid.getGrid();
    Assertions.assertThrows(IllegalArgumentException.class, () -> underlyingGrid.setSelectionMode(Grid.SelectionMode.MULTI));
  }

  @Test
  void testAllowedSelectionModes() {
    Grid<String> underlyingGrid = this.grid.getGrid();
    Assertions.assertInstanceOf(RestrictedModeGrid.class, underlyingGrid);
    Assertions.assertSame(Grid.SelectionMode.SINGLE, ((RestrictedModeGrid<String>) underlyingGrid).getAllowedSelectionMode());

    this.grid.getGrid().setSelectionMode(Grid.SelectionMode.NONE);
    this.grid.getGrid().setSelectionMode(Grid.SelectionMode.SINGLE);
  }

  @Test
  void testValueChanges() {
    Assertions.assertNull(this.grid.getValue());
    Assertions.assertTrue(this.grid.getGrid().getSelectedItems().isEmpty());
    this.grid.setValue("a");
    Assertions.assertEquals(1, this.eventCount);
    Assertions.assertEquals("a", this.grid.getValue());
    Assertions.assertEquals(1, this.grid.getGrid().getSelectedItems().size());
    Assertions.assertEquals("a", this.grid.getGrid().getSelectedItems().iterator().next());
  }

}