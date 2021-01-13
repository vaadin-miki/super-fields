package org.vaadin.miki.superfields.dates;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Locale;

public class SuperDatePickerI18nTest {

    private SuperDatePickerI18n i18n;

    @Before
    public void setUp() {
        MockVaadin.setup();
        this.i18n = new SuperDatePickerI18n(new Locale("pl", "PL"));
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void setProperlyInitialised() {
        Assert.assertEquals(new Locale("pl", "PL"), this.i18n.getLocale());
        Assert.assertEquals("Kalendarz", this.i18n.getCalendar());
        Assert.assertEquals("Anuluj", this.i18n.getCancel());
        Assert.assertEquals("Wyczyść", this.i18n.getClear());
        Assert.assertEquals("Dzisiaj", this.i18n.getToday());
        Assert.assertEquals("Tydzień", this.i18n.getWeek());
        Assert.assertEquals(Arrays.asList("styczeń", "luty", "marzec", "kwiecień", "maj", "czerwiec", "lipiec", "sierpień", "wrzesień", "październik", "listopad", "grudzień"), this.i18n.getMonthNames());
        Assert.assertEquals(Arrays.asList("niedziela", "poniedziałek", "wtorek", "środa", "czwartek", "piątek", "sobota"), this.i18n.getWeekdays());
        Assert.assertEquals(Arrays.asList("niedz.", "pon.", "wt.", "śr.", "czw.", "pt.", "sob."), this.i18n.getWeekdaysShort());
        Assert.assertEquals(1, this.i18n.getFirstDayOfWeek());
        Assert.assertEquals(Arrays.asList("stycznia", "lutego", "marca", "kwietnia", "maja", "czerwca", "lipca", "sierpnia", "września", "października", "listopada", "grudnia"), this.i18n.getDisplayMonthNames());
    }
}