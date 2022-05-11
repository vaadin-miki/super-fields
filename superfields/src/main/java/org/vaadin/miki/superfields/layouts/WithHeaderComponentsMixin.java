package org.vaadin.miki.superfields.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;

/**
 * Mixin for adding components to a header.
 *
 * @param <H> Header type.
 * @param <SELF> Self type.
 *
 * @author miki
 * @since 2021-09-15
 */
@SuppressWarnings("squid:S119") // SELF is a fine generic name that is more descriptive than S
public interface WithHeaderComponentsMixin<H extends Component & HasComponents, SELF extends HasHeader<H>> extends HasHeader<H> {

    /**
     * Adds given components to the header, if it is present.
     * @param components Components to add.
     * @return This.
     */
    @SuppressWarnings("unchecked")
    default SELF withHeaderComponents(Component... components) {
        this.getHeader().ifPresent(header -> header.add(components));
        return (SELF) this;
    }

}
