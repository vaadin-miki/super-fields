package org.vaadin.miki.events.clipboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;

/**
 * Marker interface for objects that listen to {@link ClipboardPasteEvent}s.
 *
 * @param <C> Source component type.
 * @author miki
 * @since 2026-11-02
 */
@FunctionalInterface
public interface ClipboardPasteListener<C extends Component> extends ComponentEventListener<ClipboardPasteEvent<C>> {
}
