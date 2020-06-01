package org.vaadin.miki.superfields.text;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;

/**
 * Marker interface for objects
 * @param <C> Component type.
 * @author miki
 * @since 2020-05-30
 */
public interface TextSelectionListener<C extends Component & CanSelectText> extends ComponentEventListener<TextSelectionEvent<C>> {
}
