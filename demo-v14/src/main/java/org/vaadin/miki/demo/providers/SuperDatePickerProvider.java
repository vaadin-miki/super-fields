package org.vaadin.miki.demo.providers;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.shared.dates.DatePatterns;
import org.vaadin.miki.superfields.dates.SuperDatePicker;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Provides {@link SuperDatePicker}.
 * @author miki
 * @since 2020-11-18
 */
@Order(50)
public class SuperDatePickerProvider implements ComponentProvider<SuperDatePicker>, Validator<LocalDate> {

    @Override
    public SuperDatePicker getComponent() {
        return new SuperDatePicker("Pick a date:").withDatePattern(DatePatterns.YYYY_MM_DD).withValue(LocalDate.now()).withHelperText("(default date pattern is YYYY-MM-DD)");
    }

    @Override
    public ValidationResult apply(LocalDate localDate, ValueContext valueContext) {
        return localDate != null && localDate.getDayOfWeek() == DayOfWeek.MONDAY ? ValidationResult.error("Mondays are not allowed") : ValidationResult.ok();
    }
}
