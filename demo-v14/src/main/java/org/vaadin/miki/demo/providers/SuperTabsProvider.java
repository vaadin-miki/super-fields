package org.vaadin.miki.demo.providers;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.superfields.tabs.SuperTabs;

import java.util.function.Supplier;

/**
 * Provides {@link SuperTabs}.
 * @author miki
 * @since 2020-11-17
 */
public class SuperTabsProvider implements ComponentProvider<SuperTabs<String>> {

    @Override
    public SuperTabs<String> getComponent() {
        return new SuperTabs<String>((Supplier<HorizontalLayout>) HorizontalLayout::new)
                .withTabContentGenerator(s -> new Paragraph("Did you know? All SuperFields are "+s))
                .withItems(
                        "Java friendly", "Super-configurable", "Open source",
                        "Fun to use", "Reasonably well documented"
                ).withId("super-tabs");
    }

}
