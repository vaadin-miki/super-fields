package org.vaadin.miki.superfields.dates.patterns;


import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

public class CustomDatePatternTest {

    @Test
    public void testBasicCallbacks() {
        CustomDatePattern datePattern = new CustomDatePattern(
                string -> LocalDate.of(Integer.parseInt(string.substring(0, 4)), Integer.parseInt(string.substring(4, 6)), Integer.parseInt(string.substring(6, 8))),
                date -> String.format("%04d%02d%02d", date.getYear(), date.getMonthValue(), date.getDayOfMonth())
        );
        Assert.assertEquals(LocalDate.of(2010, 2, 8), datePattern.parseDate("20100208"));
        Assert.assertEquals("19991020", datePattern.formatDate(LocalDate.of(1999, 10, 20)));
    }

}