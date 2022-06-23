package org.vaadin.miki.superfields.variant;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A data object to test ObjectField.
 * @author miki
 * @since 2022-06-23
 */
public class DataObject {

    private String text;

    private LocalDate date;

    private LocalDateTime timestamp;

    private boolean check;

    private int number;

    private BigDecimal currency;

    private String description;

    private float hidden;

    private final long fixed = 20220623L;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public BigDecimal getCurrency() {
        return currency;
    }

    public void setCurrency(BigDecimal currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getFixed() {
        return fixed;
    }

    protected float getHidden() {
        return hidden;
    }

    protected void setHidden(float hidden) {
        this.hidden = hidden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataObject that = (DataObject) o;
        return check == that.check && number == that.number && fixed == that.fixed && Objects.equals(text, that.text) && Objects.equals(date, that.date) && Objects.equals(timestamp, that.timestamp) && Objects.equals(currency, that.currency) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, date, timestamp, check, number, currency, description, fixed);
    }

    @Override
    public String toString() {
        return "DataObject{" +
                "text='" + text + '\'' +
                ", date=" + date +
                ", timestamp=" + timestamp +
                ", check=" + check +
                ", number=" + number +
                ", currency=" + currency +
                ", description='" + description + '\'' +
                ", fixed=" + fixed +
                '}';
    }
}
