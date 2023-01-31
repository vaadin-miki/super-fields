package org.vaadin.miki.superfields.buttons;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import org.vaadin.miki.markers.Clickable;
import org.vaadin.miki.markers.HasIndex;
import org.vaadin.miki.markers.WithComponentAsIconMixin;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithIndexMixin;
import org.vaadin.miki.markers.WithTextMixin;
import org.vaadin.miki.markers.WithTooltipMixin;

/**
 * An indexed {@link Button}, i.e. one that implements {@link HasIndex}.
 *
 * @author miki
 * @since 2021-08-30
 */
public class IndexedButton extends Button
        implements WithIndexMixin<IndexedButton>, WithIdMixin<IndexedButton>,
        WithComponentAsIconMixin<IndexedButton>,
        WithTextMixin<IndexedButton>, Clickable, WithTooltipMixin<IndexedButton> {

    public static final int DEFAULT_INDEX = -1;

    private int index = DEFAULT_INDEX;

    /**
     * Creates an indexed button with {@link #DEFAULT_INDEX} as index.
     */
    public IndexedButton() {
        super();
    }

    /**
     * Creates an indexed button with {@link #DEFAULT_INDEX} as index and given text on the button.
     * @param text Text to show on the button.
     */
    public IndexedButton(String text) {
        super(text);
    }

    /**
     * Creates an indexed button with {@link #DEFAULT_INDEX} as index and an icon.
     * @param icon Icon to put on the button.
     */
    public IndexedButton(Component icon) {
        super(icon);
    }

    /**
     * Creates an indexed button with {@link #DEFAULT_INDEX} as index, given text and icon on the button.
     * @param text Text on the button.
     * @param icon Icon on the button.
     */
    public IndexedButton(String text, Component icon) {
        super(text, icon);
    }

    /**
     * Creates an indexed button with {@link #DEFAULT_INDEX} as index, given text and a listener.
     * @param text Text on the button.
     * @param clickListener Event listener.
     */
    public IndexedButton(String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, clickListener);
    }

    /**
     * Creates an indexed button with {@link #DEFAULT_INDEX} as index, given icon and a listener.
     * @param icon Icon on the button.
     * @param clickListener Event listener.
     */
    public IndexedButton(Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(icon, clickListener);
    }

    /**
     * Creates an indexed button with {@link #DEFAULT_INDEX} as index, given text and icon, and a listener.
     * @param text Text on the button.
     * @param icon Icon on the button.
     * @param clickListener Event listener.
     */
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

    /**
     * Creates the button with a given text, icon, starting index and a click listener.
     * @param text Initial text.
     * @param icon Icon.
     * @param startingIndex Initial index of the button.
     * @param listener Listener to be called on click.
     */
    public IndexedButton(String text, Component icon, int startingIndex, ComponentEventListener<ClickEvent<Button>> listener) {
        this(text, icon, listener);
        this.setIndex(startingIndex);
    }

    /**
     * Creates the button with am icon, starting index and a click listener.
     * @param icon Icon.
     * @param startingIndex Initial index of the button.
     * @param listener Listener to be called on click.
     */
    public IndexedButton(Component icon, int startingIndex, ComponentEventListener<ClickEvent<Button>> listener) {
        this(icon, listener);
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

    @Override
    public void setTitle(String title) {
        this.getElement().setProperty("title", title == null ? "" : title);
    }

    @Override
    public String getTitle() {
        final String result = this.getElement().getProperty("title");
        return result == null ? "" : result;
    }

}
