package org.vaadin.miki.superfields.text;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.shared.Registration;

/**
 * Marker interface for objects that broadcast {@link TextSelectionEvent}.
 * @author miki
 * @since 2020-05-30
 */
@FunctionalInterface
public interface TextSelectionNotifier<T extends Component & CanSelectText> {

    /**
     * Adds the listener.
     * @param listener A listener to add.
     * @return A {@link Registration} that can be used to stop listening to the event.
     */
    Registration addTextSelectionListener(TextSelectionListener<T> listener);

}
