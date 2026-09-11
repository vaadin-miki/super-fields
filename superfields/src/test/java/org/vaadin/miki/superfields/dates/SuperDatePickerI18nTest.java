package org.vaadin.miki.superfields.dates;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Locale;

public class SuperDatePickerI18nTest {

  private SuperDatePickerI18n i18n;

  @BeforeEach
  public void setUp() {
    this.i18n = new SuperDatePickerI18n(new Locale.Builder().setLanguage("pl").setRegion("PL").build());
  }

  @Test
  public void setProperlyInitialised() {
    Assertions.assertEquals(new Locale.Builder().setLanguage("pl").setRegion("PL").build(), this.i18n.getLocale());
    Assertions.assertEquals("Anuluj", this.i18n.getCancel());
    Assertions.assertEquals("Dzisiaj", this.i18n.getToday());
    Assertions.assertEquals(Arrays.asList("styczeń", "luty", "marzec", "kwiecień", "maj", "czerwiec", "lipiec", "sierpień", "wrzesień", "październik", "listopad", "grudzień"), this.i18n.getMonthNames());
    Assertions.assertEquals(Arrays.asList("niedziela", "poniedziałek", "wtorek", "środa", "czwartek", "piątek", "sobota"), this.i18n.getWeekdays());
    Assertions.assertEquals(Arrays.asList("niedz.", "pon.", "wt.", "śr.", "czw.", "pt.", "sob."), this.i18n.getWeekdaysShort());
    Assertions.assertEquals(1, this.i18n.getFirstDayOfWeek());
    Assertions.assertEquals(Arrays.asList("stycznia", "lutego", "marca", "kwietnia", "maja", "czerwca", "lipca", "sierpnia", "września", "października", "listopada", "grudnia"), this.i18n.getDisplayMonthNames());
  }
}