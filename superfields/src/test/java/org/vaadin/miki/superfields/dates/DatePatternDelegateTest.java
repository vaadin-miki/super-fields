package org.vaadin.miki.superfields.dates;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vaadin.miki.shared.dates.DatePatterns;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import com.vaadin.browserless.BrowserlessUIContext;

public class DatePatternDelegateTest {

  private BrowserlessUIContext window;

  private SuperDatePicker datePicker;

  @BeforeEach
  public void setUp() {
    this.window = BrowserlessUIContext.forComponent(() -> {
      this.datePicker = new SuperDatePicker().withDatePattern(DatePatterns.YYYY_MM_DD).withLocale(new Locale.Builder().setLanguage("pl").setRegion("PL").build());
      return this.datePicker;
    });
  }

  @AfterEach
  public void closeWindow() {
    if (this.window != null) {
      this.window.close();
    }
  }

  @Test
  public void testFormattedValue() {
    LocalDate expected = LocalDate.of(1999, 5, 3);
    this.datePicker.setValue(expected);
    LocalDate value = this.datePicker.getValue();
    Assertions.assertEquals(expected, value);
    String raw = this.datePicker.getFormattedValue();
    Assertions.assertEquals("1999-05-03", raw);

    // now server-side month formatting
    this.datePicker.setDatePattern(DatePatterns.D_MMMM_YYYY);
    raw = this.datePicker.getFormattedValue();
    Assertions.assertEquals("3 maja 1999", raw);

    this.datePicker.setLocale(new Locale.Builder().setLanguage("pl").setRegion("PL").build());
    this.datePicker.setDatePattern(null);

    // now formatted according to locale
    raw = this.datePicker.getFormattedValue();
    final String formatted = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(this.datePicker.getLocale()).format(expected);
    Assertions.assertEquals(formatted, raw);
  }

}