package org.vaadin.miki.superfields.text;

import com.vaadin.flow.component.HasElement;

/**
 * Marker interface for components that can select text.
 * Handles selection using client-side JavaScript.
 * @author miki
 * @since 2020-05-29
 */
public interface CanSelectText extends HasElement {

    void selectAll();

    void selectNone();

    void select(int from, int to);

}
