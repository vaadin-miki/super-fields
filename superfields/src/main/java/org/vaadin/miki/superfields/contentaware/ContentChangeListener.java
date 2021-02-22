package org.vaadin.miki.superfields.contentaware;

import com.vaadin.flow.component.ComponentEventListener;

/**
 * Marker interface for objects that listen to {@link ContentChangeEvent}s.
 * @author miki
 * @since 2021-02-04
 */
@FunctionalInterface
public interface ContentChangeListener extends ComponentEventListener<ContentChangeEvent> {
}
