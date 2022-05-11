package org.vaadin.miki.markers;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasHelper;

/**
 * Marker interface to allow chaining {@link HasHelper}.
 * @param <SELF> Self type.
 * @author miki
 * @since 2020-10-12
 */
@SuppressWarnings("squid:S119") // SELF is a fine generic name that is more descriptive than S
public interface WithHelperMixin<SELF extends HasHelper> extends HasHelper {

    /**
     * Chains {@link #setHelperText(String)} and returns itself.
     * @param helperText Helper text to set.
     * @return This.
     * @see #setHelperText(String)
     */
    @SuppressWarnings("unchecked")
    default SELF withHelperText(String helperText) {
        this.setHelperText(helperText);
        return (SELF)this;
    }

    /**
     * Chains {@link #setHelperComponent(Component)} and returns itself.
     * @param component Helper component to use.
     * @return This.
     * @see #setHelperComponent(Component)
     */
    @SuppressWarnings("unchecked")
    default SELF withHelperComponent(Component component) {
        this.setHelperComponent(component);
        return (SELF)this;
    }


}
