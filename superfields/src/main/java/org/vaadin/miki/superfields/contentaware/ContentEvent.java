package org.vaadin.miki.superfields.contentaware;

import com.vaadin.flow.component.ComponentEvent;

/**
 * Event related to content aware components.
 * @author miki
 * @since 2021-01-29
 */
public class ContentEvent extends ComponentEvent<ContentAware> {
    public ContentEvent(ContentAware source, boolean fromClient) {
        super(source, fromClient);
    }
}
