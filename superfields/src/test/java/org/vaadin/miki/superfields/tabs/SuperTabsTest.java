package org.vaadin.miki.superfields.tabs;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.tabs.Tab;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SuperTabsTest {

    private SuperTabs<String> tabs;

    // these are only for events and the like
    private int eventCount, headerGeneratorCount, contentGeneratorCount;

    @Before
    public void setUp() {
        MockVaadin.setup();
        this.tabs = new SuperTabs<>();
        this.tabs.setTabHandler(TabHandlers.VISIBILITY_HANDLER);
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
        Assert.assertNotNull(this.tabs.getTabContentGenerator());
        Assert.assertNotNull(this.tabs.getTabHeaderGenerator());
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
        this.tabs.setTabHeaderGenerator(s -> {
            this.headerGeneratorCount++;
            return new Tab(s);
        });
        this.tabs.setTabContentGenerator(s -> {
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

    @Test
    public void testAddAndRemoveTabs() {
        final String first = "first", second = "second", third = "third", fourth = "fourth";
        this.tabs.addTab(first, second, third, fourth);

        Assert.assertEquals("value changed should be triggered only for the first tab", 1, this.eventCount);
        Assert.assertEquals(first, this.tabs.getValue());
        Assert.assertEquals(4, this.tabs.size());

        this.eventCount = 0;
        this.tabs.setValue(third);
        Assert.assertEquals(third, this.tabs.getValue());
        Assert.assertEquals(1, this.eventCount);

        this.eventCount = 0;
        this.tabs.removeTab(second);
        Assert.assertEquals("no event should trigger when non-active tab is removed", 0, this.eventCount);
        Assert.assertEquals(third, this.tabs.getValue());
        Assert.assertEquals(3, this.tabs.size());
        Assert.assertEquals(Arrays.asList(first, third, fourth), this.tabs.getValues());
        Assert.assertFalse(this.tabs.getTabContents(second).isPresent());
        Assert.assertFalse(this.tabs.getTabHeader(second).isPresent());

        this.tabs.removeTab(third);
        Assert.assertEquals("removing current tab should trigger value change", 1, this.eventCount);
        Assert.assertNull(this.tabs.getValue());
        Assert.assertEquals(2, this.tabs.size());
        Assert.assertEquals(Arrays.asList(first, fourth), this.tabs.getValues());
        Assert.assertFalse(this.tabs.getTabContents(third).isPresent());
        Assert.assertFalse(this.tabs.getTabHeader(third).isPresent());
    }

    @Test
    public void testVisibilityOfTabContentsAndSelectedTabHeadersWithDefaultTabHandler() {
        final String tab1 = "tab1", tab2 = "tab2", tab3 = "tab3";
        this.tabs.addTab(tab1, tab2, tab3);
        Assert.assertTrue(this.tabs.getTabContents(tab1).isPresent());
        Assert.assertTrue(this.tabs.getTabContents(tab1).get().isVisible());
        Assert.assertTrue(this.tabs.getTabContents(tab2).isPresent());
        Assert.assertFalse(this.tabs.getTabContents(tab2).get().isVisible());
        Assert.assertTrue(this.tabs.getTabContents(tab3).isPresent());
        Assert.assertFalse(this.tabs.getTabContents(tab3).get().isVisible());
        Assert.assertTrue(this.tabs.getTabHeader(tab1).isPresent());
        Assert.assertTrue(this.tabs.getTabHeader(tab1).get().isSelected());
        Assert.assertTrue(this.tabs.getTabHeader(tab2).isPresent());
        Assert.assertFalse(this.tabs.getTabHeader(tab2).get().isSelected());
        Assert.assertTrue(this.tabs.getTabHeader(tab3).isPresent());
        Assert.assertFalse(this.tabs.getTabHeader(tab3).get().isSelected());

        this.tabs.setValue(tab3);
        Assert.assertTrue(this.tabs.getTabContents(tab1).isPresent());
        Assert.assertFalse(this.tabs.getTabContents(tab1).get().isVisible());
        Assert.assertTrue(this.tabs.getTabContents(tab2).isPresent());
        Assert.assertFalse(this.tabs.getTabContents(tab2).get().isVisible());
        Assert.assertTrue(this.tabs.getTabContents(tab3).isPresent());
        Assert.assertTrue(this.tabs.getTabContents(tab3).get().isVisible());
        Assert.assertTrue(this.tabs.getTabHeader(tab1).isPresent());
        Assert.assertFalse(this.tabs.getTabHeader(tab1).get().isSelected());
        Assert.assertTrue(this.tabs.getTabHeader(tab2).isPresent());
        Assert.assertFalse(this.tabs.getTabHeader(tab2).get().isSelected());
        Assert.assertTrue(this.tabs.getTabHeader(tab3).isPresent());
        Assert.assertTrue(this.tabs.getTabHeader(tab3).get().isSelected());
    }

    @Test
    public void testVisibilityOfTabContentsAndSelectedTabHeadersWithRemovingTabHandler() {
        this.tabs.setTabHandler(TabHandlers.REMOVING_HANDLER);
        final String tab1 = "tab1", tab2 = "tab2", tab3 = "tab3";
        this.tabs.addTab(tab1, tab2, tab3);
        Assert.assertTrue(this.tabs.getTabContents(tab1).isPresent());
        Assert.assertTrue(this.tabs.getTabContents(tab1).get().getParent().isPresent());
        Assert.assertTrue(this.tabs.getTabContents(tab2).isPresent());
        Assert.assertFalse(this.tabs.getTabContents(tab2).get().getParent().isPresent());
        Assert.assertTrue(this.tabs.getTabContents(tab3).isPresent());
        Assert.assertFalse(this.tabs.getTabContents(tab3).get().getParent().isPresent());
        Assert.assertTrue(this.tabs.getTabHeader(tab1).isPresent());
        Assert.assertTrue(this.tabs.getTabHeader(tab1).get().isSelected());
        Assert.assertTrue(this.tabs.getTabHeader(tab2).isPresent());
        Assert.assertFalse(this.tabs.getTabHeader(tab2).get().isSelected());
        Assert.assertTrue(this.tabs.getTabHeader(tab3).isPresent());
        Assert.assertFalse(this.tabs.getTabHeader(tab3).get().isSelected());

        this.tabs.setValue(tab3);
        Assert.assertTrue(this.tabs.getTabContents(tab1).isPresent());
        Assert.assertFalse(this.tabs.getTabContents(tab1).get().getParent().isPresent());
        Assert.assertTrue(this.tabs.getTabContents(tab2).isPresent());
        Assert.assertFalse(this.tabs.getTabContents(tab2).get().getParent().isPresent());
        Assert.assertTrue(this.tabs.getTabContents(tab3).isPresent());
        Assert.assertTrue(this.tabs.getTabContents(tab3).get().getParent().isPresent());
        Assert.assertTrue(this.tabs.getTabHeader(tab1).isPresent());
        Assert.assertFalse(this.tabs.getTabHeader(tab1).get().isSelected());
        Assert.assertTrue(this.tabs.getTabHeader(tab2).isPresent());
        Assert.assertFalse(this.tabs.getTabHeader(tab2).get().isSelected());
        Assert.assertTrue(this.tabs.getTabHeader(tab3).isPresent());
        Assert.assertTrue(this.tabs.getTabHeader(tab3).get().isSelected());
    }

    @Test
    public void testChangingTabHandler() {
        this.tabs.setTabHandler(TabHandlers.selectedContentHasClassName("selected-tab"));
        final String tab1 = "tab1", tab2 = "tab2", tab3 = "tab3";
        this.tabs.addTab(tab1, tab2, tab3);
        this.eventCount = 0;

        final Map<String, Component> contents = new HashMap<>();
        // now, selected tab is tab1 and all components are attached to the container
        // in addition, selected tab (tab1) contents have a style name
        for(String value: new String[]{tab1, tab2, tab3}) {
            Assert.assertTrue(this.tabs.getTabContents(value).isPresent());
            Assert.assertTrue(this.tabs.getTabContents(value).get().getParent().isPresent());
            contents.put(value, this.tabs.getTabContents(value).get());
            Assert.assertEquals("contents for tab1 should have a selected-tab class name", value.equals(tab1), this.tabs.getTabContents(value).get().getElement().getClassList().contains("selected-tab"));
        }

        this.tabs.setTabHandler(TabHandlers.REMOVING_HANDLER);
        // no value change should happen
        Assert.assertEquals(0, this.eventCount);

        // all components should still be the same
        // but, only the selected one should have a parent (others should not)
        // also, none of them should have a selected class name
        for(String value: new String[]{tab1, tab2, tab3}) {
            Assert.assertTrue(this.tabs.getTabContents(value).isPresent());
            Assert.assertSame(contents.get(value), this.tabs.getTabContents(value).get());
            Assert.assertEquals("only tab1 should have a parent", value.equals(tab1), this.tabs.getTabContents(value).get().getParent().isPresent());
            Assert.assertFalse(this.tabs.getTabContents(value).get().getElement().getClassList().contains("selected-tab"));
        }
    }

}