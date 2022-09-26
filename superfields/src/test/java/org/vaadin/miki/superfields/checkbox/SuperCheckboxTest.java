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
        // on start, component is both enabled and not read-only
        Assert.assertTrue(this.checkbox.isEnabled());
        Assert.assertFalse(this.checkbox.isReadOnly());
        // mark read-only, it should disable component
        this.checkbox.setReadOnly(true);
        Assert.assertFalse(this.checkbox.isEnabled());
        Assert.assertTrue(this.checkbox.isReadOnly());
        // this must fail, as the component must be both enabled AND not read-only
        this.checkbox.setEnabled(true);
        Assert.assertFalse(this.checkbox.isEnabled());
        Assert.assertTrue(this.checkbox.isReadOnly());
        // now it should be enabled
        this.checkbox.setReadOnly(false);
        Assert.assertTrue(this.checkbox.isEnabled());
        Assert.assertFalse(this.checkbox.isReadOnly());
        // disable
        this.checkbox.setEnabled(false);
        Assert.assertFalse(this.checkbox.isEnabled());
        Assert.assertFalse(this.checkbox.isReadOnly());
        // make read-only
        this.checkbox.setReadOnly(true);
        Assert.assertFalse(this.checkbox.isEnabled());
        Assert.assertTrue(this.checkbox.isReadOnly());
        // make not read-only, still disabled (because enabled was set to false)
        this.checkbox.setReadOnly(false);
        Assert.assertFalse(this.checkbox.isEnabled());
        Assert.assertFalse(this.checkbox.isReadOnly());
        // finally, enable
        this.checkbox.setEnabled(true);
        Assert.assertTrue(this.checkbox.isEnabled());
        Assert.assertFalse(this.checkbox.isReadOnly());
    }

}