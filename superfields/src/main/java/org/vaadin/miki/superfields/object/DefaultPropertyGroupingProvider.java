package org.vaadin.miki.superfields.object;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Default grouping of the properties - a single group with one element, a property.
 * @author miki
 * @since 2022-09-01
 */
public class DefaultPropertyGroupingProvider implements PropertyGroupingProvider {

    @Override
    public <T> Map<String, List<Property<T, ?>>> groupDefinitions(List<Property<T, ?>> definitions) {
        return definitions.stream().collect(Collectors.toMap(Property::getName, Collections::singletonList));
    }
}
