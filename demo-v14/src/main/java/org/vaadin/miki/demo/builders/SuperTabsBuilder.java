package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.superfields.tabs.SuperTabs;
import org.vaadin.miki.superfields.tabs.TabHandler;
import org.vaadin.miki.superfields.tabs.TabHandlers;

import java.util.function.Consumer;

/**
 * Builds content for {@link SuperTabs}.
 * @author miki
 * @since 2020-11-19
 */
public class SuperTabsBuilder implements ContentBuilder<SuperTabs<?>> {

    @Override
    public void buildContent(SuperTabs<?> component, Consumer<Component[]> callback) {
        final Checkbox multilineTabs = new Checkbox("Multiline tabs?", event -> component.setMultiline(event.getValue()));

        final ComboBox<TabHandler> tabHandlers = new ComboBox<>("Select a tab handler: ",
                TabHandlers.VISIBILITY_HANDLER, TabHandlers.REMOVING_HANDLER, TabHandlers.selectedContentHasClassName("selected-tab"));
        tabHandlers.addValueChangeListener(event -> {
            if(event.getValue() != null)
                component.setTabHandler(event.getValue());
        });

        callback.accept(new Component[]{multilineTabs, tabHandlers});
    }
}
