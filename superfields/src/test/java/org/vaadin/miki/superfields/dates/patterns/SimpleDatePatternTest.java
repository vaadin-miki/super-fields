package org.vaadin.miki.superfields.dates.patterns;

import org.junit.Assert;
import org.junit.Test;
import org.vaadin.miki.superfields.dates.DatePattern;

import java.time.LocalDate;

public class SimpleDatePatternTest {

    private static final LocalDate[] DATES = new LocalDate[] {
            LocalDate.of(2020, 1, 1),
            LocalDate.of(1915, 5, 28),
            LocalDate.of(2138, 10, 2),
            LocalDate.of(1800, 11, 13),
            LocalDate.of(1930, 10, 1)
    };

    private void assertDates(DatePattern pattern, LocalDate[] dates, String[] outputs) {
        Assert.assertEquals(String.format("there should be %d expected outputs", outputs.length), DATES.length, outputs.length);
        Assert.assertEquals(String.format("there should be %d expected dates", outputs.length), DATES.length, dates.length);
        for(int zmp1=0; zmp1<DATES.length; zmp1++) {
            Assert.assertEquals(String.format("formatting date %s did not return %s", DATES[zmp1].toString(), outputs[zmp1]), outputs[zmp1], pattern.formatDate(DATES[zmp1]));
            Assert.assertEquals(String.format("parsing string %s did not return %s", outputs[zmp1], dates[zmp1].toString()), dates[zmp1], pattern.parseDate(outputs[zmp1]));
        }
    }

    private void assertDates(DatePattern pattern, String... outputs) {
        this.assertDates(pattern, DATES, outputs);
    }

    @Test
    public void testYearZeroMonthZeroDayPattern() {
        SimpleDatePattern pattern = new SimpleDatePattern().withDisplayOrder(DisplayOrder.YEAR_MONTH_DAY)
                .withSeparator('-').withZeroPrefixedDay(true).withZeroPrefixedMonth(true);
        this.assertDates(pattern, "2020-01-01", "1915-05-28", "2138-10-02", "1800-11-13", "1930-10-01");
    }

    @Test
    public void testMonthDayYearPattern() {
        SimpleDatePattern pattern = new SimpleDatePattern().withDisplayOrder(DisplayOrder.MONTH_DAY_YEAR)
                .withZeroPrefixedMonth(false).withSeparator('/');
        this.assertDates(pattern, "1/01/2020", "5/28/1915", "10/02/2138", "11/13/1800", "10/01/1930");
    }

    @Test
    public void testDayMonthShortYearPreviousBelowPattern() {
        SimpleDatePattern pattern = new SimpleDatePattern().withDisplayOrder(DisplayOrder.DAY_MONTH_YEAR)
                .withZeroPrefixedDay(false).withZeroPrefixedMonth(false)
                .withSeparator('.').withShortYear(true).withBaseCentury(21).withCenturyBoundaryYear(30).withPreviousCenturyBelowBoundary(true);
        // short year will make the dates switch to a century based on two digits only
        // years below boundary year will be in 20th century, boundary year and above will be in 21st
        this.assertDates(pattern,
                new LocalDate[]{
                        LocalDate.of(1920, 1, 1),
                        LocalDate.of(1915, 5, 28),
                        LocalDate.of(2038, 10, 2),
                        LocalDate.of(1900, 11, 13),
                        LocalDate.of(2030, 10, 1),
                },
                new String[]{"1.1.20", "28.5.15", "2.10.38", "13.11.00", "1.10.30"});
    }

    @Test
    public void testDayMonthShortYearPreviousNotBelowPattern() {
        SimpleDatePattern pattern = new SimpleDatePattern().withDisplayOrder(DisplayOrder.DAY_MONTH_YEAR)
                .withZeroPrefixedDay(false).withZeroPrefixedMonth(false)
                .withSeparator('.').withShortYear(true).withBaseCentury(21).withCenturyBoundaryYear(30).withPreviousCenturyBelowBoundary(false);
        // short year will make the dates switch to a century based on two digits only
        // years below boundary year (including) will be in 21st century, and above boundary year will be in 20th
        this.assertDates(pattern,
                new LocalDate[]{
                        LocalDate.of(2020, 1, 1),
                        LocalDate.of(2015, 5, 28),
                        LocalDate.of(1938, 10, 2),
                        LocalDate.of(2000, 11, 13),
                        LocalDate.of(2030, 10, 1),
                },
                new String[]{"1.1.20", "28.5.15", "2.10.38", "13.11.00", "1.10.30"});
    }

}