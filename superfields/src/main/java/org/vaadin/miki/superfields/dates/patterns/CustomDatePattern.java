package org.vaadin.miki.superfields.dates.patterns;

import com.vaadin.flow.function.SerializableFunction;
import org.vaadin.miki.superfields.dates.DatePattern;

import java.time.LocalDate;
import java.util.Objects;

/**
 * A completely custom, server-side date pattern.
 * @author miki
 * @since 2020-05-26
 */
public class CustomDatePattern implements DatePattern {

    private final SerializableFunction<String, LocalDate> parsingFunction;

    private final SerializableFunction<LocalDate, String> formattingFunction;

    private final String displayName;

    /**
     * Constructs a custom pattern from two given callbacks, without a display name.
     *
     * @param parsingFunction    Implementation of {@link #parseDate(String)}. Must not be {@code null}.
     * @param formattingFunction Implementation of {@link #formatDate(LocalDate)}. Must not be {@code null}.
     */
    public CustomDatePattern(SerializableFunction<String, LocalDate> parsingFunction, SerializableFunction<LocalDate, String> formattingFunction) {
        this(null, parsingFunction, formattingFunction);
    }

    /**
     * Constructs a custom pattern from two given callbacks.
     *
     * @param displayName Display name. Used only in {@link #toString()}.
     * @param parsingFunction    Implementation of {@link #parseDate(String)}. Must not be {@code null}.
     * @param formattingFunction Implementation of {@link #formatDate(LocalDate)}. Must not be {@code null}.
     */
    public CustomDatePattern(String displayName, SerializableFunction<String, LocalDate> parsingFunction, SerializableFunction<LocalDate, String> formattingFunction) {
        this.displayName = displayName;
        this.parsingFunction = parsingFunction;
        this.formattingFunction = formattingFunction;
    }

    @Override
    public LocalDate parseDate(String text) {
        return parsingFunction.apply(text);
    }

    @Override
    public String formatDate(LocalDate date) {
        return formattingFunction.apply(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomDatePattern that = (CustomDatePattern) o;
        return parsingFunction.equals(that.parsingFunction) &&
                formattingFunction.equals(that.formattingFunction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parsingFunction, formattingFunction);
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}
