package org.vaadin.miki.superfields.enabler;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 * @author miki
 * @since 2024-12-13
 */
public class TimedEnablerTest {

  private TimedEnabler<Button> enabler;
  private Button button;

  @Before
  public void setUp() {
    MockVaadin.setup();
    this.button = new Button("inner button");
    this.enabler = new TimedEnabler<>(this.button);
  }

  @After
  public void tearDown() {
    MockVaadin.tearDown();
  }

  @Test
  public void testInstantChanges() {
    Assert.assertTrue(this.enabler.isEnabled());
    Assert.assertTrue(this.button.isEnabled());
    this.enabler.setEnabled(false);
    Assert.assertFalse(this.enabler.isChangeInProgress());
    Assert.assertFalse(this.enabler.isEnabled());
    Assert.assertFalse(this.button.isEnabled());
  }

  @Test
  public void testDelayedChanges() {
    Assert.assertTrue(this.enabler.isEnabled());
    Assert.assertTrue(this.button.isEnabled());
    this.enabler.setDisabledTimeout(2000);
    this.enabler.setEnabled(false);
    Assert.assertTrue(this.enabler.isChangeInProgress());
    UI.getCurrent().access(() -> {
      try {
        Thread.sleep(500);
        // the component should still be enabled, but with change in progress
        Assert.assertTrue(this.enabler.isEnabled());
        Assert.assertTrue(this.button.isEnabled());
        Assert.assertTrue(this.enabler.isChangeInProgress());
        LoggerFactory.getLogger(this.getClass()).info("testing thread sleeping");
        Thread.sleep(5000);
        LoggerFactory.getLogger(this.getClass()).info("testing thread slept");
        // now both things should be disabled and no change should happen
        Assert.assertFalse(this.enabler.isEnabled());
        Assert.assertFalse(this.button.isEnabled());
        Assert.assertFalse(this.enabler.isChangeInProgress());
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
    LoggerFactory.getLogger(this.getClass()).info("main thread finished");
  }

  @Test
  public void testNoConcurrentRequests() throws InterruptedException {
    Assert.assertTrue(this.enabler.isEnabled());
    Assert.assertTrue(this.button.isEnabled());
    this.enabler.setDisabledTimeout(2000);
    this.enabler.setEnabled(false);
    Assert.assertTrue(this.enabler.isChangeInProgress());
    // change the timeout to shorter one, trigger again
    this.enabler.setDisabledTimeout(100);
    this.enabler.setEnabled(false);
    Assert.assertEquals(100, this.enabler.getDisabledTimeout());
    Thread.sleep(200);
    // this should not work - the original request is still queued
    Assert.assertTrue(this.enabler.isChangeInProgress());
    Assert.assertTrue(this.enabler.isEnabled());
    Assert.assertTrue(this.button.isEnabled());
    Thread.sleep(5000);
    // and now things should be disabled
    Assert.assertFalse(this.enabler.isChangeInProgress());
    Assert.assertFalse(this.enabler.isEnabled());
    Assert.assertFalse(this.button.isEnabled());
    // however, the timeout should be changed to whatever it was changed to
    Assert.assertEquals(100, this.enabler.getDisabledTimeout());
  }

}
