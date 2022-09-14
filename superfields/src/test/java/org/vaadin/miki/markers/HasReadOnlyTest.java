package org.vaadin.miki.markers;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;
import org.vaadin.miki.superfields.text.SuperTextArea;

// tests for #402
public class HasReadOnlyTest {

    @Before
    public void setup() {
        MockVaadin.setup();
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void testComponentReadOnlyInLayout() {
        final FlexLayout layout = FlexLayoutHelpers.row();
        final SuperTextArea area = new SuperTextArea();
        layout.add(area);
        Assert.assertTrue(area.isEnabled());
        HasReadOnly.setReadOnly(true, layout);
        // must be read-only
        Assert.assertTrue(area.isReadOnly());
        // but must also be enabled
        Assert.assertTrue(area.isEnabled());
        // layout itself also should be enabled
        Assert.assertTrue(layout.isEnabled());
    }

    @Test
    public void testComponentDisabledInLayout() {
        final FlexLayout layout = FlexLayoutHelpers.row();
        final Button area = new Button();
        layout.add(area);
        Assert.assertTrue(area.isEnabled());
        HasReadOnly.setReadOnly(true, layout);
        // cannot be read-only, so must be disabled
        Assert.assertFalse(area.isEnabled());
        // layout itself also should be enabled
        Assert.assertTrue(layout.isEnabled());
    }

}