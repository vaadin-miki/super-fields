package org.vaadin.miki.events.clipboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;

/**
 * Marker interface for objects that listen to {@link ClipboardCopyEvent}s.
 *
 * @param <C> Source component.
 * @author miki
 * @since 2026-11-02
 */
@FunctionalInterface
public interface ClipboardCopyListener<C extends Component> extends ComponentEventListener<ClipboardCopyEvent<C>> {
}
