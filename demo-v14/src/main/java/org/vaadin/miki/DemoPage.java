package org.vaadin.miki;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import java.util.HashMap;
import java.util.Map;

/**
 * Page that shows a demo of a component.
 * @author miki
 * @since 2020-07-04
 */
@Route(value = "demo", layout = MainLayout.class)
public class DemoPage extends VerticalLayout implements HasUrlParameter<String>, HasDynamicTitle {

    private final DemoComponentFactory demoComponentFactory = DemoComponentFactory.get();

    private final Map<String, Component> pages = new HashMap<>();

    private Class<? extends Component> componentType;

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        this.removeAll();

        this.add(this.pages.computeIfAbsent(parameter, s ->
                this.demoComponentFactory.getDemoableComponentTypes().stream()
                        .filter(type -> type.getSimpleName().equalsIgnoreCase(s))
                        .findFirst()
                        .map(this::buildDemoPageFor)
                        .orElseGet(this::buildErrorPage)
        ));
    }

    private Component buildDemoPageFor(Class<? extends Component> type) {
        this.componentType = type;
        return this.demoComponentFactory.buildDemoPageFor(type);
    }

    private Component buildErrorPage() {
        this.componentType = null;
        return new Span("You are seeing this because there was a problem in navigating to the demo page for your selected component.");
    }

    @Override
    public String getPageTitle() {
        return this.componentType == null ? "SuperFields - Error page" : "SuperFields - Demo for "+this.componentType.getSimpleName();
    }
}
