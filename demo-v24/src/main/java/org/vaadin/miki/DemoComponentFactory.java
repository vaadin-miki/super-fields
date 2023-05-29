package org.vaadin.miki;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.internal.ReflectTools;
import org.atteo.classindex.ClassIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.NeedsValidatorStorage;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.demo.ValidatorStorage;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

/**
 * Builds demo pages for components.
 * @author miki
 * @since 2020-07-04
 */
public final class DemoComponentFactory implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoComponentFactory.class);

    private static final Comparator<Class<?>> CLASS_ORDER_COMPARATOR = Comparator.comparingInt(t -> t.isAnnotationPresent(Order.class) ? t.getAnnotation(Order.class).value() : Integer.MAX_VALUE - t.getSimpleName().charAt(0));

    private static boolean isNotInterface(Class<?> type) {
        return !type.isInterface();
    }

    public static DemoComponentFactory get() {
        // rolling back to creating a new instance every time, because of some threading issues
        return new DemoComponentFactory();
    }

    private final Map<Class<? extends Component>, Component> components = new LinkedHashMap<>();

    private final Map<Class<?>, ContentBuilder<?>> contentBuilders = new LinkedHashMap<>();

    @SuppressWarnings({"unchecked", "squid:S3740", "rawtypes"}) // validator type should be ok
    private DemoComponentFactory() {
        final ValidatorStorage storage = new ValidatorStorage();

        StreamSupport.stream(ClassIndex.getSubclasses(ComponentProvider.class).spliterator(), false)
                .filter(DemoComponentFactory::isNotInterface)
                .sorted(CLASS_ORDER_COMPARATOR)
                .forEach(type -> {
                    try {
                        final ComponentProvider<?> componentProvider = type.getDeclaredConstructor().newInstance();
                        final Component component = componentProvider.getComponent();
                        this.components.put(component.getClass(), component);
                        if (componentProvider instanceof Validator && component instanceof HasValue)
                            storage.registerValidator((HasValue<?, ?>) component).accept((Validator) componentProvider);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        LOGGER.error("creating a component from {} failed due to error {} with message {}", type.getSimpleName(), e.getClass().getSimpleName(), e.getMessage());
                    }
                });

        StreamSupport.stream(ClassIndex.getSubclasses(ContentBuilder.class).spliterator(), false)
                .filter(DemoComponentFactory::isNotInterface)
                .sorted(CLASS_ORDER_COMPARATOR)
                .forEach(type -> {
                    try {
                        final ContentBuilder<?> contentBuilder = type.getDeclaredConstructor().newInstance();
                        final Class<?> suitableType = ReflectTools.getGenericInterfaceType(contentBuilder.getClass(), ContentBuilder.class);
                        this.contentBuilders.put(suitableType, contentBuilder);
                        if (contentBuilder instanceof NeedsValidatorStorage)
                            ((NeedsValidatorStorage)contentBuilder).setValidatorStorage(storage);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        LOGGER.error("creating a content builder from {} failed due to error {} with message {}", type.getSimpleName(), e.getClass().getSimpleName(), e.getMessage());
                    }
                });
    }

    /**
     * Returns the collection of {@link Component} types.
     * @return Unmodifiable collection of classes.
     */
    public Collection<Class<? extends Component>> getDemoableComponentTypes() {
        return Collections.unmodifiableCollection(this.components.keySet());
    }

    /**
     * Builds the page for a given component type.
     * @param type Type of component to build page for.
     * @return A demo page.
     */
    public Component buildDemoPageFor(Class<? extends Component> type) {
        final Component component = this.components.get(type);
        component.getElement().getClassList().add("demo");
        final Div result = new Div();
        result.setSizeUndefined();
        result.addClassName("component-page");
        final VerticalLayout componentSection = new VerticalLayout();
        componentSection.setSizeUndefined();
        componentSection.addClassName("component-section");
        final Span title = new Span("Demo page of "+component.getClass().getSimpleName());
        title.addClassName("section-header");
        title.addClassName("component-header");

        componentSection.add(title, component);
        result.add(componentSection);

        this.contentBuilders.entrySet().stream().
                filter(entry -> entry.getKey().isAssignableFrom(type)).
                forEach(entry -> result.add(this.buildContentFor(component, entry.getKey().getSimpleName(), entry.getValue())));
        return result;
    }

    @SuppressWarnings("unchecked") // type is checked
    private <C> Component buildContentFor(Component component, String typeName, ContentBuilder<C> builder) {
        final VerticalLayout section = new VerticalLayout();
        section.setSizeUndefined();
        section.addClassName("section-layout");
        final Span header = new Span("Configuration options for "+typeName);
        header.addClassName("section-header");
        section.add(header);
        builder.buildContent((C)component, section::add);
        return section;
    }

}
