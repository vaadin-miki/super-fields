package org.vaadin.miki.demo.providers;

/**
 * Simple data class to showcase in grid select demo.
 * @author miki
 * @since 2020-08-07
 */
// needs to be public, otherwise grid cannot access it
public class SuperFieldsGridItem {

    private final int nameLength;

    private String name;

    SuperFieldsGridItem(String name) {
        this.name = name;
        this.nameLength = this.name.length();
    }

    public int getNameLength() {
        return this.nameLength;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
