package org.vaadin.miki.shared.labels;

import java.util.Locale;

/**
 * Available label positions.
 *
 * @author miki
 * @since 2022-09-23
 */
public enum LabelPosition {

    /**
     * Default label position, without any changes.
     */
    DEFAULT(false),

    /**
     * Label is placed to the side of the component, before it, and aligned to the start of the column.
     * In left-to-right, top-to-bottom layouts, this means: label on the left, aligned to the top.
     */
    BEFORE_START,
    /**
     * Label is placed to the side of the component, before it, and aligned to the middle of the column.
     * In left-to-right, top-to-bottom layouts, this means: label on the left, in the vertical middle of column.
     */
    BEFORE_MIDDLE,
    /**
     * Label is placed to the side of the component, before it, and aligned to the end of the column.
     * In left-to-right, top-to-bottom layouts, this means: label on the left, aligned to the bottom.
     */
    BEFORE_END,
    /**
     * Label is placed to the side of the component, after it, and aligned to the start of the column.
     * In left-to-right, top-to-bottom layouts, this means: label on the right, aligned to the top.
     */
    AFTER_START,
    /**
     * Label is placed to the side of the component, after it, and aligned to the middle of the column.
     * In left-to-right, top-to-bottom layouts, this means: label on the right, in the vertical middle of column.
     */
    AFTER_MIDDLE,
    /**
     * Label is placed to the side of the component, after it, and aligned to the end of the column.
     * In left-to-right, top-to-bottom layouts, this means: label on the right, aligned to the bottom.
     */
    AFTER_END,
    /**
     * Label is placed as the last thing of the entire component.
     * In left-to-right, top-to-bottom layouts, this means: label on the bottom.
     */
    LAST(false);

    private final String positionData;

    LabelPosition() {
        this(true);
    }

    /**
     * Creates the enum.
     * @param side Whether the label is on the side of the component.
     */
    LabelPosition(boolean side) {
        this.positionData = String.join(" ", ((side ? "side_" : "") + this.name()).toLowerCase(Locale.ROOT).split("_"));
    }

    /**
     * The attribute value that corresponds to the given label position. Used by CSS selectors.
     * This is a space-separated list of styles. Never {@code null}.
     *
     * @return A non-{@code null} array.
     */
    public String getPositionData() {
        return positionData;
    }
}
