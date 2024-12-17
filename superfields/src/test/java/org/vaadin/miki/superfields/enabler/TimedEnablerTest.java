package org.vaadin.miki.superfields.enabler;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.button.Button;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
  public void testInstantChanges() throws InterruptedException {
    Assert.assertTrue(this.enabler.isEnabled());
    Assert.assertTrue(this.button.isEnabled());
    this.enabler.setEnabled(false);
    Assert.assertFalse(this.enabler.isEnabled());
    Assert.assertTrue(this.button.isEnabled());
  }

}
