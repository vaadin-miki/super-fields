package org.vaadin.miki.superfields.tabs;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.tabs.Tabs;

/**
 * An extension of {@link Tabs} with some additional styles added in the js file.
 * This class serves no other purpose and probably should not be used outside of {@link SuperTabs}.
 *
 * @author miki
 * @since 2026-01-18
 */
@JsModule("./super-tabs-header.js")
@Tag("super-tabs-header")
public class SuperTabsHeader extends Tabs {
}
