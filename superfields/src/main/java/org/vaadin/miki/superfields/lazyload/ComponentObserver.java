package org.vaadin.miki.superfields.lazyload;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@JsModule("./component-observer.js")
@Tag("component-observer")
public class ComponentObserver extends PolymerTemplate<ComponentObserver.ComponentObserverModel> {

    public static class ComponentObserverModel implements TemplateModel {}

    private static final String JS_VARIABLE_NAME = "'intersection_observer'";

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentObserver.class);

    private final Set<Component> observedComponents = new HashSet<>();

    public ComponentObserver(double... visibilityRanges) {
        this(null, "0px", visibilityRanges);
    }

    public ComponentObserver(Component viewportRoot, String rootMargin, double... visibilityRanges) {
        if(visibilityRanges == null || visibilityRanges.length == 0)
            visibilityRanges = new double[]{0.0d, 1.0d};

        final Element rootElement = viewportRoot == null ? null : viewportRoot.getElement();

        this.getElement().executeJs("this.set("+JS_VARIABLE_NAME+"," +
                " new IntersectionObserver(" +
                " (changes, observer) => changes.forEach( change => this.$server.componentStatusChanged(change.target, change.intersectionRatio)) ," +
                " {root: "+(rootElement == null ? "null" : "$0")+", rootMargin: '"+rootMargin+"', threshold: ["+ Arrays.stream(visibilityRanges).mapToObj(String::valueOf).collect(Collectors.joining(", "))+"]}" +
                ")" +
                ")", rootElement);
    }

    @ClientCallable
    private void componentStatusChanged(Element element, double range) {
        this.observedComponents.stream().filter(component -> component.getElement().equals(element)).findFirst().ifPresent(component ->
                LOGGER.warn("component {} changed visibility to {}", component, range)
        );
    }

    public void observe(Component... components) {
        for (Component component : components)
            if(this.observedComponents.add(component))
                this.getElement().executeJs("this.get("+JS_VARIABLE_NAME+").observe($0)", component.getElement());
    }

    public void unobserve(Component... components) {
        for (Component component : components)
            if(this.observedComponents.remove(component))
                this.getElement().executeJs("this.get("+JS_VARIABLE_NAME+").unobserve($0)", component.getElement());
    }

}
