package org.vaadin.miki.events.clipboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.shared.Registration;

/**
 * Marker interface for objects that broadcast {@link ClipboardCopyEvent}s.
 *
 * @param <C> Component type.
 * @author miki
 * @since 2026-11-02
 */
@FunctionalInterface
public interface ClipboardCopyNotifier<C extends Component> {

  /**
   * Adds a {@link ClipboardCopyListener}.
   *
   * @param listener Listener.
   * @return A {@link Registration}.
   */
  Registration addClipboardCopyListener(ClipboardCopyListener<C> listener);

}
