package org.vaadin.miki.markers;

/**
 * Mixin interface to support chaining {@link #setLabel(String)}.
 * @param <SELF> Self type.
 * @author miki
 * @since 2020-04-12
 */
@SuppressWarnings("squid:S119") // SELF is a fine generic name that is more descriptive than S
public interface WithLabelMixin<SELF extends WithLabelMixin<SELF>> extends HasLabel {

    /**
     * Chains {@link #setLabel(String)} and returns itself.
     * @param label Label to set.
     * @return This.
     * @see #setLabel(String)
     */
    @SuppressWarnings("unchecked")
    default SELF withLabel(String label) {
        this.setLabel(label);
        return (SELF)this;
    }
}
