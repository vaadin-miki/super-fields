package org.vaadin.miki.superfields.dates;

/**
 * Marker interface for objects that have a {@link DatePattern}.
 * @author miki
 * @since 2020-04-24
 */
public interface HasDatePattern {

    /**
     * Sets new date pattern to use when displaying and parsing dates.
     * @param pattern A pattern to use.
     */
    void setDatePattern(DatePattern pattern);

    /**
     * Returns current date pattern.
     * @return A {@link DatePattern}.
     */
    DatePattern getDatePattern();

}
