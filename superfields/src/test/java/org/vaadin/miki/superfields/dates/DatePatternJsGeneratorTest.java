package org.vaadin.miki.superfields.dates;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DatePatternJsGeneratorTest {

    private DatePatternJsGenerator formatter;

    @Before
    public void setUp() {
        this.formatter = DatePatternJsGenerator.INSTANCE;
    }

    @Test
    public void testFormatBasicYearMonthDayOutOfTheBox() {
        DatePattern pattern = new DatePattern();
        String expected = "return [String(date.year), String(date.month + 1).padStart(2, '0'), String(date.day).padStart(2, '0')].join('-');";
        String result = this.formatter.formatDate(pattern);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testFormatMonthDayShortYear() {
        DatePattern pattern = new DatePattern().withDisplayOrder(DatePattern.Order.MONTH_DAY_YEAR).
                withZeroPrefixedMonth(false).withZeroPrefixedDay(false).
                withShortYear(true).withSeparator('/');
        String expected = "return [String(date.month + 1), String(date.day), (date.year < 10 ? '0'+String(date.year) : String(date.year).substr(-2))].join('/');";
        String result = this.formatter.formatDate(pattern);
        Assert.assertEquals(expected, result);
    }

}