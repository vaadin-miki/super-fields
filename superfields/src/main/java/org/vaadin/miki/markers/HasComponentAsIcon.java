package org.vaadin.miki.markers;

import com.vaadin.flow.component.Component;

/**
 * Marker interface for objects that accept a general {@link Component} as an icon.
 * @author miki
 * @since 2020-07-07
 */
public interface HasComponentAsIcon {

    /**
     * Sets given {@link Component} as an icon of this object.
     * @param icon A {@link Component} to be used as an icon. Can be {@code null} to clear current icon.
     */
    void setIcon(Component icon);

    /**
     * Returns current icon.
     * @return A {@link Component} that serves as an icon for this object. Can be {@code null}.
     */
    Component getIcon();

}
