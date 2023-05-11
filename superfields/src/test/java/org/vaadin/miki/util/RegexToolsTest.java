package org.vaadin.miki.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link RegexTools}.
 *
 * @author miki
 * @since 2023-05-05
 */
public class RegexToolsTest {

  @Test
  public void testRegexEscaped() {
    final char[] input = new char[]{0, 'a', 'Ą', 160, ':', '?', '!', '-', '6'};
    final String[] expected = new String[]{"\0", "a", "Ą", ""+(char)160, ":", "\\?", "\\!", "\\-", "6"};
    for(int zmp1=0; zmp1<input.length; zmp1++)
      Assert.assertEquals(expected[zmp1], RegexTools.escaped(input[zmp1]));
  }

  @Test
  public void testCharacterSelector() {
    final char[] mains = new char[]{0, '-', '.', ',', '?', 'X', '.'};
    final char[][] alts = new char[][]{ new char[]{'f'}, new char[]{}, new char[]{'.'}, null, new char[]{'a', 'b', '!'}, new char[]{'ą'}, new char[]{'-', 'Ź'}};
    final String[] expected = new String[]{"[\0f]", "\\-", "\\.", ",", "[\\?ab\\!]", "[Xą]", "[\\.\\-Ź]"};
    for(int zmp1=0; zmp1<mains.length; zmp1++)
      Assert.assertEquals("error at position "+zmp1, expected[zmp1], RegexTools.characterSelector(mains[zmp1], alts[zmp1]));
  }

}
