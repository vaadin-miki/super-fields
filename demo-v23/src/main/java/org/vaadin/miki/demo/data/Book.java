package org.vaadin.miki.demo.data;

import java.util.Objects;

/**
 * @author miki
 * @since 2022-06-16
 */
public class Book {

    private String title;
    private Person author;
    private int firstPublished;
    private String summary;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    public int getFirstPublished() {
        return firstPublished;
    }

    public void setFirstPublished(int firstPublished) {
        this.firstPublished = firstPublished;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return firstPublished == book.firstPublished && Objects.equals(title, book.title) && Objects.equals(author, book.author) && Objects.equals(summary, book.summary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, firstPublished, summary);
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author=" + author +
                ", firstPublished=" + firstPublished +
                ", summary='" + summary + '\'' +
                '}';
    }
}
