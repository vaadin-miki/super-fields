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

    public static FlexLayout row() {
        return create(FlexLayout.FlexDirection.ROW);
    }

    public static FlexLayout column() {
        return create(FlexLayout.FlexDirection.COLUMN);
    }

    public static FlexLayout rowReverse() {
        return create(FlexLayout.FlexDirection.ROW_REVERSE);
    }

    public static FlexLayout columnReverse() {
        return create(FlexLayout.FlexDirection.COLUMN_REVERSE);
    }

    private FlexLayoutHelpers() {
        // instances not allowed
    }

}
