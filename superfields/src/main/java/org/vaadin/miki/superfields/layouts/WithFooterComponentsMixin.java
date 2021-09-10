package org.vaadin.miki.superfields.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;

/**
 * Mixin for adding components to a footer.
 * @param <F> Footer type.
 * @param <SELF> Self type.
 */
public interface WithFooterComponentsMixin<F extends Component & HasComponents, SELF extends HasFooter<F>> extends HasFooter<F> {

    /**
     * Adds given components to the footer, if it is present.
     * @param components Components to add.
     * @return This.
     */
    @SuppressWarnings("unchecked")
    default SELF withFooterComponents(Component... components) {
        this.getFooter().ifPresent(footer -> footer.add(components));
        return (SELF) this;
    }

}
