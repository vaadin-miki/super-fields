package org.vaadin.miki.superfields.collections;

/**
 * Mixin for chaining {@link #setValueComponentProvider(ValueComponentProvider)}.
 * @param <T> Type of data in the collection.
 * @param <SELF> Self type.
 * @author miki
 * @since 2021-08-25
 */
public interface WithValueComponentProvider<T, SELF extends HasValueComponentProvider<T>> extends HasValueComponentProvider<T> {

    /**
     * Chains {@link #setValueComponentProvider(ValueComponentProvider)} and returns itself.
     * @param provider Provider to use.
     * @return This.
     */
    @SuppressWarnings("unchecked")
    default SELF withCollectionComponentProvider(ValueComponentProvider<T, ?> provider) {
        this.setValueComponentProvider(provider);
        return (SELF) this;
    }
}
