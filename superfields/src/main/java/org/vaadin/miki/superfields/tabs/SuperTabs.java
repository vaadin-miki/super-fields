package org.vaadin.miki.superfields.tabs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import org.vaadin.miki.markers.HasLabel;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Tab sheet component that also is a field.
 * @param <T> Type of value.
 * @author miki
 * @since 2020-04-10
 */
@Tag("super-tabs")
public class SuperTabs<T> extends CustomField<T> implements HasLabel, HasStyle {

    private final Tabs tabs = new Tabs();

    private final Div contents = new Div();

    private final Map<Tab, Component> tabsToContents = new HashMap<>();

    private final List<Map.Entry<T, Tab>> values = new ArrayList<>();

    private TabHeaderGenerator<T> tabHeaderGenerator;

    private TabContentGenerator<T> tabContentGenerator;

    private boolean customValueAllowed = false;

    /**
     * Creates the component with no tabs and default {@link TabHeaderGenerator} and {@link TabContentGenerator}.
     */
    public SuperTabs() {
        this(null);
    }

    /**
     * Creates the component with given {@link TabContentGenerator} and tabs, but with default {@link TabHeaderGenerator}.
     * @param tabContentGenerator {@link TabContentGenerator} to construct content for tabs.
     * @param values Values to use.
     */
    @SafeVarargs
    public SuperTabs(TabContentGenerator<T> tabContentGenerator, T... values) {
        this(null, tabContentGenerator, values);
    }

    /**
     * Creates the component with given {@link TabHeaderGenerator}, {@link TabContentGenerator} and tabs.
     * @param tabHeaderGenerator {@link TabHeaderGenerator} to construct tab headers.
     * @param tabContentGenerator {@link TabContentGenerator} to construct content for tabs.
     * @param values Values to use.
     */
    @SafeVarargs
    public SuperTabs(TabHeaderGenerator<T> tabHeaderGenerator, TabContentGenerator<T> tabContentGenerator, T... values) {
        this(null, tabHeaderGenerator, tabContentGenerator, values);
    }

    /**
     * Creates the component with given {@link TabHeaderGenerator}, {@link TabContentGenerator} and tabs, overriding the default value from {@code null} to the one provided.
     * @param defaultValue Default value that corresponds to no tab selected.
     * @param tabHeaderGenerator {@link TabHeaderGenerator} to construct tab headers.
     * @param tabContentGenerator {@link TabContentGenerator} to construct content for tabs.
     * @param values Values to use.
     */
    @SafeVarargs
    public SuperTabs(T defaultValue, TabHeaderGenerator<T> tabHeaderGenerator, TabContentGenerator<T> tabContentGenerator, T... values) {
        super(defaultValue);
        this.setTabHeaderGenerator(tabHeaderGenerator);
        this.setTabContentGenerator(tabContentGenerator);
        this.tabs.setAutoselect(false);
        this.tabs.setWidthFull();
        this.contents.setWidthFull();
        this.add(this.tabs, this.contents);
        this.tabs.addSelectedChangeListener(this::onTabChanged);
        this.addTab(values);
    }

    private void onTabChanged(Tabs.SelectedChangeEvent event) {
        // hide previous contents
        if(event.getPreviousTab() != null && this.tabsToContents.containsKey(event.getPreviousTab()))
            this.tabsToContents.get(event.getPreviousTab()).setVisible(false);
        // show new contents
        if(event.getSelectedTab() != null && this.tabsToContents.containsKey(event.getSelectedTab()))
            this.tabsToContents.get(event.getSelectedTab()).setVisible(true);
        // trigger value change
        this.updateValue();
    }

    /**
     * Adds a new tab and content for given value, using {@link #getTabHeaderGenerator()} and {@link #getTabContentGenerator()} to produce components.
     * @param value A new value, not yet present in the component. Warning: no check are made to make sure this value is not used.
     * @param select Whether or not to select newly added tab.
     */
    protected void addNewTab(T value, boolean select) {
        Tab tab = this.getTabHeaderGenerator().generateTab(value);
        Component content = this.getTabContentGenerator().generateComponent(value);
        this.addNewTab(value, tab, content, select);
    }

