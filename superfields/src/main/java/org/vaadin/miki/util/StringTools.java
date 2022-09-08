package org.vaadin.miki.util;

import java.util.Locale;

/**
 * Some general-purpose utils for {@link String}s.
 *
 * @author miki
 * @since 2022-09-08
 */
public class StringTools {

    /**
     * Modifies the text by putting the first character of it to uppercase.
     * @param string String.
     * @return The input string, but with the first letter being uppercase.
     */
    public static String firstLetterUppercase(String string) {
        return string == null || string.isEmpty() ? string : string.substring(0, 1).toUpperCase(Locale.ROOT) + string.substring(1);
    }

    /**
     * Converts {@code camelCase} into {@code Readable Text}.
     * @param string String.
     * @return Human-readable string.
     */
    // regexp by polygenelubricants (what a username!) https://stackoverflow.com/a/2560017/384484
    public static String humanReadable(String string) {
        if(string == null || string.isEmpty()) return string;
        else return firstLetterUppercase(string.replaceAll("(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])", " "));
    }

    private StringTools() {
        // no instances allowed
    }
}
