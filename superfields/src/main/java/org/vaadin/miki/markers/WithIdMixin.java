package org.vaadin.miki.markers;

/**
 * Mixin interface to allow chaining setting id.
 * @param <SELF> Self type.
 * @author miki
 * @since 2020-04-22
 */
public interface WithIdMixin<SELF extends HasId> extends HasId {

    /**
     * Chains setting id.
     * @param id Id to set.
     * @return This.
     */
    @SuppressWarnings("unchecked")
    default SELF withId(String id) {
        this.setId(id);
        return (SELF)this;
    }

}
