package org.vaadin.miki.superfields.tabs;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.tabs.Tab;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class SuperTabsTest {

    private SuperTabs<String> tabs;

    // these are only for events and the like
    private int eventCount, headerGeneratorCount, contentGeneratorCount;

    @Before
    public void setUp() {
        MockVaadin.setup();
        this.tabs = new SuperTabs<>();
        this.tabs.addValueChangeListener(e -> eventCount++);
        this.eventCount = 0;
        this.headerGeneratorCount = 0;
        this.contentGeneratorCount = 0;
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void testNothingOnStartup() {
        Assert.assertEquals(0, this.tabs.size());
        Assert.assertEquals(0, this.eventCount);
        Assert.assertFalse(this.tabs.isCustomValueAllowed());
        Assert.assertNotNull(this.tabs.getContentGenerator());
        Assert.assertNotNull(this.tabs.getHeaderGenerator());
    }

    @Test
    public void testAddDefaultFirstTab() {
        final String string = "hello";
        this.tabs.addTab(string);
        Assert.assertEquals(1, this.tabs.size());
        Assert.assertEquals(Collections.singletonList(string), this.tabs.getValues());
        Assert.assertEquals("adding first tab must trigger value change event", 1, this.eventCount);
        Assert.assertEquals(string, this.tabs.getValue());
        Assert.assertTrue(this.tabs.getTabHeader(string).isPresent());
        Assert.assertTrue(this.tabs.getTabContents(string).isPresent());
    }

    @Test
    public void testAddTwoTabsAndToggleThenAddAnotherTab() {
        // also tracking how many times generators were triggered
        this.tabs.setHeaderGenerator(s -> {
            this.headerGeneratorCount++;
            return new Tab(s);
        });
        this.tabs.setContentGenerator(s -> {
            this.contentGeneratorCount++;
            return new Span(s);
        });
        final String first = "first", second = "second", third = "third";
        this.tabs.addTab(first);
        // only one generation of both header and content, because the first tab is selected automatically
        Assert.assertEquals("header should have been only generated once", 1, this.headerGeneratorCount);
        Assert.assertEquals("content should have been only generated once", 1, this.contentGeneratorCount);

        this.tabs.addTab(second);
        Assert.assertEquals("only the first tab should trigger value change", 1, this.eventCount);
        Assert.assertEquals(first, this.tabs.getValue());
        Assert.assertEquals(2, this.tabs.size());
        Assert.assertEquals("header should have been generated twice", 2, this.headerGeneratorCount);
        Assert.assertEquals("content should have been generated twice", 2, this.contentGeneratorCount);

        this.eventCount = 0;
        // switch to other tab
        this.tabs.setValue(second);
        Assert.assertEquals("only one value change event should have happened", 1, this.eventCount);
        Assert.assertEquals(second, this.tabs.getValue());
        this.tabs.setValue(second);
        Assert.assertEquals("value was not really changed, event should not trigger", 1, this.eventCount);

        // switch to the first tab again
        this.tabs.setValue(first);
        Assert.assertEquals("two value change events should have happened", 2, this.eventCount);
        Assert.assertEquals("header *still* should have been generated twice", 2, this.headerGeneratorCount);
        Assert.assertEquals("content *still* should have been generated twice", 2, this.contentGeneratorCount);

        // this should have no effect
        this.tabs.setValue("no effect");
        Assert.assertEquals("no value change happened", 2, this.eventCount);
        Assert.assertEquals("no changes, two headers", 2, this.headerGeneratorCount);
        Assert.assertEquals("no changes, two contents", 2, this.contentGeneratorCount);
        Assert.assertEquals(first, this.tabs.getValue());

        this.eventCount = 0;
        this.tabs.setCustomValueAllowed(true);
        this.tabs.setValue(third);
        Assert.assertEquals(3, this.tabs.size());
        Assert.assertEquals("tab should be switched to the new one", 1, this.eventCount);
        Assert.assertEquals(third, this.tabs.getValue());
        Assert.assertEquals("should now be three tabs", 3, this.headerGeneratorCount);
        Assert.assertEquals("should now be three contents", 3, this.contentGeneratorCount);
        Assert.assertEquals(Arrays.asList(first, second, third), this.tabs.getValues());
    }

}