package org.vaadin.miki.superfields.object.util;

import org.vaadin.miki.superfields.object.Property;
import org.vaadin.miki.superfields.object.PropertyGroupingProvider;
import org.vaadin.miki.superfields.object.PropertyMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A grouping provider based on a {@link String} metadata property.
 * Can optionally also sort, based on another {@link Integer} metadata property.
 * Grouping metadata must be of type {@link #ACCEPTED_GROUPING_METADATA_TYPE}. When the group name is not specified, a single-element group with the property is created, with the name of that property as the name of the group.
 * Sorting metadata must be of type that belongs to {@link #ACCEPTED_SORTING_METADATA_TYPES}. Sorting is done by value of the metadata first, then by name of the property.
 *
 * @author miki
 * @since 2022-09-02
 */
public class MetadataBasedGroupingProvider implements PropertyGroupingProvider {

    public static final Set<Class<?>> ACCEPTED_SORTING_METADATA_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(Integer.class, int.class)));
    public static final Class<String> ACCEPTED_GROUPING_METADATA_TYPE = String.class;

    private String groupingMetadataName;
    private String sortingMetadataName;

    @Override
    public <T> Map<String, List<Property<T, ?>>> groupDefinitions(List<Property<T, ?>> definitions) {
        // sort based on the value of metadata
        if(this.getSortingMetadataName() != null)
            definitions.sort(Comparator.<Property<T, ?>>comparingInt(def -> {
                final PropertyMetadata metadata = def.getMetadata().get(this.getSortingMetadataName());
                if (metadata != null && ACCEPTED_SORTING_METADATA_TYPES.contains(metadata.getValueType()))
                    return (Integer) metadata.getValue();
                else return Integer.MAX_VALUE;
            }).thenComparing(Property::getName));

        // group based on the value of another metadata, if present
        final Map<String, List<Property<T, ?>>> result = new LinkedHashMap<>();
        if(this.getGroupingMetadataName() != null)
            definitions.forEach(def -> {
                final PropertyMetadata metadata = def.getMetadata().get(this.getGroupingMetadataName());
                final String groupName = metadata != null && ACCEPTED_GROUPING_METADATA_TYPE.isInstance(metadata.getValue()) ? ACCEPTED_GROUPING_METADATA_TYPE.cast(metadata.getValue()) : def.getName();
                result.computeIfAbsent(groupName, g -> new ArrayList<>()).add(def);
            });
        else definitions.forEach(def -> result.put(def.getName(), Collections.singletonList(def)));
        return result;
    }

    /**
     * Returns name of the metadata property used to obtain group names.
     * @return Name of the metadata property. May be {@code null} when no metadata property was specified.
     */
    public String getGroupingMetadataName() {
        return groupingMetadataName;
    }

    /**
     * Sets the name of the metadata property used to obtain group names.
     * May be set to {@code null} do disable grouping. In such case each {@link Property} will be in its own, single element group, regardless of the metadata.
     * @param groupingMetadataName Name of the metadata. Its type must be {@link #ACCEPTED_GROUPING_METADATA_TYPE}.
     */
    public void setGroupingMetadataName(String groupingMetadataName) {
        this.groupingMetadataName = groupingMetadataName;
    }

    /**
     * Chains {@link #setGroupingMetadataName(String)} and returns itself.
     * @param metadataName Name of the metadata.
     * @return This.
     * @see #setGroupingMetadataName(String)
     */
    public final MetadataBasedGroupingProvider withGroupingMetadataName(String metadataName) {
        this.setGroupingMetadataName(metadataName);
        return this;
    }

    /**
     * Returns the current name of the metadata property used for sorting.
     * @return Metadata name. May be {@code null}.
     */
    public String getSortingMetadataName() {
        return sortingMetadataName;
    }

    /**
     * Sets the name of the metadata property used for sorting the properties.
     * May be set to {@code null} to disable sorting.
     * @param sortingMetadataName Name of the metadata. Its type must belong to {@link #ACCEPTED_SORTING_METADATA_TYPES}.
     */
    public void setSortingMetadataName(String sortingMetadataName) {
        this.sortingMetadataName = sortingMetadataName;
    }

    /**
     * Chains {@link #setSortingMetadataName(String)} and returns itself.
     * @param metadataName Metadata name.
     * @return This.
     * @see #setSortingMetadataName(String)
     */
    public final MetadataBasedGroupingProvider withSortingMetadataName(String metadataName) {
        this.setSortingMetadataName(metadataName);
        return this;
    }
}
