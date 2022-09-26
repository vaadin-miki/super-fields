package org.vaadin.miki.shared.labels;

import java.util.Locale;

/**
 * Available label positions.
 *
 * @author miki
 * @since 2022-09-23
 */
public enum LabelPosition {

    DEFAULT(false),
    BEFORE_START,
    BEFORE_MIDDLE,
    BEFORE_END,
    AFTER_START,
    AFTER_MIDDLE,
    AFTER_END,
    LAST(false);

    private final String positionData;

    LabelPosition() {
        this(true);
    }

    LabelPosition(boolean side) {
        this.positionData = String.join(" ", ((side ? "side_" : "") + this.name()).toLowerCase(Locale.ROOT).split("_"));
    }

    /**
     * The attribute value that corresponds to the given label position. Used by CSS selectors.
     * @return A non-{@code null} array.
     */
    public String getPositionData() {
        return positionData;
    }
}
