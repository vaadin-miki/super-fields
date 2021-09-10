package org.vaadin.miki.markers;

/**
 * Marker interface for objects that have a String property named {@code text}.
 * Does not extend nor depend on Vaadin's {@code HasText}, because that interface extends {@code HasElement}.
 * @param <SELF> Self type.
 *
 * @author miki
 * @since 2020-07-07
 */
public interface WithTextMixin<SELF extends HasText> extends HasText {

    /**
     * Chains {@link #setText(String)} and returns itself.
     * @param text Text to set.
     * @return This.
     */
    @SuppressWarnings("unchecked")
    default SELF withText(String text) {
        this.setText(text);
        return (SELF)this;
    }

}
