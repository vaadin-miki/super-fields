package org.vaadin.miki.events.text;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import org.vaadin.miki.markers.CanSelectText;

/**
 * Marker interface for objects
 * @param <C> Component type.
 * @author miki
 * @since 2020-05-30
 */
public interface TextSelectionListener<C extends Component & CanSelectText> extends ComponentEventListener<TextSelectionEvent<C>> {
}
