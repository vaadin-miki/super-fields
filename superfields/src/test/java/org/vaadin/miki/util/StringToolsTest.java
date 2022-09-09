package org.vaadin.miki.util;

import org.junit.Assert;
import org.junit.Test;

public class StringToolsTest {

    @Test
    public void testFirstLetterUppercase() {

        final String[] input = new String[]{null, "", "hello", "h", "Hello", "\nfoo"};
        final String[] expected = new String[]{null, "", "Hello", "H", "Hello", "\nfoo"};

        for(int zmp1=0; zmp1<input.length; zmp1++)
            Assert.assertEquals(expected[zmp1], StringTools.firstLetterUppercase(input[zmp1]));
    }

    @Test
    public void testHumanReadable() {
        final String[] input = new String[]{null, "", "this is sparta", "thisIsSparta", "ThisIsSparta", "This_is_not", " thisIsAlsoSparta  ", "and     this"};
        final String[] expected = new String[]{null, "", "This is sparta", "This Is Sparta", "This Is Sparta", "This_is_not", "This Is Also Sparta", "And this"};

        for(int zmp1=0; zmp1<input.length; zmp1++)
            Assert.assertEquals(expected[zmp1], StringTools.humanReadable(input[zmp1]));
    }

}