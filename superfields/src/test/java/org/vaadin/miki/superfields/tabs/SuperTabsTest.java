package org.vaadin.miki.superfields.tabs;

import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.tabs.Tab;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SuperTabsTest {

    private BrowserlessUIContext window;

    private SuperTabs<String> tabs;

    // these are only for events and the like
    private int eventCount, headerGeneratorCount, contentGeneratorCount;

    @BeforeEach
    public void setUp() {
        this.window = BrowserlessUIContext.forComponent(() -> {
            this.tabs = new SuperTabs<>();
            return this.tabs;
        });
        this.tabs.setTabHandler(TabHandlers.VISIBILITY_HANDLER);
        this.tabs.addValueChangeListener(e -> eventCount++);
        this.eventCount = 0;
        this.headerGeneratorCount = 0;
        this.contentGeneratorCount = 0;
    }

    @AfterEach
    public void closeWindow() {
        if (this.window != null) {
            this.window.close();
        }
    }

    @Test
    public void testNothingOnStartup() {
        Assertions.assertEquals(0, this.tabs.size());
        Assertions.assertEquals(0, this.eventCount);
        Assertions.assertFalse(this.tabs.isCustomValueAllowed());
        Assertions.assertNotNull(this.tabs.getTabContentGenerator());
        Assertions.assertNotNull(this.tabs.getTabHeaderGenerator());
    }

    @Test
    public void testAddDefaultFirstTab() {
        final String string = "hello";
        this.tabs.addTab(string);
        Assertions.assertEquals(1, this.tabs.size());
        Assertions.assertEquals(Collections.singletonList(string), this.tabs.getValues());
        Assertions.assertEquals(1, this.eventCount, "adding first tab must trigger value change event");
        Assertions.assertEquals(string, this.tabs.getValue());
        Assertions.assertTrue(this.tabs.getTabHeader(string).isPresent());
        Assertions.assertTrue(this.tabs.getTabContents(string).isPresent());
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
        Assertions.assertEquals(1, this.headerGeneratorCount, "header should have been only generated once");
        Assertions.assertEquals(1, this.contentGeneratorCount, "content should have been only generated once");

        this.tabs.addTab(second);
        Assertions.assertEquals(1, this.eventCount, "only the first tab should trigger value change");
        Assertions.assertEquals(first, this.tabs.getValue());
        Assertions.assertEquals(2, this.tabs.size());
        Assertions.assertEquals(2, this.headerGeneratorCount, "header should have been generated twice");
        Assertions.assertEquals(2, this.contentGeneratorCount, "content should have been generated twice");

        this.eventCount = 0;
        // switch to other tab
        this.tabs.setValue(second);
        Assertions.assertEquals(1, this.eventCount, "only one value change event should have happened");
        Assertions.assertEquals(second, this.tabs.getValue());
        this.tabs.setValue(second);
        Assertions.assertEquals(1, this.eventCount, "value was not really changed, event should not trigger");

        // switch to the first tab again
        this.tabs.setValue(first);
        Assertions.assertEquals(2, this.eventCount, "two value change events should have happened");
        Assertions.assertEquals(2, this.headerGeneratorCount, "header *still* should have been generated twice");
        Assertions.assertEquals(2, this.contentGeneratorCount, "content *still* should have been generated twice");

        // this should have no effect
        this.tabs.setValue("no effect");
        Assertions.assertEquals(2, this.eventCount, "no value change happened");
        Assertions.assertEquals(2, this.headerGeneratorCount, "no changes, two headers");
        Assertions.assertEquals(2, this.contentGeneratorCount, "no changes, two contents");
        Assertions.assertEquals(first, this.tabs.getValue());

        this.eventCount = 0;
        this.tabs.setCustomValueAllowed(true);
        this.tabs.setValue(third);
        Assertions.assertEquals(3, this.tabs.size());
        Assertions.assertEquals(1, this.eventCount, "tab should be switched to the new one");
        Assertions.assertEquals(third, this.tabs.getValue());
        Assertions.assertEquals(3, this.headerGeneratorCount, "should now be three tabs");
        Assertions.assertEquals(3, this.contentGeneratorCount, "should now be three contents");
        Assertions.assertEquals(Arrays.asList(first, second, third), this.tabs.getValues());
    }

    @Test
    public void testAddAndRemoveTabs() {
        final String first = "first", second = "second", third = "third", fourth = "fourth";
        this.tabs.addTab(first, second, third, fourth);

        Assertions.assertEquals(1, this.eventCount, "value changed should be triggered only for the first tab");
        Assertions.assertEquals(first, this.tabs.getValue());
        Assertions.assertEquals(4, this.tabs.size());

        this.eventCount = 0;
        this.tabs.setValue(third);
        Assertions.assertEquals(third, this.tabs.getValue());
        Assertions.assertEquals(1, this.eventCount);

        this.eventCount = 0;
        this.tabs.removeTab(second);
        Assertions.assertEquals(0, this.eventCount, "no event should trigger when non-active tab is removed");
        Assertions.assertEquals(third, this.tabs.getValue());
        Assertions.assertEquals(3, this.tabs.size());
        Assertions.assertEquals(Arrays.asList(first, third, fourth), this.tabs.getValues());
        Assertions.assertFalse(this.tabs.getTabContents(second).isPresent());
        Assertions.assertFalse(this.tabs.getTabHeader(second).isPresent());

        this.tabs.removeTab(third);
        Assertions.assertEquals(1, this.eventCount, "removing current tab should trigger value change");
        Assertions.assertNull(this.tabs.getValue());
        Assertions.assertEquals(2, this.tabs.size());
        Assertions.assertEquals(Arrays.asList(first, fourth), this.tabs.getValues());
        Assertions.assertFalse(this.tabs.getTabContents(third).isPresent());
        Assertions.assertFalse(this.tabs.getTabHeader(third).isPresent());
    }

    @Test
    public void testVisibilityOfTabContentsAndSelectedTabHeadersWithDefaultTabHandler() {
        final String tab1 = "tab1", tab2 = "tab2", tab3 = "tab3";
        this.tabs.addTab(tab1, tab2, tab3);
        Assertions.assertTrue(this.tabs.getTabContents(tab1).isPresent());
        Assertions.assertTrue(this.tabs.getTabContents(tab1).get().isVisible());
        Assertions.assertTrue(this.tabs.getTabContents(tab2).isPresent());
        Assertions.assertFalse(this.tabs.getTabContents(tab2).get().isVisible());
        Assertions.assertTrue(this.tabs.getTabContents(tab3).isPresent());
        Assertions.assertFalse(this.tabs.getTabContents(tab3).get().isVisible());
        Assertions.assertTrue(this.tabs.getTabHeader(tab1).isPresent());
        Assertions.assertTrue(this.tabs.getTabHeader(tab1).get().isSelected());
        Assertions.assertTrue(this.tabs.getTabHeader(tab2).isPresent());
        Assertions.assertFalse(this.tabs.getTabHeader(tab2).get().isSelected());
        Assertions.assertTrue(this.tabs.getTabHeader(tab3).isPresent());
        Assertions.assertFalse(this.tabs.getTabHeader(tab3).get().isSelected());

        this.tabs.setValue(tab3);
        Assertions.assertTrue(this.tabs.getTabContents(tab1).isPresent());
        Assertions.assertFalse(this.tabs.getTabContents(tab1).get().isVisible());
        Assertions.assertTrue(this.tabs.getTabContents(tab2).isPresent());
        Assertions.assertFalse(this.tabs.getTabContents(tab2).get().isVisible());
        Assertions.assertTrue(this.tabs.getTabContents(tab3).isPresent());
        Assertions.assertTrue(this.tabs.getTabContents(tab3).get().isVisible());
        Assertions.assertTrue(this.tabs.getTabHeader(tab1).isPresent());
        Assertions.assertFalse(this.tabs.getTabHeader(tab1).get().isSelected());
        Assertions.assertTrue(this.tabs.getTabHeader(tab2).isPresent());
        Assertions.assertFalse(this.tabs.getTabHeader(tab2).get().isSelected());
        Assertions.assertTrue(this.tabs.getTabHeader(tab3).isPresent());
        Assertions.assertTrue(this.tabs.getTabHeader(tab3).get().isSelected());
    }

    @Test
    public void testVisibilityOfTabContentsAndSelectedTabHeadersWithRemovingTabHandler() {
        this.tabs.setTabHandler(TabHandlers.REMOVING_HANDLER);
        final String tab1 = "tab1", tab2 = "tab2", tab3 = "tab3";
        this.tabs.addTab(tab1, tab2, tab3);
        Assertions.assertTrue(this.tabs.getTabContents(tab1).isPresent());
        Assertions.assertTrue(this.tabs.getTabContents(tab1).get().getParent().isPresent());
        Assertions.assertTrue(this.tabs.getTabContents(tab2).isPresent());
        Assertions.assertFalse(this.tabs.getTabContents(tab2).get().getParent().isPresent());
        Assertions.assertTrue(this.tabs.getTabContents(tab3).isPresent());
        Assertions.assertFalse(this.tabs.getTabContents(tab3).get().getParent().isPresent());
        Assertions.assertTrue(this.tabs.getTabHeader(tab1).isPresent());
        Assertions.assertTrue(this.tabs.getTabHeader(tab1).get().isSelected());
        Assertions.assertTrue(this.tabs.getTabHeader(tab2).isPresent());
        Assertions.assertFalse(this.tabs.getTabHeader(tab2).get().isSelected());
        Assertions.assertTrue(this.tabs.getTabHeader(tab3).isPresent());
        Assertions.assertFalse(this.tabs.getTabHeader(tab3).get().isSelected());

        this.tabs.setValue(tab3);
        Assertions.assertTrue(this.tabs.getTabContents(tab1).isPresent());
        Assertions.assertFalse(this.tabs.getTabContents(tab1).get().getParent().isPresent());
        Assertions.assertTrue(this.tabs.getTabContents(tab2).isPresent());
        Assertions.assertFalse(this.tabs.getTabContents(tab2).get().getParent().isPresent());
        Assertions.assertTrue(this.tabs.getTabContents(tab3).isPresent());
        Assertions.assertTrue(this.tabs.getTabContents(tab3).get().getParent().isPresent());
        Assertions.assertTrue(this.tabs.getTabHeader(tab1).isPresent());
        Assertions.assertFalse(this.tabs.getTabHeader(tab1).get().isSelected());
        Assertions.assertTrue(this.tabs.getTabHeader(tab2).isPresent());
        Assertions.assertFalse(this.tabs.getTabHeader(tab2).get().isSelected());
        Assertions.assertTrue(this.tabs.getTabHeader(tab3).isPresent());
        Assertions.assertTrue(this.tabs.getTabHeader(tab3).get().isSelected());
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
            Assertions.assertTrue(this.tabs.getTabContents(value).isPresent());
            Assertions.assertTrue(this.tabs.getTabContents(value).get().getParent().isPresent());
            contents.put(value, this.tabs.getTabContents(value).get());
            Assertions.assertEquals(value.equals(tab1), this.tabs.getTabContents(value).get().getElement().getClassList().contains("selected-tab"), "contents for tab1 should have a selected-tab class name");
        }

        this.tabs.setTabHandler(TabHandlers.REMOVING_HANDLER);
        // no value change should happen
        Assertions.assertEquals(0, this.eventCount);

        // all components should still be the same
        // but, only the selected one should have a parent (others should not)
        // also, none of them should have a selected class name
        for(String value: new String[]{tab1, tab2, tab3}) {
            Assertions.assertTrue(this.tabs.getTabContents(value).isPresent());
            Assertions.assertSame(contents.get(value), this.tabs.getTabContents(value).get());
            Assertions.assertEquals(value.equals(tab1), this.tabs.getTabContents(value).get().getParent().isPresent(), "only tab1 should have a parent");
            Assertions.assertFalse(this.tabs.getTabContents(value).get().getElement().getClassList().contains("selected-tab"));
        }
    }

    @Test
    public void testTabSetSelected() {
        final String tabTitle = "foo";
        this.tabs.addTab("something", "anything", tabTitle, "another thing");
        Assertions.assertNotEquals(tabTitle, this.tabs.getValue());
        final Optional<Tab> perhapsHeader = this.tabs.getTabHeader(tabTitle);
        Assertions.assertTrue(perhapsHeader.isPresent());
        final Tab tab = perhapsHeader.get();
        tab.setSelected(true);
        Assertions.assertEquals(tabTitle, this.tabs.getValue());

        final Tab notThere = new Tab("oh wow");
        notThere.setSelected(true);
        Assertions.assertEquals(tabTitle, this.tabs.getValue());
    }

}