package org.vaadin.miki;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;

import java.util.HashMap;
import java.util.Map;

/**
 * Main layout of the application.
 * @author miki
 * @since 2020-07-04
 */
@CssImport("./styles/demo-styles.css")
@CssImport(value = "./styles/super-number-fields-styles.css", themeFor = "vaadin-text-field")
@CssImport(value = "./styles/super-tabs-styles.css", themeFor = "vaadin-tabs")
public class MainLayout extends VerticalLayout implements RouterLayout, AfterNavigationObserver {

    private final Tabs navigationTabs = new Tabs();

    private final Map<String, Tab> tabs = new HashMap<>();

    private final DemoComponentFactory demoComponentFactory = DemoComponentFactory.get();

    public MainLayout() {
        // set up tabs
        this.navigationTabs.setWidthFull();
        final RouterLink infoLink = new RouterLink();
        infoLink.setRoute(InfoPage.class);
        final Icon icon = new Icon(VaadinIcon.INFO_CIRCLE);
        icon.setSize("16px");
        icon.setColor("blue");
        icon.addClassName("tab-icon");
        infoLink.add(icon, new Span("SuperFields demo"));
        this.navigationTabs.add(new Tab(infoLink));
        this.demoComponentFactory.getDemoableComponentTypes().stream().map(type -> {
            Tab tab = new Tab(new RouterLink(type.getSimpleName(), DemoPage.class, type.getSimpleName().toLowerCase()));
            this.tabs.put(type.getSimpleName().toLowerCase(), tab);
            return tab;
        }).forEach(this.navigationTabs::add);
        this.add(this.navigationTabs);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        if(event.getLocation().getPath().isEmpty())
            this.navigationTabs.setSelectedIndex(0);
        else
            this.navigationTabs.setSelectedTab(this.tabs.get(event.getLocation().getSegments().get(1)));
    }
}
