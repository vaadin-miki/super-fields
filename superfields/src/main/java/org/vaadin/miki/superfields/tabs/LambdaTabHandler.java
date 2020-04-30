package org.vaadin.miki.superfields.tabs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.function.SerializableTriConsumer;

/**
 * Implementation of {@link TabHandler} that relies on lambdas.
 * @author miki
 * @since 2020-04-30
 */
public final class LambdaTabHandler implements TabHandler {
    private final SerializableTriConsumer<Tab, Component, HasComponents> onAdd;
    private final SerializableTriConsumer<Tab, Component, HasComponents> onRemove;
    private final SerializableTriConsumer<Tab, Component, HasComponents> onSelect;
    private final SerializableTriConsumer<Tab, Component, HasComponents> onDeselect;

    /**
     * Creates this object with given delegates.
     * @param onAdd Implementation of {@link #tabAdded(Tab, Component, HasComponents)}.
     * @param onRemove Implementation of {@link #tabRemoved(Tab, Component, HasComponents)}.
     * @param onSelect Implementation of {@link #tabSelected(Tab, Component, HasComponents)}.
     * @param onDeselect Implementation of {@link #tabDeselected(Tab, Component, HasComponents)}.
     */
    public LambdaTabHandler(SerializableTriConsumer<Tab, Component, HasComponents> onAdd, SerializableTriConsumer<Tab, Component, HasComponents> onRemove, SerializableTriConsumer<Tab, Component, HasComponents> onSelect, SerializableTriConsumer<Tab, Component, HasComponents> onDeselect) {
        this.onAdd = onAdd;
        this.onRemove = onRemove;
        this.onSelect = onSelect;
        this.onDeselect = onDeselect;
    }

    @Override
    public void tabAdded(Tab tabHeader, Component tabContents, HasComponents contentsContainer) {
        this.onAdd.accept(tabHeader, tabContents, contentsContainer);
    }

    @Override
    public void tabRemoved(Tab tabHeader, Component tabContents, HasComponents contentsContainer) {
        this.onRemove.accept(tabHeader, tabContents, contentsContainer);
    }

    @Override
    public void tabSelected(Tab tabHeader, Component tabContents, HasComponents contentsContainer) {
        this.onSelect.accept(tabHeader, tabContents, contentsContainer);
    }

    @Override
    public void tabDeselected(Tab tabHeader, Component tabContents, HasComponents contentsContainer) {
        this.onDeselect.accept(tabHeader, tabContents, contentsContainer);
    }
}
