package org.vaadin.miki.events.clipboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.shared.Registration;

/**
 * Marker interface for components capable of broadcasting {@link ClipboardPasteEvent}s.
 *
 * @param <C> Component type.
 * @author miki
 * @since 2026-11-02
 */
@FunctionalInterface
public interface ClipboardPasteNotifier<C extends Component> {

  Registration addClipboardPasteListener(ClipboardPasteListener<C> listener);

}
