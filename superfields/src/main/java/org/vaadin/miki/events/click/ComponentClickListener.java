package org.vaadin.miki.events.click;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import org.vaadin.miki.markers.Clickable;

/**
 * Marker interface for objects that react to {@link ComponentClickEvent}s.
 * @author miki
 * @since 2020-07-08
 */
@FunctionalInterface
public interface ComponentClickListener<C extends Component & Clickable> extends ComponentEventListener<ComponentClickEvent<C>> {

}
