package org.vaadin.miki.superfields.variant.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.miki.superfields.variant.ObjectPropertyDefinition;
import org.vaadin.miki.superfields.variant.ObjectPropertyDefinitionGroupingProvider;
import org.vaadin.miki.superfields.variant.ObjectPropertyMetadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A grouping provider based on a {@link String} metadata property.
 * Can optionally also sort, based on another {@link Integer} metadata property.
 * @author miki
 * @since 2022-09-02
 */
public class MetadataBasedGroupingProvider implements ObjectPropertyDefinitionGroupingProvider {

    public static final Set<Class<?>> ACCEPTED_SORTING_METADATA_TYPES = Set.of(Integer.class, int.class);
    public static final Class<String> ACCEPTED_GROUPING_METADATA_TYPE = String.class;

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataBasedGroupingProvider.class);

    private String groupingMetadataName;
    private String sortingMetadataName;

    @Override
    public <T> Map<String, List<ObjectPropertyDefinition<T, ?>>> groupDefinitions(List<ObjectPropertyDefinition<T, ?>> definitions) {

        // sort based on the value of metadata
        if(this.getSortingMetadataName() != null)
            definitions.sort(Comparator.<ObjectPropertyDefinition<T, ?>>comparingInt(def -> {
                final ObjectPropertyMetadata metadata = def.getMetadata().get(this.getSortingMetadataName());
                if (metadata != null && ACCEPTED_SORTING_METADATA_TYPES.contains(metadata.getValueType()))
                    return (Integer) metadata.getValue();
                else return Integer.MAX_VALUE;
            }).thenComparing(ObjectPropertyDefinition::getName));

        // group based on the value of another metadata, if present
        final Map<String, List<ObjectPropertyDefinition<T, ?>>> result = new LinkedHashMap<>();
        if(this.getGroupingMetadataName() != null)
            definitions.forEach(def -> {
                final ObjectPropertyMetadata metadata = def.getMetadata().get(this.getGroupingMetadataName());
                final String groupName = metadata != null && ACCEPTED_GROUPING_METADATA_TYPE.isInstance(metadata.getValue()) ? ACCEPTED_GROUPING_METADATA_TYPE.cast(metadata.getValue()) : def.getName();
                result.computeIfAbsent(groupName, g -> new ArrayList<>()).add(def);
            });
        else definitions.forEach(def -> result.put(def.getName(), Collections.singletonList(def)));
        return result;
    }

    public String getGroupingMetadataName() {
        return groupingMetadataName;
    }

    public void setGroupingMetadataName(String groupingMetadataName) {
        this.groupingMetadataName = groupingMetadataName;
    }

    public final MetadataBasedGroupingProvider withGroupingMetadataName(String metadataName) {
        this.setGroupingMetadataName(metadataName);
        return this;
    }

    public String getSortingMetadataName() {
        return sortingMetadataName;
    }

    public void setSortingMetadataName(String sortingMetadataName) {
        this.sortingMetadataName = sortingMetadataName;
    }

    public final MetadataBasedGroupingProvider withSortingMetadataName(String metadataName) {
        this.setSortingMetadataName(metadataName);
        return this;
    }
}
