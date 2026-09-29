package org.vaadin.miki.markers;

import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonTester;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaTester;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;
import org.vaadin.miki.superfields.text.SuperTextArea;

// tests for #402
public class HasReadOnlyTest {

    @Test
    public void testComponentReadOnlyInLayout() {
        final FlexLayout layout = FlexLayoutHelpers.row();
        final SuperTextArea area = new SuperTextArea();
        layout.add(area);
        Assertions.assertTrue(area.isEnabled());
        HasReadOnly.setReadOnly(true, layout);
        // must be read-only
        Assertions.assertTrue(area.isReadOnly());
        // but must also be enabled
        Assertions.assertTrue(area.isEnabled());
        // layout itself also should be enabled
        Assertions.assertTrue(layout.isEnabled());
    }

    // the read-only flag alone proves nothing: setValue() changes a read-only field just fine,
    // so the only way to test #402 is to do what a user would do
    @Test
    public void testUserCannotEditComponentMadeReadOnlyInLayout() {
        final FlexLayout layout = FlexLayoutHelpers.row();
        final SuperTextArea area = new SuperTextArea();
        layout.add(area);
        try (BrowserlessUIContext window = BrowserlessUIContext.forComponent(layout)) {
            HasReadOnly.setReadOnly(true, layout);
            final TextAreaTester<TextArea> tester = window.test(area);
            Assertions.assertThrows(IllegalStateException.class, () -> tester.setValue("nope"),
                    "a user must not be able to type into a read-only text area");
            Assertions.assertEquals("", area.getValue(), "the value must not have changed");
        }
    }

    @Test
    public void testUserCannotClickComponentDisabledInLayout() {
        final FlexLayout layout = FlexLayoutHelpers.row();
        final Button button = new Button();
        layout.add(button);
        try (BrowserlessUIContext window = BrowserlessUIContext.forComponent(layout)) {
            HasReadOnly.setReadOnly(true, layout);
            final ButtonTester<Button> tester = window.test(button);
            Assertions.assertThrows(IllegalStateException.class, tester::click,
                    "a user must not be able to click a disabled button");
        }
    }

    @Test
    public void testComponentDisabledInLayout() {
        final FlexLayout layout = FlexLayoutHelpers.row();
        final Button area = new Button();
        layout.add(area);
        Assertions.assertTrue(area.isEnabled());
        HasReadOnly.setReadOnly(true, layout);
        // cannot be read-only, so must be disabled
        Assertions.assertFalse(area.isEnabled());
        // layout itself also should be enabled
        Assertions.assertTrue(layout.isEnabled());
    }

}