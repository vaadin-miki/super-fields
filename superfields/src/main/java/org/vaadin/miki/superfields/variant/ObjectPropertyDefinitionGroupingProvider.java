package org.vaadin.miki.superfields.variant;

import java.util.List;
import java.util.Map;

/**
 * Groups {@link ObjectPropertyDefinition}s.
 * @author miki
 * since 2022-09-01
 */
public interface ObjectPropertyDefinitionGroupingProvider {

    /**
     * Groups given definitions.
     * @param <T> Type parameter for {@link ObjectPropertyDefinition}s.
     * @param definitions Definitions to group.
     * @return A non-{@code null} (but possibly empty) map. The map will not contain new {@link ObjectPropertyDefinition}s, those from the parameter will be reused.
     * @implNote The groups are obtained from the map based on the order of the keys. This means a {@code LinkedHashMap} should preferably be used.
     */
    <T> Map<String, List<ObjectPropertyDefinition<T, ?>>> groupDefinitions(List<ObjectPropertyDefinition<T, ?>> definitions);

}
