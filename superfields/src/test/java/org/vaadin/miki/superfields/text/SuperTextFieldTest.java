package org.vaadin.miki.superfields.text;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SuperTextFieldTest {

    private SuperTextField textField;

    private int eventCounter;

    private String lastSelectedText;

    @Before
    public void setUp() {
        MockVaadin.setup();
        this.textField = new SuperTextField();
        this.textField.addTextSelectionListener(event -> {
            eventCounter++;
            lastSelectedText = event.getSelectedText();
        });
        this.eventCounter = 0;
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    // note: it is not possible to test client-side with karibu
    @Test
    public void testServerSideSelection() {
        final String helloWorld = "hello, world!";
        Assert.assertFalse(this.textField.isReceivingSelectionEventsFromClient());
        this.textField.setValue(helloWorld);
        this.textField.selectAll();
        Assert.assertEquals("text-selection should have been fired", 1, this.eventCounter);
        Assert.assertEquals("all text should be selected in event", helloWorld, this.lastSelectedText);
        Assert.assertEquals("all text should be selected in attribute", helloWorld, this.textField.getSelectedText());
        this.textField.selectNone();
        Assert.assertEquals("text-selection should have been fired again", 2, this.eventCounter);
        Assert.assertTrue("no text should be selected in event", this.lastSelectedText.isEmpty());
        Assert.assertTrue("no text should be selected in attribute", this.textField.getSelectedText().isEmpty());
        this.textField.select(7, 12);
        Assert.assertEquals("text-selection should have been fired again", 3, this.eventCounter);
        Assert.assertEquals("some text should be selected in event", "world", this.lastSelectedText);
        Assert.assertEquals("some text should be selected in attribute", "world", this.textField.getSelectedText());
        this.textField.setValue("clear selection");
        Assert.assertEquals("text-selection should have been fired again", 4, this.eventCounter);
        Assert.assertTrue("no text should be selected in event", this.lastSelectedText.isEmpty());
        Assert.assertTrue("no text should be selected in attribute", this.textField.getSelectedText().isEmpty());
    }

}