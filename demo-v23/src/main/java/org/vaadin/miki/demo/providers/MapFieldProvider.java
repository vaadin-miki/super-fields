package org.vaadin.miki.demo.providers;

import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.collections.MapField;
import org.vaadin.miki.superfields.numbers.SuperIntegerField;
import org.vaadin.miki.superfields.text.SuperTextField;

import java.util.LinkedHashMap;

/**
 * @author miki
 * @since 2022-04-08
 */
@Order(147)
public class MapFieldProvider implements ComponentProvider<MapField<String, Integer>> {
    @Override
    public MapField<String, Integer> getComponent() {
        return new MapField<>(LinkedHashMap::new, SuperTextField::new, SuperIntegerField::new);
    }
}
