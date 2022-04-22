package org.vaadin.miki.markers;

/**
 * Mixin interface to support chaining {@link #setTitle(String)}.
 * @param <SELF> Self type.
 * @author miki
 * @since 2020-04-12
 */
@SuppressWarnings("squid:S119") // SELF is a fine generic name that is more descriptive than S
public interface WithTitleMixin<SELF extends WithTitleMixin<SELF>> extends HasTitle {

    /**
     * Chains {@link #setTitle(String)} and returns itself.
     * @param title Title to use.
     * @return This.
     * @see #setTitle(String)
     */
    @SuppressWarnings("unchecked")
    default SELF withTitle(String title) {
        this.setTitle(title);
        return (SELF)this;
    }

}