    /**
     * Adds a new tab and content for given value.
     * @param value A new value, not yet present in the component. Warning: no check are made to make sure this value is not used.
     * @param tab Tab corresponding to the value.
     * @param content Content corresponding to the value.
     * @param select Whether or not to select newly added tab.
     */
    protected void addNewTab(T value, Tab tab, Component content, boolean select) {
        this.tabsToContents.put(tab, content);
        this.values.add(new AbstractMap.SimpleImmutableEntry<>(value, tab));
        this.tabs.add(tab);
        content.setVisible(false);
        this.contents.add(content);
        if(select) {
            if (!Objects.equals(this.tabs.getSelectedTab(), tab))
                this.tabs.setSelectedTab(tab);
            else
                this.updateValue();
        }
    }

    /**
     * This method adds one or more tabs. It converts the values into a {@link Collection} and calls {@link #addTabs(Collection)}.
     * @param values Values to add tabs and contents for.
     * @see #addTabs(Collection)
     */
    @SafeVarargs
    public final void addTab(T... values) {
        this.addTabs(Arrays.asList(values));
    }

    /**
     * Adds a tab for each of the given values. Selects the tab if it was already present, otherwise just adds it.
     * Will use {@link #getTabHeaderGenerator()} and {@link #getTabContentGenerator()} to produce components.
     * If there were no tabs prior to calling this method, the first tab added this way will be selected.
     * @param values Values to add tabs and contents for.
     */
    public void addTabs(Collection<T> values) {
        values.forEach(value ->
            this.getValueAndTab(value).ifPresentOrElse(
                    e -> this.tabs.setSelectedTab(e.getValue()),
                    () -> this.addNewTab(value, this.values.isEmpty())
            ));
    }

    /**
     * Adds a tab for a given value. Selects the tab if it was already present, otherwise just adds it.
     * This will *not* overwrite the existing tab. If you want that, remove the tab first.
     * If this is the first tab added, it will be selected.
     * @param value A value that corresponds to provided tab header and tab contents.
     * @param tabHeader Header.
     * @param tabContents Contents.
     */
    public void addTab(T value, Tab tabHeader, Component tabContents) {
        this.getValueAndTab(value).ifPresentOrElse(
                e -> this.tabs.setSelectedTab(e.getValue()),
                () -> this.addNewTab(value, tabHeader, tabContents, this.values.isEmpty())
        );
    }

    /**
     * Removes an existing tab, its header and contents. Makes no check on whether the tab is really there.
     * @param value Value to remove.
     * @param header Header being removed.
     */
    protected void removeExistingTab(T value, Tab header) {
        Component component = this.tabsToContents.get(header);
        this.contents.remove(component);
        if(Objects.equals(header, this.tabs.getSelectedTab()))
            this.tabs.setSelectedTab(null);
        this.tabs.remove(header);
        this.values.removeIf(e -> Objects.equals(value, e.getKey()));
    }

    /**
     * Removes the tab for a given value, if found.
     * If the tab was selected, there will be no selection active.
     * @param value Value to remove the tab for.
     */
    public void removeTab(T value) {
        this.getValueAndTab(value).ifPresent(e -> this.removeExistingTab(e.getKey(), e.getValue()));
    }

    /**
     * Finds the value and tab entry corresponding to the given {@code value}.
     * @param value A value to look for.
     * @return Value and its matching {@link Tab}, if any.
     */
    protected Optional<Map.Entry<T, Tab>> getValueAndTab(T value) {
        return this.values.stream().filter(e -> Objects.equals(value, e.getKey())).findFirst();
    }

    /**
     * Returns tab header that corresponds to the given value.
     * @param value Value to look for.
     * @return A {@link Tab} that corresponds to the value, if any.
     */
    public Optional<Tab> getTabHeader(T value) {
        return this.getValueAndTab(value).map(Map.Entry::getValue);
    }

