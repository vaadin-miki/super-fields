package org.vaadin.miki.superfields.checkbox;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SuperCheckboxTest {

    private SuperCheckbox checkbox;

    @Before
    public void setup() {
        MockVaadin.setup();
        this.checkbox = new SuperCheckbox();
    }

    @After
    public void teardown() {
        MockVaadin.tearDown();
    }

    @Test
    public void testReadOnlyAndEnabled() {
        this.checkbox.setReadOnly(true);
        Assert.assertTrue(this.checkbox.isReadOnly());
        Assert.assertFalse(this.checkbox.isEnabled());
        this.checkbox.setEnabled(true);
        Assert.assertTrue(this.checkbox.isEnabled());
        Assert.assertFalse(this.checkbox.isReadOnly());
    }

}