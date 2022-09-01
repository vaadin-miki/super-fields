package org.vaadin.miki.superfields.variant;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default grouping of the properties - a single group with one element, a property.
 * @author miki
 * @since 2022-09-01
 */
// TODO the name is ridiculous, change it ;)
public class DefaultObjectPropertyDefinitionGroupingProvider implements ObjectPropertyDefinitionGroupingProvider {

    @Override
    public <T> Map<String, List<ObjectPropertyDefinition<T, ?>>> groupDefinitions(List<ObjectPropertyDefinition<T, ?>> definitions) {
        final var result = new HashMap<String, List<ObjectPropertyDefinition<T, ?>>>();
        definitions.forEach(def -> result.put(def.getName(), Collections.singletonList(def)));
        return result;
    }
}
