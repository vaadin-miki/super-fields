package org.vaadin.miki.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Tools related to regular expressions.
 *
 * @author miki
 * @since 2023-05-05
 */
public class RegexTools {

  /**
   * Characters that need to be escaped in a regular expression.
   */
  // courtesy of Tobi G. https://stackoverflow.com/questions/14134558/list-of-all-special-characters-that-need-to-be-escaped-in-a-regex
  public static final Set<Character> CHARACTERS_TO_ESCAPE = Set.of(
      '\\', '.', '[', ']', '{', '}', '(', ')', '<', '>', '*', '+', '-', '=', '!', '?', '^', '$', '|'
  );

  /**
   * Escapes the character if it needs to in a regular expression.
   * @param character Character to escape.
   * @return Escaped character (if it belongs to {@link #CHARACTERS_TO_ESCAPE}), otherwise the passed character.
   */
  public static String escaped(char character) {
    return CHARACTERS_TO_ESCAPE.contains(character) ? "\\"+character : String.valueOf(character);
  }

  /**
   * Appends to a given builder a regular expression that is a selector for a given character(s).
   * @param builder Builder to add the regular expression to.
   * @param mainCharacter The character to select.
   * @param alternatives Eventual alternatives.
   * @return The passed builder.
   */
  public static StringBuilder characterSelector(StringBuilder builder, char mainCharacter, Collection<Character> alternatives) {
    if(alternatives == null)
      return builder.append(escaped(mainCharacter));
    else {
      // do not modify the original set
      alternatives = new LinkedHashSet<>(alternatives);
      alternatives.remove(mainCharacter);
      if(alternatives.isEmpty())
        return builder.append(escaped(mainCharacter));
      else {
        builder.append("[").append(escaped(mainCharacter));
        alternatives.forEach(character -> builder.append(escaped(character)));
        return builder.append("]");
      }
    }

  }

  /**
   * Builds a regular expression that is a selector for a given character(s).
   * @param mainCharacter The character to select.
   * @param alternatives Eventual alternatives.
   * @return A regular expression that matches the character or its alternatives.
   */
  public static String characterSelector(char mainCharacter, Collection<Character> alternatives) {
    return characterSelector(new StringBuilder(), mainCharacter, alternatives).toString();
  }

  /**
   * Builds a regular expression that is a selector for a given character(s).
   * @param main The character to select.
   * @param alternatives Eventual alternatives.
   * @return A regular expression that matches the character or its alternatives.
   */
  public static String characterSelector(char main, char... alternatives) {
    if(alternatives == null || alternatives.length == 0)
      return characterSelector(main, Collections.emptySet());
    else return characterSelector(main, Arrays.asList(IntStream.range(0, alternatives.length)
        .mapToObj(index -> alternatives[index])
        .toArray(Character[]::new)));
  }

  private RegexTools() {
    // no instances allowed
  }

}
