package org.vaadin.miki.superfields.text;

import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vaadin.miki.events.text.TextSelectionNotifier;
import org.vaadin.miki.markers.CanReceiveSelectionEventsFromClient;
import org.vaadin.miki.markers.CanSelectText;
import org.vaadin.miki.shared.text.TextSelectionDelegate;

abstract class AbstractTestForTextSelection<C extends Component & CanSelectText & HasValue<?, String> & CanReceiveSelectionEventsFromClient & TextSelectionNotifier<C>> {

    private BrowserlessUIContext window;

    private C textComponent;

    private int eventCounter;

    private String lastSelectedText;

    protected abstract C constructComponent();

    @BeforeEach
    public void setUp() {
        this.window = BrowserlessUIContext.forComponent(() -> {
            this.textComponent = this.constructComponent();
            return this.textComponent;
        });
        this.textComponent.addTextSelectionListener(event -> {
            eventCounter++;
            lastSelectedText = event.getSelectedText();
        });
        this.eventCounter = 0;
    }

    @AfterEach
    public void closeWindow() {
        if (this.window != null) {
            this.window.close();
        }
    }

    // note: the client-side half of text selection needs a real browser to run its JavaScript,
    // so only the server-side API is covered here
    @Test
    public void testServerSideSelection() {
        final String helloWorld = "hello, world!";
        Assertions.assertFalse(this.textComponent.isReceivingSelectionEventsFromClient());
        this.textComponent.setValue(helloWorld);
        this.textComponent.selectAll();
        Assertions.assertEquals(1, this.eventCounter, "text-selection should have been fired");
        Assertions.assertEquals(helloWorld, this.lastSelectedText, "all text should be selected in event");
        Assertions.assertEquals(helloWorld, this.textComponent.getElement().getAttribute(TextSelectionDelegate.SELECTED_TEXT_ATTRIBUTE_NAME), "all text should be selected in attribute");
        this.textComponent.selectNone();
        Assertions.assertEquals(2, this.eventCounter, "text-selection should have been fired again");
        Assertions.assertTrue(this.lastSelectedText.isEmpty(), "no text should be selected in event");
        Assertions.assertTrue(this.textComponent.getElement().getAttribute(TextSelectionDelegate.SELECTED_TEXT_ATTRIBUTE_NAME).isEmpty(), "no text should be selected in attribute");
        this.textComponent.select(7, 12);
        Assertions.assertEquals(3, this.eventCounter, "text-selection should have been fired again");
        Assertions.assertEquals("world", this.lastSelectedText, "some text should be selected in event");
        Assertions.assertEquals("world", this.textComponent.getElement().getAttribute(TextSelectionDelegate.SELECTED_TEXT_ATTRIBUTE_NAME), "some text should be selected in attribute");
        this.textComponent.setValue("clear selection");
        Assertions.assertEquals(4, this.eventCounter, "text-selection should have been fired again");
        Assertions.assertTrue(this.lastSelectedText.isEmpty(), "no text should be selected in event");
        Assertions.assertTrue(this.textComponent.getElement().getAttribute(TextSelectionDelegate.SELECTED_TEXT_ATTRIBUTE_NAME).isEmpty(), "no text should be selected in attribute");
    }

}
