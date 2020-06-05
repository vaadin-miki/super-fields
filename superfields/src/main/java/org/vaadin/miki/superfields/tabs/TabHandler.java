package org.vaadin.miki.superfields.tabs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.tabs.Tab;

import java.io.Serializable;

/**
 * Interface for objects handling the tab adding, displaying and hiding.
 *
 * @author miki
 * @since 2020-04-30
 */
public interface TabHandler extends Serializable {

    /**
     * Called when a tab has been added, but its contents have not.
     * @param tabHeader Header that was added to the tab headers.
     * @param tabContents Contents to be added.
     * @param contentsContainer Container with the contents. It also is a {@link Component}.
     */
    void tabAdded(Tab tabHeader, Component tabContents, HasComponents contentsContainer);

    /**
     * Called when a tab has been removed, but its contents have not.
     * @param tabHeader Header that was removed.
     * @param tabContents Contents to be removed.
     * @param contentsContainer Container with the contents. It also is a {@link Component}.
     */
    void tabRemoved(Tab tabHeader, Component tabContents, HasComponents contentsContainer);

    /**
     * Called when a tab has been selected, but its contents have not yet been updated.
     * @param tabHeader Header that was selected.
     * @param tabContents Contents to be selected.
     * @param contentsContainer Container with the contents. It also is a {@link Component}.
     */
    void tabSelected(Tab tabHeader, Component tabContents, HasComponents contentsContainer);

    /**
     * Called when a tab has been deselected, but its contents have not yet been updated.
     * @param tabHeader Header that was deselected.
     * @param tabContents Contents to be deselected.
     * @param contentsContainer Container with the contents. It also is a {@link Component}.
     */
    void tabDeselected(Tab tabHeader, Component tabContents, HasComponents contentsContainer);
}
