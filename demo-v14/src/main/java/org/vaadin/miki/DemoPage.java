package org.vaadin.miki;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

/**
 * Page that shows a demo of a component.
 * @author miki
 * @since 2020-07-04
 */
@Route(value = "demo", layout = MainLayout.class)
public class DemoPage extends VerticalLayout implements HasUrlParameter<String>, HasDynamicTitle {

    private final DemoComponentFactory demoComponentFactory = DemoComponentFactory.get();

    private Class<? extends Component> componentType;

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        this.removeAll();
        this.demoComponentFactory.getDemoableComponentTypes().stream()
                .filter(type -> type.getSimpleName().equalsIgnoreCase(parameter))
                .findFirst().ifPresentOrElse(this::buildDemoPageFor, this::buildErrorPage);
    }

    private void buildDemoPageFor(Class<? extends Component> type) {
        this.componentType = type;
        this.add(this.demoComponentFactory.buildDemoPageFor(type));
    }

    private void buildErrorPage() {
        this.componentType = null;
        this.add(new Span("You are seeing this because there was a problem in navigating to the demo page for your selected component."));
    }

    @Override
    public String getPageTitle() {
        return this.componentType == null ? "SuperFields - Error page" : "SuperFields - Demo for "+this.componentType.getSimpleName();
    }
}
