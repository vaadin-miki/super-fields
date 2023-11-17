package org.vaadin.miki.markers;

/**
 * Mixin interface for {@link com.vaadin.flow.component.shared.HasClearButton}.
 * @param <SELF> Self type.
 * @author miki
 * @since 2021-01-10
 */
@SuppressWarnings("squid:S119") // SELF is a fine generic name that is more descriptive than S
public interface WithClearButtonMixin<SELF extends com.vaadin.flow.component.shared.HasClearButton> extends com.vaadin.flow.component.shared.HasClearButton {

    /**
     * Chains {@link #setClearButtonVisible(boolean)} and returns itself.
     * @param state New visibility state for the clear button.
     * @return This.
     */
    @SuppressWarnings("unchecked")
    default SELF withClearButtonVisible(boolean state) {
        this.setClearButtonVisible(state);
        return (SELF)this;
    }

    /**
     * Chains {@link #setClearButtonVisible(boolean)} called with {@code true} and returns itself.
     * @return This.
     */
    default SELF withClearButtonVisible() {
        return this.withClearButtonVisible(true);
    }

    /**
     * Chains {@link #setClearButtonVisible(boolean)} called with {@code false} and returns itself.
     * @return This.
     */
    default SELF withoutClearButtonVisible() {
        return this.withClearButtonVisible(false);
    }

}
