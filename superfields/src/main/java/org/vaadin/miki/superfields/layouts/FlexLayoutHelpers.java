package org.vaadin.miki.superfields.layouts;

import com.vaadin.flow.component.orderedlayout.FlexLayout;

/**
 * Utility class to make creating {@link FlexLayout}s easier.
 *
 * @author miki
 * @since 2021-09-03
 */
public class FlexLayoutHelpers {

    private static FlexLayout create(FlexLayout.FlexDirection direction) {
        final FlexLayout result = new FlexLayout();
        result.setFlexDirection(direction);
        return result;
    }

    /**
     * Returns a {@link FlexLayout} with {@link FlexLayout.FlexDirection#ROW} direction.
     * @return A {@link FlexLayout}.
     */
    public static FlexLayout row() {
        return create(FlexLayout.FlexDirection.ROW);
    }

    /**
     * Returns a {@link FlexLayout} with {@link FlexLayout.FlexDirection#COLUMN} direction.
     * @return A {@link FlexLayout}.
     */
    public static FlexLayout column() {
        return create(FlexLayout.FlexDirection.COLUMN);
    }

    /**
     * Returns a {@link FlexLayout} with {@link FlexLayout.FlexDirection#ROW_REVERSE} direction.
     * @return A {@link FlexLayout}.
     */
    public static FlexLayout rowReverse() {
        return create(FlexLayout.FlexDirection.ROW_REVERSE);
    }

    /**
     * Returns a {@link FlexLayout} with {@link FlexLayout.FlexDirection#COLUMN_REVERSE} direction.
     * @return A {@link FlexLayout}.
     */
    public static FlexLayout columnReverse() {
        return create(FlexLayout.FlexDirection.COLUMN_REVERSE);
    }

    /**
     * Returns a {@link FlexLayout}-based {@link HeaderFooterLayoutWrapper}, where main alignment and body are columns, and header and footer are rows.
     * @return A {@link HeaderFooterLayoutWrapper}.
     */
    public static HeaderFooterLayoutWrapper<FlexLayout, FlexLayout, FlexLayout, FlexLayout> columnWithHeaderRowAndFooterRow() {
        return new HeaderFooterLayoutWrapper<>(
                FlexLayoutHelpers::column, row(), column(), row()
        );
    }

    private FlexLayoutHelpers() {
        // instances not allowed
    }

}
