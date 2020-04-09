package org.vaadin.miki.markers;

import java.util.Locale;

/**
 * Marker interface for objects that can have their {@link Locale} changed.
 * @author miki
 * @since 2020-04-09
 */
public interface HasLocale {

    /**
     * Sets new locale for this object.
     * @param locale Locale to use. Should not be {@code null}.
     */
    void setLocale(Locale locale);

    /**
     * Returns locale associated with this object.
     * @return A {@link Locale}. Never {@code null}.
     */
    Locale getLocale();

}
