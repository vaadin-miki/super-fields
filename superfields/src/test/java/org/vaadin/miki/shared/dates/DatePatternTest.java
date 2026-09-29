package org.vaadin.miki.shared.dates;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DatePatternTest {

    @Test
    public void noSeparatorMeansZeroPrefixedDayAndMonth() {
        final DatePattern pattern = new DatePattern().withZeroPrefixedDay(false).withMonthDisplayMode(DatePattern.MonthDisplayMode.NUMBER);
        Assertions.assertTrue(pattern.hasSeparator());
        pattern.withoutSeparator();
        Assertions.assertFalse(pattern.hasSeparator());
        Assertions.assertTrue(pattern.isZeroPrefixedDay(), "zero prefixed day must be set when there is no separator");
        Assertions.assertEquals(DatePattern.MonthDisplayMode.ZERO_PREFIXED_NUMBER, pattern.getMonthDisplayMode(), "zero prefixed month must be set when there is no separator");
    }

    @Test
    public void turningOffZeroPrefixedDaySetsDefaultSeparatorWhenWasNone() {
        final DatePattern pattern = new DatePattern().withoutSeparator();
        Assertions.assertFalse(pattern.hasSeparator());
        pattern.setZeroPrefixedDay(false);
        Assertions.assertEquals(DatePattern.DEFAULT_SEPARATOR, pattern.getSeparator(), "separator should be reverted to default");
    }

    @Test
    public void turningOffZeroPrefixedMonthSetsDefaultSeparatorWhenWasNone() {
        final DatePattern pattern = new DatePattern().withoutSeparator();
        Assertions.assertFalse(pattern.hasSeparator());
        pattern.withMonthDisplayMode(DatePattern.MonthDisplayMode.NUMBER);
        Assertions.assertEquals(DatePattern.DEFAULT_SEPARATOR, pattern.getSeparator(), "separator should be reverted to default");
    }

}