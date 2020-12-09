package org.vaadin.miki.markers;

/**
 * Mixin interface to allow chaining {@link #setMaximumSelectionSize(int)}.
 * @param <SELF> Self type.
 * @author miki
 * @since 2020-12-09
 */
public interface WithMaximumSelectionSize<SELF extends HasMaximumSelectionSize> extends HasMaximumSelectionSize {

    /**
     * Chains {@link #setMaximumSelectionSize(int)} and returns itself.
     * @param maximumSelectionSize Maximum allowed selection size.
     * @return This.
     * @see #setMaximumSelectionSize(int)
     */
    @SuppressWarnings("unchecked")
    default SELF withMaximumSelectionSize(int maximumSelectionSize) {
        setMaximumSelectionSize(maximumSelectionSize);
        return (SELF)this;
    }
}
