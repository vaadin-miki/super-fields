package org.vaadin.miki.demo.providers;

import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.collections.MapEntryField;
import org.vaadin.miki.superfields.collections.MapField;
import org.vaadin.miki.superfields.numbers.SuperIntegerField;
import org.vaadin.miki.superfields.text.SuperTextField;
import org.vaadin.miki.superfields.util.CollectionComponentProviders;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * @author miki
 * @since 2022-04-08
 */
@Order(147)
public class MapFieldProvider implements ComponentProvider<MapField<String, Integer>> {
    @Override
    public MapField<String, Integer> getComponent() {
        return new MapField<>(LinkedHashMap::new,
                CollectionComponentProviders.columnWithHeaderAndFooterRows(
                        Arrays.asList(
                                CollectionComponentProviders.removeAllButton("Clear"),
                                CollectionComponentProviders.addFirstButton("Add as first")
                        ),
                        Collections.singletonList(CollectionComponentProviders.addLastButton("Add as last"))),
                CollectionComponentProviders.rowWithRemoveButtonFirst((i, c) -> new MapEntryField<>(
                        () -> new SuperTextField("Any text:"), () -> new SuperIntegerField("Any integer:")
                ), "Remove"));
    }
}
