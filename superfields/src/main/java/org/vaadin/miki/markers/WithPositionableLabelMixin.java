package org.vaadin.miki.markers;

import org.vaadin.miki.shared.labels.LabelPosition;

/**
 * A mixin for {@link HasPositionableLabel}.
 *
 * @author miki
 * @since 2022-09-23
 */
public interface WithPositionableLabelMixin<SELF extends HasPositionableLabel> extends HasPositionableLabel {

    /**
     * Chains {@link #setLabelPosition(LabelPosition)} and returns itself.
     * @param position Position.
     * @return This.
     */
    @SuppressWarnings("unchecked")
    default SELF withLabelPosition(LabelPosition position) {
        this.setLabelPosition(position);
        return (SELF) this;
    }
}
