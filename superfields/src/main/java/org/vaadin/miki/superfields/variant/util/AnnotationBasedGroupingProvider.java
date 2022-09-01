package org.vaadin.miki.superfields.variant.util;

import org.vaadin.miki.superfields.variant.ObjectPropertyDefinition;
import org.vaadin.miki.superfields.variant.ObjectPropertyDefinitionGroupingProvider;

import java.util.List;
import java.util.Map;

/**
 * A grouping provider based on a presence of an annotation on a field in the definition.
 */
public class AnnotationBasedGroupingProvider implements ObjectPropertyDefinitionGroupingProvider {
    @Override
    public <T> Map<String, List<ObjectPropertyDefinition<T, ?>>> groupDefinitions(List<ObjectPropertyDefinition<T, ?>> definitions) {
        return null;
    }
}
