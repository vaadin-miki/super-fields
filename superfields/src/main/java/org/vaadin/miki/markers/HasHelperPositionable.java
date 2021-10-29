package org.vaadin.miki.markers;

import com.vaadin.flow.component.HasElement;

/**
 * Allows controlling position of the helper text/component.
 * @author miki
 * @since 2021-10-29
 */
public interface HasHelperPositionable extends HasElement {

    /**
     * Vaadin expects this in the theme list.
     */
    String HELPER_ABOVE_THEME_VARIANT = "helper-above-field";

    /**
     * Positions the helper above the component.
     */
    default void setHelperAbove() {
        this.getElement().getThemeList().add(HELPER_ABOVE_THEME_VARIANT);
    }

    /**
     * Positions the helper below the component.
     */
    default void setHelperBelow() {
        this.getElement().getThemeList().remove(HELPER_ABOVE_THEME_VARIANT);
    }

    /**
     * Positions the helper above or below the component.
     * @param above When {@code true}, helper should be positioned above.
     * @see #setHelperAbove()
     * @see #setHelperBelow()
     */
    default void setHelperAbove(boolean above) {
        if(above)
            this.setHelperAbove();
        else this.setHelperBelow();
    }

    /**
     * Checks if the helper is positioned above.
     * @return When {@code true}, helper should be positioned above.
     */
    default boolean isHelperAbove() { // no, there is not :)
        return this.getElement().getThemeList().contains(HELPER_ABOVE_THEME_VARIANT);
    }

}
