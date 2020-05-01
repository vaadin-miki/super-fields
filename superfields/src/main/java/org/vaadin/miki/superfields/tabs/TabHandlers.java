package org.vaadin.miki.superfields.tabs;

import com.vaadin.flow.component.HasComponents;

/**
 * Utility class with some pre-made {@link TabHandlers}s.
 * @author miki
 * @since 2020-04-30
 */
public class TabHandlers {

    /**
     * A {@link TabHandler} that toggles visibility. All tab contents is loaded to the dom structure.
     */
    public static final TabHandler VISIBILITY_HANDLER = new LambdaTabHandler(
            "Visibility-based",
            (tab, component, div) -> {
                component.setVisible(false);
                div.add(component);
            },
            (tab, component, div) -> {
                div.remove(component);
                component.setVisible(true);
            },
            (tab, component, div) ->
                component.setVisible(true),
            (tab, component, div) ->
                component.setVisible(false)
    );

    /**
     * A {{@link TabHandler} that adds and removes tab content.
     */
    public static final TabHandler REMOVING_HANDLER = new LambdaTabHandler(
            "Removing handler",
            (tab, component, div) -> {},
            (tab, component, div) ->
                component.getParent().filter(parent -> parent.equals(div)).map(HasComponents.class::cast).ifPresent(parent -> parent.remove(component)),
            (tab, component, div) -> div.add(component),
            (tab, component, div) ->
                component.getParent().filter(parent -> parent.equals(div)).map(HasComponents.class::cast).ifPresent(parent -> parent.remove(component))
    );

    /**
     * Returns an instance of {@link TabHandler} in which all contents are always visible, but a selected tab contents has a given class name.
     * @param selectedClassName Class name to use.
     * @return A {@link TabHandler}.
     */
    public static TabHandler selectedContentHasClassName(final String selectedClassName) {
        return new LambdaTabHandler("Adding class name <"+selectedClassName+">",
                (tab, component, div) -> div.add(component),
                (tab, component, div) -> div.remove(component),
                (tab, component, div) -> component.getElement().getClassList().add(selectedClassName),
                (tab, component, div) -> component.getElement().getClassList().remove(selectedClassName)
        );
    }

    private TabHandlers() {
        // instances not allowed
    }
}
