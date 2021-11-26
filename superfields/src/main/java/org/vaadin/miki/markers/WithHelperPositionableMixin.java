package org.vaadin.miki.markers;

/**
 * Mixin interface for nice chaining of methods in {@link HasHelperPositionable}.
 * @author miki
 * @since 2021-10-29
 */
public interface WithHelperPositionableMixin<SELF extends HasHelperPositionable> extends HasHelperPositionable {

    /**
     * Chains {@link #setHelperAbove()} and returns itself.
     * @return This.
     * @see #setHelperBelow()
     */
    @SuppressWarnings("unchecked")
    default SELF withHelperAbove() {
        this.setHelperAbove();
        return (SELF)this;
    }

    /**
     * Chains {@link #setHelperBelow()} and returns itself.
     * @return This.
     * @see #setHelperBelow()
     */
    @SuppressWarnings("unchecked")
    default SELF withHelperBelow() {
        this.setHelperBelow();
        return (SELF) this;
    }

    /**
     * Chains {@link #setHelperAbove(boolean)} and returns itself.
     * @param above When {@code true}, helper should be positioned above.
     * @return This.
     * @see #setHelperAbove(boolean)
     */
    @SuppressWarnings("unchecked")
    default SELF withHelperAbove(boolean above) {
        this.setHelperAbove(above);
        return (SELF) this;
    }

}
