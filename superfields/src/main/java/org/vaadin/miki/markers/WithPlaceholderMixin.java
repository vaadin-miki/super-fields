package org.vaadin.miki.markers;

import com.vaadin.flow.component.HasPlaceholder;

/**
 * Mixin interface to support chaining {@link #setPlaceholder(String)}.
 * @param <SELF> Self type.
 * @author miki
 * @since 2020-04-12
 */
@SuppressWarnings("squid:S119") // SELF is a fine generic name that is more descriptive than S
public interface WithPlaceholderMixin<SELF extends WithPlaceholderMixin<SELF>> extends HasPlaceholder {

    /**
     * Chains {@link #setPlaceholder(String)} and returns itself.
     * @param placeholder Placeholder to use.
     * @return This.
     * @see #setPlaceholder(String)
     */
    @SuppressWarnings("unchecked")
    default SELF withPlaceholder(String placeholder) {
        this.setPlaceholder(placeholder);
        return (SELF)this;
    }

}
