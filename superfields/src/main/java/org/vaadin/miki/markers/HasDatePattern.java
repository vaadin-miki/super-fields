package org.vaadin.miki.markers;

import org.vaadin.miki.superfields.dates.DatePattern;

/**
 * Marker interface for objects that have a {@link DatePattern}.
 * @author miki
 * @since 2020-04-24
 */
public interface HasDatePattern {

    /**
     * Sets new date pattern to use when displaying and parsing dates.
     * It may reset the value currently displayed in the component.
     * @param pattern A pattern to use. Can be {@code null}, which will reset the pattern to whatever is supported by default.
     */
    void setDatePattern(DatePattern pattern);

    /**
     * Returns current date pattern.
     * @return A {@link DatePattern}. May be {@code null}.
     */
    DatePattern getDatePattern();

}
