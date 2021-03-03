package org.vaadin.miki.superfields.contentaware;

import com.vaadin.flow.shared.Registration;

/**
 * Marker interface for objects that broadcast {@link ContentChangeEvent}s.
 * @author miki
 * @since 2021-02-04
 */
@FunctionalInterface
public interface ContentChangeNotifier {

    /**
     * Adds a listener to be notified whenever {@link ContentChangeEvent} happens.
     * @param listener Listener to add.
     * @return A {@link Registration} that can be used to stop listening to events.
     */
    Registration addContentChangeListener(ContentChangeListener listener);

}
