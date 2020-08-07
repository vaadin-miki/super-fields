package org.vaadin.miki;

/**
 * Simple data class to showcase in grid select demo.
 * @author miki
 * @since 2020-08-07
 */
// needs to be public, otherwise grid cannot access it
public class SuperFieldsGridItem {

    private Class<?> type;

    private String name;

    SuperFieldsGridItem(Class<?> type) {
        this.type = type;
        this.name = type.getSimpleName();
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
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
