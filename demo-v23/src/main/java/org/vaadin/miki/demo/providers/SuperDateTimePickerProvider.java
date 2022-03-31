package org.vaadin.miki.demo.providers;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.shared.dates.DatePatterns;
import org.vaadin.miki.superfields.dates.SuperDateTimePicker;

import java.time.LocalDateTime;

/**
 * Provides {@link SuperDateTimePicker}.
 * @author miki
 * @since 2020-11-17
 */
@Order(60)
public class SuperDateTimePickerProvider implements ComponentProvider<SuperDateTimePicker>, Validator<LocalDateTime> {

    @Override
    public SuperDateTimePicker getComponent() {
        return new SuperDateTimePicker("Pick a date and time:").withDatePattern(DatePatterns.M_D_YYYY_SLASH).withValue(LocalDateTime.now()).withHelperText("(default date pattern is month/day/year)");
    }

    @Override
    public ValidationResult apply(LocalDateTime localDateTime, ValueContext valueContext) {
        return localDateTime != null && localDateTime.getDayOfYear() < 50 ? ValidationResult.error("the first 50-or-so days of each year are not allowed") : ValidationResult.ok();
    }
}
