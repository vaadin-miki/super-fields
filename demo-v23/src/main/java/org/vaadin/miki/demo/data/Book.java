package org.vaadin.miki.demo.data;

import org.vaadin.miki.superfields.util.factory.BigField;
import org.vaadin.miki.superfields.util.factory.FieldCaption;
import org.vaadin.miki.superfields.util.factory.FieldGroup;
import org.vaadin.miki.superfields.util.factory.FieldOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author miki
 * @since 2022-06-16
 */
public class Book {

    public static Book of(String title, int firstPublished, String language, Format ownedFormat, Person... authors) {
        final Book result = new Book();
        result.setAuthors(Arrays.asList(authors));
        result.setTitle(title);
        result.setFirstPublished(firstPublished);
        result.setLanguage(language);
        result.setOwnedFormat(ownedFormat);
        return result;
    }

    @FieldOrder(1)
    private String title = "";

    @FieldOrder(2)
    private List<Person> authors = new ArrayList<>();

    @FieldOrder(3)
    @FieldGroup("publication")
    private int firstPublished;

    @FieldOrder(4)
    @FieldGroup("publication")
    @FieldCaption("Original language")
    private String language;

    @FieldOrder(6)
    @FieldGroup("publication")
    private Format ownedFormat;

    @BigField
    @FieldOrder(5)
    private String summary = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Person> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Person> authors) {
        this.authors = authors;
    }

    public int getFirstPublished() {
        return firstPublished;
    }

    public void setFirstPublished(int firstPublished) {
        this.firstPublished = firstPublished;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Format getOwnedFormat() {
        return ownedFormat;
    }

    public void setOwnedFormat(Format ownedFormat) {
        this.ownedFormat = ownedFormat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return getFirstPublished() == book.getFirstPublished() && Objects.equals(getTitle(), book.getTitle()) && Objects.equals(getAuthors(), book.getAuthors()) && Objects.equals(getLanguage(), book.getLanguage()) && getOwnedFormat() == book.getOwnedFormat() && Objects.equals(getSummary(), book.getSummary());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getAuthors(), getFirstPublished(), getLanguage(), getOwnedFormat(), getSummary());
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", authors=" + authors +
                ", firstPublished=" + firstPublished +
                ", language='" + language + '\'' +
                ", ownedFormat=" + ownedFormat +
                ", summary='" + summary + '\'' +
                '}';
    }
}
