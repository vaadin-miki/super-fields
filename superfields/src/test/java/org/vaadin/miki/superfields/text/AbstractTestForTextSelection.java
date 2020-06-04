package org.vaadin.miki.superfields.text;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.miki.events.text.TextSelectionNotifier;
import org.vaadin.miki.markers.CanReceiveSelectionEventsFromClient;
import org.vaadin.miki.markers.CanSelectText;
import org.vaadin.miki.shared.text.TextSelectionDelegate;

abstract class AbstractTestForTextSelection<C extends Component & CanSelectText & HasValue<?, String> & CanReceiveSelectionEventsFromClient & TextSelectionNotifier<C>> {

    private C textComponent;

    private int eventCounter;

    private String lastSelectedText;

    protected abstract C constructComponent();

    @Before
    public void setUp() {
        MockVaadin.setup();
        this.textComponent = this.constructComponent();
        this.textComponent.addTextSelectionListener(event -> {
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
        Assert.assertFalse(this.textComponent.isReceivingSelectionEventsFromClient());
        this.textComponent.setValue(helloWorld);
        this.textComponent.selectAll();
        Assert.assertEquals("text-selection should have been fired", 1, this.eventCounter);
        Assert.assertEquals("all text should be selected in event", helloWorld, this.lastSelectedText);
        Assert.assertEquals("all text should be selected in attribute", helloWorld, this.textComponent.getElement().getAttribute(TextSelectionDelegate.SELECTED_TEXT_ATTRIBUTE_NAME));
        this.textComponent.selectNone();
        Assert.assertEquals("text-selection should have been fired again", 2, this.eventCounter);
        Assert.assertTrue("no text should be selected in event", this.lastSelectedText.isEmpty());
        Assert.assertTrue("no text should be selected in attribute", this.textComponent.getElement().getAttribute(TextSelectionDelegate.SELECTED_TEXT_ATTRIBUTE_NAME).isEmpty());
        this.textComponent.select(7, 12);
        Assert.assertEquals("text-selection should have been fired again", 3, this.eventCounter);
        Assert.assertEquals("some text should be selected in event", "world", this.lastSelectedText);
        Assert.assertEquals("some text should be selected in attribute", "world", this.textComponent.getElement().getAttribute(TextSelectionDelegate.SELECTED_TEXT_ATTRIBUTE_NAME));
        this.textComponent.setValue("clear selection");
        Assert.assertEquals("text-selection should have been fired again", 4, this.eventCounter);
        Assert.assertTrue("no text should be selected in event", this.lastSelectedText.isEmpty());
        Assert.assertTrue("no text should be selected in attribute", this.textComponent.getElement().getAttribute(TextSelectionDelegate.SELECTED_TEXT_ATTRIBUTE_NAME).isEmpty());
    }

}
