package org.vaadin.miki.superfields.dates;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * An interface for objects capable of handling date patterns.
 * @author miki
 * @since 2020-04-23
 */
public interface DatePattern extends Serializable {

    /**
     * Parses given text as a date.
     * @param text Text to parse. Should not be {@code null}.
     * @return A {@link LocalDate} corresponding to the text, or {@code null} for errors.
     */
    LocalDate parseDate(String text);

    /**
     * Formats given date.
     * @param date Date to format.
     * @return A string that corresponds to the given date. Should not be {@code null}.
     */
    String formatDate(LocalDate date);

}
