package org.vaadin.miki.markers;

import com.vaadin.flow.component.icon.Icon;

/**
 * Mixin interface to allow chaining {@link #setIcon(Icon)}.
 * @param <SELF> Self type.
 * @author miki
 * @since 2020-07-07
 */
public interface WithIconMixin<SELF extends HasIcon> extends HasIcon {

    /**
     * Chains {@link #setIcon(Icon)} and returns itself.
     * @param icon Icon to set.
     * @return This.
     * @see #setIcon(Icon)
     */
    @SuppressWarnings("unchecked")
    default SELF withIcon(Icon icon) {
        this.setIcon(icon);
        return (SELF)this;
    }

}
