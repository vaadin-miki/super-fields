package org.vaadin.miki.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link RegexTools}.
 *
 * @author miki
 * @since 2023-05-05
 */
public class RegexToolsTest {

  @Test
  public void testRegexEscapedInCharSelector() {
    final char[] input = new char[]{0, 'a', 'Ą', 160, ':', '?', '!', '-', '6'};
    final String[] expected = new String[]{"\0", "a", "Ą", ""+(char)160, ":", "\\?", "\\!", "\\-", "6"};
    for(int zmp1=0; zmp1<input.length; zmp1++)
      Assertions.assertEquals(expected[zmp1], RegexTools.escaped(input[zmp1], true));
  }

  @Test
  public void testRegexEscapedOutsideCharSelector() {
    final char[] input = new char[]{0, 'a', 'Ą', 160, ':', '?', '!', '-', '6'};
    final String[] expected = new String[]{"\0", "a", "Ą", ""+(char)160, ":", "\\?", "\\!", "-", "6"};
    for(int zmp1=0; zmp1<input.length; zmp1++)
      Assertions.assertEquals(expected[zmp1], RegexTools.escaped(input[zmp1], false));
  }

  @Test
  public void testCharacterSelector() {
    final char[] mains = new char[]{0, '-', '-', '.', ',', '?', 'X', '.'};
    final char[][] alts = new char[][]{ new char[]{'f'}, new char[]{'!', '_'}, new char[]{}, new char[]{'.'}, null, new char[]{'a', 'b', '!'}, new char[]{'ą'}, new char[]{'-', 'Ź'}};
    // note the '-' - it should be escaped only inside [...] (see #481)
    final String[] expected = new String[]{"[\0f]", "[\\-\\!_]", "-", "\\.", ",", "[\\?ab\\!]", "[Xą]", "[\\.\\-Ź]"};
    for(int zmp1=0; zmp1<mains.length; zmp1++)
      Assertions.assertEquals(expected[zmp1], RegexTools.characterSelector(mains[zmp1], alts[zmp1]), "error at position "+zmp1);
  }

}
