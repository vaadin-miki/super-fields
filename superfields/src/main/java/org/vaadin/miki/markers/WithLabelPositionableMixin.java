package org.vaadin.miki.markers;

import org.vaadin.miki.shared.labels.LabelPosition;

/**
 * A mixin for {@link HasLabelPositionable}.
 *
 * @author miki
 * @since 2022-09-23
 */
@SuppressWarnings("squid:S119") // SELF is a pretty good generic name
public interface WithLabelPositionableMixin<SELF extends HasLabelPositionable> extends HasLabelPositionable {

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
