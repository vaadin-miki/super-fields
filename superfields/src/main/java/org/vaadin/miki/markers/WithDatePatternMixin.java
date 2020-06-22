package org.vaadin.miki.markers;

import org.vaadin.miki.shared.dates.DatePattern;

/**
 * Mixin interface to allow chaining {@link #setDatePattern(DatePattern)}.
 * @param <SELF> Self type.
 * @author miki
 * @since 2020-04-24
 */
public interface WithDatePatternMixin<SELF extends WithDatePatternMixin<SELF>> extends HasDatePattern {

    /**
     * Chains {@link #setDatePattern(DatePattern)} and returns itself.
     * @param pattern {@link DatePattern} to use.
     * @return This.
     * @see #setDatePattern(DatePattern)
     */
    @SuppressWarnings("unchecked")
    default SELF withDatePattern(DatePattern pattern) {
        this.setDatePattern(pattern);
        return (SELF)this;
    }
}
