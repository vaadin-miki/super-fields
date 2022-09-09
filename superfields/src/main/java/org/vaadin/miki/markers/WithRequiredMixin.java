package org.vaadin.miki.markers;

/**
 * Mixin for {@link HasRequired}.
 * @param <SELF> Self type.
 * @author miki
 * @since 2022-09-09
 */
public interface WithRequiredMixin<SELF extends HasRequired> extends HasRequired {

    /**
     * Chains {@link #setRequired(boolean)} and returns itself.
     * @param state Required state.
     * @return This.
     */
    @SuppressWarnings("unchecked")
    default SELF withRequired(boolean state) {
        this.setRequired(state);
        return (SELF) this;
    }

    /**
     * Calls and returns {@link #withRequired(boolean)} with {@code true}.
     * @return This.
     */
    default SELF withRequired() {
        return this.withRequired(true);
    }

    /**
     * Calls and returns {@link #withRequired(boolean)} with {@code false}.
     * @return This.
     */
    default SELF withoutRequired() {
        return this.withRequired(false);
    }

}
