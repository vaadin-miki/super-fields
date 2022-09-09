package org.vaadin.miki.demo.data;

import org.vaadin.miki.superfields.util.factory.FieldGroup;
import org.vaadin.miki.superfields.util.factory.FieldOrder;

import java.time.LocalDate;
import java.util.Objects;

/**
 * @author miki
 * @since 2022-06-16
 */
public class Person {

    public static Person of(String name, LocalDate dateOfBirth, boolean nobelPrize) {
        final Person result = new Person();
        result.setName(name);
        result.setDateOfBirth(dateOfBirth);
        result.setNobelPrize(nobelPrize);
        return result;
    }

    @FieldOrder(1)
    @FieldGroup("person")
    private String name;

    @FieldOrder(2)
    @FieldGroup("person")
    private LocalDate dateOfBirth;

    @FieldOrder(3)
    @FieldGroup("person")
    private boolean nobelPrize;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean isNobelPrize() {
        return nobelPrize;
    }

    public void setNobelPrize(boolean nobelPrize) {
        this.nobelPrize = nobelPrize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return nobelPrize == person.nobelPrize && Objects.equals(name, person.name) && Objects.equals(dateOfBirth, person.dateOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dateOfBirth, nobelPrize);
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", nobelPrize=" + nobelPrize +
                '}';
    }
}
