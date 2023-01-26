package org.vaadin.miki.markers;

import com.vaadin.flow.component.shared.HasTooltip;

/**
 * Mixin interface to support chaining {@link #setTooltipText(String)}.
 * @param <SELF> Self type.
 * @author jc
 * @since 2023-01-26
 */
@SuppressWarnings("squid:S119") // SELF is a fine generic name that is more descriptive than S
public interface WithTooltipMixin<SELF extends WithTooltipMixin<SELF>> extends HasTooltip {

    /**
     * Chains {@link #setTooltipText(String)} and returns itself.
     * @param title Title to use.
     * @return This.
     * @see #setTooltipText(String)
     */
    @SuppressWarnings("unchecked")
    default SELF withTooltipText(String title) {
        this.setTooltipText(title);
        return (SELF)this;
    }

}
