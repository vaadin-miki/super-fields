package org.vaadin.miki.markers;

import org.vaadin.miki.shared.labels.LabelPosition;

/**
 * Marker interface for components that have a positionable label.
 *
 * @author miki
 * @since 2022-09-23
 */
public interface HasPositionableLabel {

    /**
     * Sets the label position to a new one.
     * @param position A position to use. Setting {@code null} will reset it to the default setting.
     */
    void setLabelPosition(LabelPosition position);

    /**
     * Returns current label position, if it has been set.
     * @return A {@link LabelPosition} if one has been set, or {@code null} if the component still has the default setting.
     */
    LabelPosition getLabelPosition();

}
