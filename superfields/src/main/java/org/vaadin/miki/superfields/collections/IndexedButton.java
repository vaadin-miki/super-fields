package org.vaadin.miki.superfields.collections;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import org.vaadin.miki.markers.Clickable;
import org.vaadin.miki.markers.HasIndex;
import org.vaadin.miki.markers.WithIndexMixin;
import org.vaadin.miki.markers.WithTextMixin;

/**
 * An indexed {@link Button}, i.e. one that implements {@link HasIndex}.
 *
 * @author miki
 * @since 2021-08-30
 */
public class IndexedButton extends Button implements WithIndexMixin<IndexedButton>, WithTextMixin<IndexedButton>, Clickable {

    public static final int DEFAULT_INDEX = -1;

    private int index = DEFAULT_INDEX;

    public IndexedButton() {
        super();
    }

    public IndexedButton(String text) {
        super(text);
    }

    public IndexedButton(Component icon) {
        super(icon);
    }

    public IndexedButton(String text, Component icon) {
        super(text, icon);
    }

    public IndexedButton(String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, clickListener);
    }

    public IndexedButton(Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(icon, clickListener);
    }

    public IndexedButton(String text, Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, icon, clickListener);
    }

    /**
     * Creates the button with a given initial index.
     * @param startingIndex Initial index of the button.
     */
    public IndexedButton(int startingIndex) {
        super();
        this.setIndex(startingIndex);
    }

    /**
     * Creates the button with a given text, starting index and a click listener.
     * @param text Initial text.
     * @param startingIndex Initial index of the button.
     * @param listener Listener to be called on click.
     */
    public IndexedButton(String text, int startingIndex, ComponentEventListener<ClickEvent<Button>> listener) {
        this(text, listener);
        this.setIndex(startingIndex);
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public final void setIndex(int index) {
        this.index = index;
        this.setEnabled(index > DEFAULT_INDEX);
    }

    @Override
    @SuppressWarnings("squid:S1185") // without this method the code does not compile
    public String getText() {
        return super.getText();
    }
}
