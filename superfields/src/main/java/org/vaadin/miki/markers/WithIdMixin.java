package org.vaadin.miki.markers;

/**
 * Mixin interface to allow chaining setting id.
 * @param <SELF> Self type.
 * @author miki
 * @since 2020-04-22
 */
@SuppressWarnings("squid:S119") // SELF is a fine generic name that is more descriptive than S
public interface WithIdMixin<SELF extends HasId> extends HasId {

    /**
     * Chains setting id.
     * @param id Id to set.
     * @return This.
     * @see #setId(String)
     */
    @SuppressWarnings("unchecked")
    default SELF withId(String id) {
        this.setId(id);
        return (SELF)this;
    }

}
