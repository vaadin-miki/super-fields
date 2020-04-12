package org.vaadin.miki.markers;

import java.util.Locale;

/**
 * Mixin interface to support chaining {@link #setLocale(Locale)}.
 * @param <SELF> Self type.
 * @author miki
 * @since 2020-04-12
 */
public interface WithLocaleMixin<SELF extends WithLocaleMixin<SELF>> extends HasLocale {

    /**
     * Chains {@link #setLocale(Locale)} and returns itself.
     * @param locale Locale to set.
     * @return This.
     * @see #setLocale(Locale)
     */
    @SuppressWarnings("unchecked")
    default SELF withLocale(Locale locale) {
        setLocale(locale);
        return (SELF)this;
    }

}
