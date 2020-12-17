package org.vaadin.miki.demo.providers;

import org.atteo.classindex.ClassIndex;
import org.vaadin.miki.demo.ComponentProvider;

import java.util.stream.StreamSupport;

/**
 * Simple data class to showcase in grid select demo.
 * @author miki
 * @since 2020-08-07
 */
// needs to be public, otherwise grid cannot access it
public class SuperFieldsGridItem {

    /**
     * Returns an array of {@link SuperFieldsGridItem} where each element represents one {@link ComponentProvider}.
     * @return A non-null array.
     */
    public static SuperFieldsGridItem[] getAllRegisteredProviders() {
        // scans all providers (as that should be all showcased components) and adds them to the grid
        return StreamSupport.stream(ClassIndex.getSubclasses(ComponentProvider.class).spliterator(), false)
                .filter(type -> !type.isInterface())
                .map(Class::getSimpleName)
                .map(s -> s.endsWith("Provider") ? s.substring(0, s.length()-8) : s)
                .map(SuperFieldsGridItem::new)
                .toArray(SuperFieldsGridItem[]::new);
    }

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
