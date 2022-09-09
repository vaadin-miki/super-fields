package org.vaadin.miki.superfields.object;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Groups {@link Property}s.
 * @author miki
 * since 2022-09-01
 */
public interface PropertyGroupingProvider extends Serializable {

    /**
     * Groups given definitions.
     * @param <T> Type parameter for {@link Property}s.
     * @param definitions Definitions to group.
     * @return A non-{@code null} (but possibly empty) map. The map will not contain new {@link Property}s, those from the parameter will be reused.
     * @implNote The groups are obtained from the map based on the order of the keys. This means a {@code LinkedHashMap} should preferably be used.
     */
    <T> Map<String, List<Property<T, ?>>> groupDefinitions(List<Property<T, ?>> definitions);

}
