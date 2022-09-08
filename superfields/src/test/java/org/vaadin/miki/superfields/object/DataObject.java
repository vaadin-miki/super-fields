package org.vaadin.miki.superfields.object;

import org.vaadin.miki.superfields.object.reflect.Ignore;
import org.vaadin.miki.superfields.util.factory.BigField;
import org.vaadin.miki.superfields.util.factory.FieldCaption;
import org.vaadin.miki.superfields.util.factory.FieldGroup;
import org.vaadin.miki.superfields.util.factory.FieldOrder;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A data object to test ObjectField.
 * @author miki
 * @since 2022-06-23
 */
public class DataObject {

    public static DataObject build() {
        try {
            final SecureRandom random = SecureRandom.getInstanceStrong();
            final DataObject result = new DataObject();
            result.setTimestamp(LocalDateTime.now());
            result.setDate(LocalDate.now());
            result.setText("most data is random");
            result.setDescription("even this description has this random number: " + random.nextLong());
            result.setNumber(random.nextInt(256));
            result.setCurrency(BigDecimal.valueOf(random.nextDouble()));
            result.setCheck(random.nextBoolean());
            result.setHidden(random.nextFloat());
            return result;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // field order annotation on the setter
    private String text;

    @FieldOrder(11)
    @FieldGroup("random-group")
    private LocalDate date;

    @FieldOrder(10)
    // caption annotation on setter
    private LocalDateTime timestamp;

    @FieldOrder(3)
    @FieldGroup("currency-check")
    private boolean check;

    @FieldOrder(120)
    // group annotation on getter
    // caption annotation on getter
    private int number;

    @FieldOrder(4)
    @FieldGroup("currency-check")
    private BigDecimal currency;

    @FieldOrder(2)
    @BigField
    private String description;

    @Ignore
    private float hidden;

    // no field order - should be placed last
    // caption annotation on getter
    private final long fixed = 20220623L;

    public String getText() {
        return text;
    }

    @FieldOrder(1)
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

    @FieldCaption("Date and time")
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    @FieldGroup("random-group")
    @FieldCaption("Amount")
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

    @FieldCaption("Internal information")
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
        return check == that.check && number == that.number && Objects.equals(text, that.text) && Objects.equals(date, that.date) && Objects.equals(timestamp, that.timestamp) && Objects.equals(currency, that.currency) && Objects.equals(description, that.description);
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
