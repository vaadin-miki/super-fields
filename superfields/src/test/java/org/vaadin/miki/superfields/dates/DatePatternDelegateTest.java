package org.vaadin.miki.superfields.dates;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.miki.shared.dates.DatePatterns;

import java.time.LocalDate;
import java.util.Locale;

public class DatePatternDelegateTest {

  private SuperDatePicker datePicker;

  @Before
  public void setUp() {
    MockVaadin.setup();
    this.datePicker = new SuperDatePicker().withDatePattern(DatePatterns.YYYY_MM_DD).withLocale(new Locale("pl", "PL"));
  }

  @After
  public void tearDown() {
    MockVaadin.tearDown();
  }

  @Test
  public void testFormattedValue() {
    LocalDate expected = LocalDate.of(1999, 5, 3);
    this.datePicker.setValue(expected);
    LocalDate value = this.datePicker.getValue();
    Assert.assertEquals(expected, value);
    String raw = this.datePicker.getFormattedValue();
    Assert.assertEquals("1999-05-03", raw);

    // now server-side month formatting
    this.datePicker.setDatePattern(DatePatterns.D_MMMM_YYYY);
    raw = this.datePicker.getFormattedValue();
    Assert.assertEquals("3 maja 1999", raw);

    this.datePicker.setLocale(new Locale("pl", "PL"));
    this.datePicker.setDatePattern(null);

    // now formatted according to locale
    raw = this.datePicker.getFormattedValue();
    Assert.assertEquals("3.05.1999", raw);
  }

}