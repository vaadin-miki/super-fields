package org.vaadin.miki.markers;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasHelper;

/**
 * Marker interface to allow chaining {@link HasHelper}.
 * @param <SELF> Self type.
 * @author miki
 * @since 2020-10-12
 */
public interface WithHelper<SELF extends HasHelper> extends HasHelper {

    /**
     * Chains {@link #setHelperText(String)} and returns itself.
     * @param helperText Helper text to set.
     * @return This.
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
     */
    @SuppressWarnings("unchecked")
    default SELF withHelperComponent(Component component) {
        this.setHelperComponent(component);
        return (SELF)this;
    }


}
