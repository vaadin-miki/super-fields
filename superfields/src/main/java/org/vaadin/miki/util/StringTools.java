package org.vaadin.miki.util;

import java.util.Locale;
import java.util.Set;

/**
 * Some general-purpose utils for {@link String}s.
 *
 * @author miki
 * @since 2022-09-08
 */
public class StringTools {

    // regexp by polygenelubricants (what a username!) https://stackoverflow.com/a/2560017/384484
    // modified by Miki to not stop at _
    // putting it this way also removes the sonar warning about the regexp being too complex
    // it is a regexp, how can it be not complex?!
    private static final String CAMEL_CASE_REGEXP = String.format("%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z_])", "(?<=[^A-Z])(?=[A-Z])", "(?<=[A-Za-z])(?=[^A-Za-z_])");

    /**
     * Modifies the text by putting the first character of it to uppercase.
     * @param string String.
     * @return The input string, but with the first letter being uppercase.
     */
    public static String firstLetterUppercase(String string) {
        return string == null || string.isEmpty() ? string : string.substring(0, 1).toUpperCase(Locale.ROOT) + string.substring(1);
    }

    /**
     * Converts {@code camelCase} into {@code Readable Text}. Also removes more than two consecutive spaces and trims the result.
     * @param string String.
     * @return Human-readable string.
     */
    public static String humanReadable(String string) {
        if(string == null || string.isEmpty()) return string;
        else return firstLetterUppercase(string.replaceAll(CAMEL_CASE_REGEXP, " ")
                .replaceAll("\\s{2,}", " ") // also get rid of double spaces
                .trim()); // and trim
    }

    private StringTools() {
        // no instances allowed
    }
}
