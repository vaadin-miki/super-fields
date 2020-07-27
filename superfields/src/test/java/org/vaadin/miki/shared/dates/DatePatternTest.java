package org.vaadin.miki.shared.dates;

import org.junit.Assert;
import org.junit.Test;

public class DatePatternTest {

    @Test
    public void noSeparatorMeansZeroPrefixedDayAndMonth() {
        final DatePattern pattern = new DatePattern().withZeroPrefixedDay(false).withZeroPrefixedMonth(false);
        Assert.assertTrue(pattern.hasSeparator());
        pattern.withoutSeparator();
        Assert.assertFalse(pattern.hasSeparator());
        Assert.assertTrue("zero prefixed day must be set when there is no separator", pattern.isZeroPrefixedDay());
        Assert.assertTrue("zero prefixed month must be set when there is no separator", pattern.isZeroPrefixedMonth());
    }

    @Test
    public void turningOffZeroPrefixedDaySetsDefaultSeparatorWhenWasNone() {
        final DatePattern pattern = new DatePattern().withoutSeparator();
        Assert.assertFalse(pattern.hasSeparator());
        pattern.setZeroPrefixedDay(false);
        Assert.assertEquals("separator should be reverted to default", DatePattern.DEFAULT_SEPARATOR, pattern.getSeparator());
    }

    @Test
    public void turningOffZeroPrefixedMonthSetsDefaultSeparatorWhenWasNone() {
        final DatePattern pattern = new DatePattern().withoutSeparator();
        Assert.assertFalse(pattern.hasSeparator());
        pattern.setZeroPrefixedMonth(false);
        Assert.assertEquals("separator should be reverted to default", DatePattern.DEFAULT_SEPARATOR, pattern.getSeparator());
    }

}