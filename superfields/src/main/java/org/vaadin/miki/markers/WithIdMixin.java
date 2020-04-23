package org.vaadin.miki.markers;

import com.vaadin.flow.component.HasElement;

/**
 * Mixin interface to allow chaining setting id.
 * @param <SELF> Self type.
 * @author miki
 * @since 2020-04-22
 */
public interface WithIdMixin<SELF extends HasElement> extends HasElement {

    @SuppressWarnings("unchecked")
    default SELF withId(String id) {
        this.getElement().setAttribute("id", id);
        return (SELF)this;
    }

}