    /**
     * Returns contents that corresponds to the given value.
     * Note: {@link Component#setVisible(boolean)} calls on the result are discouraged ;)
     * @param value Value to look for.
     * @return A {@link Component} that corresponds to the value, if any.
     */
    public Optional<Component> getTabContents(T value) {
        return this.getValueAndTab(value).map(e -> this.tabsToContents.get(e.getValue()));
    }

    @Override
    protected T generateModelValue() {
        if(this.tabs.getSelectedIndex() < this.values.size() && this.tabs.getSelectedIndex() >= 0)
            return this.values.get(this.tabs.getSelectedIndex()).getKey();
        else return this.getEmptyValue();
    }

    @Override
    protected void setPresentationValue(T t) {
        this.values.stream().filter(e -> Objects.equals(t, e.getKey())).findFirst().ifPresentOrElse(
                e -> this.tabs.setSelectedTab(e.getValue()),
                () -> {
                    if(this.isCustomValueAllowed())
                        addNewTab(t, true);
                    else this.updateValue();
                }
        );
    }

    /**
     * Returns the values that correspond to tabs.
     * @return A collection of values. Modifying the returned collection has no effect on this component.
     * @see #getTabHeader(Object)
     * @see #getTabContents(Object)
     */
    public List<T> getValues() {
        return this.values.stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

    /**
     * Returns the number of values that currently correspond to tabs.
     * @return A number of values.
     */
    public int size() {
        return this.values.size();
        // note: there cannot be a helper method .isEmpty() for checking size == 0
        // that is because there already exists isEmpty in CustomField, and it checks for value being empty
    }

    /**
     * Sets the generator responsible for providing content for selected tabs.
     * It is called first time a tab is displayed. When the generator is changed, the new one has no effect on previously generated content.
     * @param tabContentGenerator {@link TabContentGenerator}. If {@code null}, it will be replaced with a default function.
     */
    public void setTabContentGenerator(TabContentGenerator<T> tabContentGenerator) {
        this.tabContentGenerator = Optional.ofNullable(tabContentGenerator).orElse(t -> new Span(String.valueOf(t)));
    }

    /**
     * Returns the generator responsible for providing content for selected tabs.
     * @return {@link TabContentGenerator}.
     */
    public TabContentGenerator<T> getTabContentGenerator() {
        return this.tabContentGenerator;
    }

    /**
     * Sets the generator responsible for providing tabs (headers) for values.
     * It is called first time a tab is created. When the generator is changed, the new one has no effect on previously generated tabs.
     * @param tabHeaderGenerator {@link TabHeaderGenerator}. If {@code null}, it will be replaced with a default function.
     */
    public void setTabHeaderGenerator(TabHeaderGenerator<T> tabHeaderGenerator) {
        this.tabHeaderGenerator = Optional.ofNullable(tabHeaderGenerator).orElse(t -> new Tab(String.valueOf(t)));
    }

    /**
     * Returns the generator responsible for providing tabs (headers) for values.
     * @return {@link TabContentGenerator}.
     */
    public TabHeaderGenerator<T> getTabHeaderGenerator() {
        return this.tabHeaderGenerator;
    }

    /**
     * Whether or not custom values may be set through {@link #setValue(Object)}.
     * @return {@code true} when selecting a value that does not have a corresponding tab results in adding a new tab, {@code false} otherwise (and by default).
     */
    public boolean isCustomValueAllowed() {
        return this.customValueAllowed;
    }

    /**
     * Allows or disallows creating new tabs when calling {@link #setValue(Object)}.
     * @param customValueAllowed when {@code true}, selecting a value that does not have a corresponding tab will result in adding and selecting a new tab; {@code false} will ignore setting the value.
     */
    public void setCustomValueAllowed(boolean customValueAllowed) {
        this.customValueAllowed = customValueAllowed;
    }
}