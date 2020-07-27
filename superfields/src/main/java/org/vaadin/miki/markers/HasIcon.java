package org.vaadin.miki.markers;

import com.vaadin.flow.component.icon.Icon;

/**
 * Marker interface for objects that have an {@link Icon}.
 * @author miki
 * @since 2020-07-07
 */
public interface HasIcon {

    /**
     * Sets an icon associated with this object.
     * @param icon Icon to set. Can be {@code null} to remove current icon.
     */
    void setIcon(Icon icon);

    /**
     * Returns current icon.
     * @return Current {@link Icon}. Can be {@code null} if no icon has been associated.
     */
    Icon getIcon();

}
