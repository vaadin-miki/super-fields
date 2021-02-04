package org.vaadin.miki.superfields.contentaware;

import com.vaadin.flow.component.ComponentEvent;

/**
 * Event related to content aware components.
 * @author miki
 * @since 2021-01-29
 */
public class ContentChangeEvent extends ComponentEvent<ContentAware> {

    private final int addedNodes;
    private final int removedNodes;

    public ContentChangeEvent(ContentAware source, boolean fromClient, int addedNodes, int removedNodes) {
        super(source, fromClient);
        this.addedNodes = addedNodes;
        this.removedNodes = removedNodes;
    }

    public int getNumberOfAddedNodes() {
        return this.addedNodes;
    }

    public int getNumberOfRemovedNodes() {
        return this.removedNodes;
    }
}
