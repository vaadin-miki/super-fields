package org.vaadin.miki.markers;

/**
 * Mixin interface for easy chaining of {@link #setIndex(int)}.
 * @param <SELF> This.
 *
 * @author miki
 * @since 2021-08-31
 */
public interface WithIndexMixin<SELF extends HasIndex> extends HasIndex {

    /**
     * Chains {@link #setIndex(int)} and returns itself.
     * @param index Index to set.
     * @return This.
     */
    @SuppressWarnings("unchecked")
    default SELF withIndex(int index) {
        this.setIndex(index);
        return (SELF) this;
    }

}
