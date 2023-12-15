package org.vaadin.miki.superfields.lazyload;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.html.Span;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author miki
 * @since 2023-12-15
 */
public class LazyLoadTest {

  private LazyLoad<Span> lazyLoad;

  @Before
  public void setup() {
    MockVaadin.setup();
    this.lazyLoad = new LazyLoad<>(new Span("this is a test"));
  }

  @After
  public void tearDown() {
    MockVaadin.tearDown();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testVisibilityBelowZero() {
    this.lazyLoad.setContentVisibilityRanges(-3, 0.5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testVisibilityAboveOne() {
    this.lazyLoad.setContentVisibilityRanges(0.1, 1.5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testVisibilityNotInOrder() {
    this.lazyLoad.setContentVisibilityRanges(0.9, 0.8);
  }

  @Test
  public void testVisibilityRangesOk() {
    Assert.assertEquals(0, this.lazyLoad.getContentHiddenVisibilityRange(), 0.000005);
    Assert.assertEquals(1, this.lazyLoad.getContentShownVisibilityRange(),  0.000005);

    this.lazyLoad.setContentVisibilityRanges(0.2, 0.75);
    Assert.assertEquals(0.2,  this.lazyLoad.getContentHiddenVisibilityRange(), 0.0005);
    Assert.assertEquals(0.75, this.lazyLoad.getContentShownVisibilityRange(),  0.0005);
  }

}