package org.vaadin.miki.markers;

import com.vaadin.flow.component.Component;

/**
 * Mixin interface for {@link HasComponentAsIcon}.
 * @param <SELF> Self type.
 * @author miki
 * @since 2020-07-07
 */
public interface WithComponentAsIconMixin<SELF extends HasComponentAsIcon> extends HasComponentAsIcon {

    /**
     * Chains {@link #setIcon(Component)} and returns itself.
     * @param icon Icon to set. Can be {@code null}.
     * @return This.
     * @see #setIcon(Component)
     */
    @SuppressWarnings("unchecked")
    default SELF withIcon(Component icon) {
        this.setIcon(icon);
        return (SELF)this;
    }

}
