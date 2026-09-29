package org.vaadin.miki.superfields.lazyload;

import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author miki
 * @since 2023-12-15
 */
public class LazyLoadTest {

  private BrowserlessUIContext window;

  private LazyLoad<Span> lazyLoad;

  @BeforeEach
  public void setup() {
    this.window = BrowserlessUIContext.forComponent(() -> {
      this.lazyLoad = new LazyLoad<>(new Span("this is a test"));
      return this.lazyLoad;
    });
  }

  @AfterEach
  public void closeWindow() {
    if (this.window != null) {
      this.window.close();
    }
  }

  @Test
  public void testVisibilityBelowZero() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> this.lazyLoad.setContentVisibilityRanges(-3, 0.5));
  }

  @Test
  public void testVisibilityAboveOne() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> this.lazyLoad.setContentVisibilityRanges(0.1, 1.5));
  }

  @Test
  public void testVisibilityNotInOrder() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> this.lazyLoad.setContentVisibilityRanges(0.9, 0.8));
  }

  @Test
  public void testVisibilityRangesOk() {
    Assertions.assertEquals(0, this.lazyLoad.getContentHiddenVisibilityRange(), 0.000005);
    Assertions.assertEquals(1, this.lazyLoad.getContentShownVisibilityRange(), 0.000005);

    this.lazyLoad.setContentVisibilityRanges(0.2, 0.75);
    Assertions.assertEquals(0.2, this.lazyLoad.getContentHiddenVisibilityRange(), 0.0005);
    Assertions.assertEquals(0.75, this.lazyLoad.getContentShownVisibilityRange(), 0.0005);
  }

}