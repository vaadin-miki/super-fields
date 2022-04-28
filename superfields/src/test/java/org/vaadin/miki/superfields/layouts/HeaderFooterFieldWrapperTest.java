package org.vaadin.miki.superfields.layouts;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.miki.superfields.text.SuperTextField;

public class HeaderFooterFieldWrapperTest {

    @Before
    public void setUp() throws Exception {
        MockVaadin.setup();
    }

    @After
    public void tearDown() throws Exception {
        MockVaadin.tearDown();
    }

    @Test
    public void testDefaultValueIsFromComponent() {
        final SuperTextField textField = new SuperTextField();

        final HeaderFooterFieldWrapper<String, FlexLayout, FlexLayout> wrapper = new HeaderFooterFieldWrapper<>(
                FlexLayout::new, new FlexLayout(), textField, new FlexLayout()
        );

        Assert.assertEquals("", textField.getValue());
        Assert.assertEquals("", wrapper.getValue());
    }

}