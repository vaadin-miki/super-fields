package org.vaadin.miki.markers;

import com.vaadin.flow.component.HasElement;
import org.vaadin.miki.shared.labels.LabelPosition;

/**
 * Marker interface for components that have a positionable label.
 *
 * @author miki
 * @since 2022-09-23
 */
public interface HasLabelPositionable extends HasElement {

    /**
     * Attribute name that contains the selected label position value.
     */
    String LABEL_POSITION_ATTRIBUTE = "data-label-position";
    /**
     * Attribute name that contains details of the label for easy styling.
     */
    String LABEL_POSITION_DETAILS_ATTRIBUTE = "data-label-position-details";

    /**
     * Sets the label position to a new one.
     * @param position A position to use. Setting {@code null} will reset it to the default setting.
     */
    default void setLabelPosition(LabelPosition position) {
        if(position == null || position == LabelPosition.DEFAULT) {
            this.getElement().removeAttribute(LABEL_POSITION_ATTRIBUTE);
            this.getElement().removeAttribute(LABEL_POSITION_DETAILS_ATTRIBUTE);
        }
        else {
            this.getElement().setAttribute(LABEL_POSITION_ATTRIBUTE, position.name());
            this.getElement().setAttribute(LABEL_POSITION_DETAILS_ATTRIBUTE, String.join(" ", position.name().toLowerCase().split("_")));
        }
    }

    /**
     * Returns current label position, if it has been set.
     * @return A {@link LabelPosition}.
     */
    default LabelPosition getLabelPosition() {
        if(this.getElement().hasAttribute(LABEL_POSITION_ATTRIBUTE))
            return LabelPosition.valueOf(this.getElement().getAttribute(LABEL_POSITION_ATTRIBUTE));
        else return LabelPosition.DEFAULT;
    }

}
