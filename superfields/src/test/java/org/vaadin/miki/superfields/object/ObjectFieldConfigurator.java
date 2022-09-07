package org.vaadin.miki.superfields.object;

import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import org.vaadin.miki.superfields.collections.CollectionField;
import org.vaadin.miki.superfields.collections.MapEntryField;
import org.vaadin.miki.superfields.collections.MapField;
import org.vaadin.miki.superfields.dates.SuperDatePicker;
import org.vaadin.miki.superfields.dates.SuperDateTimePicker;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;
import org.vaadin.miki.superfields.numbers.SuperBigDecimalField;
import org.vaadin.miki.superfields.numbers.SuperDoubleField;
import org.vaadin.miki.superfields.numbers.SuperIntegerField;
import org.vaadin.miki.superfields.numbers.SuperLongField;
import org.vaadin.miki.superfields.text.LabelField;
import org.vaadin.miki.superfields.text.SuperTextArea;
import org.vaadin.miki.superfields.text.SuperTextField;
import org.vaadin.miki.superfields.object.builder.SimplePropertyComponentBuilder;
import org.vaadin.miki.superfields.object.reflect.AnnotationMetadataProvider;
import org.vaadin.miki.superfields.object.reflect.ReflectivePropertyProvider;
import org.vaadin.miki.superfields.object.util.MetadataBasedGroupingProvider;
import org.vaadin.miki.util.ReflectTools;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ObjectFieldConfigurator {

    public static final ObjectFieldConfigurator INSTANCE = new ObjectFieldConfigurator();

    public static final String MULTILINE_METADATA_PROPERTY = "multiline";
    public static final String GROUP_METADATA_PROPERTY = "group";
    public static final String ORDER_METADATA_PROPERTY = "order";
    public static final String CAPTION_METADATA_PROPERTY = "caption";
    public static final String READ_ONLY_METADATA_PROPERTY = "read-only";
    public static final String COLLECTION_ELEMENT_TYPE_METADATA_PROPERTY = "collection-element";
    public static final String MAP_KEY_TYPE_METADATA_PROPERTY = "map-key";
    public static final String MAP_VALUE_TYPE_METADATA_PROPERTY = "map-value";

    private static final Set<Class<?>> EXPECTED_BOOLEAN_TYPES = Set.of(Boolean.class, boolean.class);

    private static <T> CollectionField<T, List<T>> buildCollectionField(Property<?, T> definition, Class<? extends Collection<?>> collectionType, SimplePropertyComponentBuilder callbackFactory) {
        return new CollectionField<>(ArrayList::new, () -> callbackFactory.buildPropertyField(definition).orElseThrow(() -> new IllegalArgumentException(String.format("cannot build collection element field (type %s)", definition.getType().getSimpleName()))));
    }

    private static <K, V> MapField<K, V> buildMapField(Property<?, K> keyDefinition, Property<?, V> valueDefinition, SimplePropertyComponentBuilder callbackFactory) {
        return new MapField<>(LinkedHashMap::new, FlexLayoutHelpers::column, () -> new MapEntryField<>(
                FlexLayoutHelpers::row,
                () -> callbackFactory.buildPropertyField(keyDefinition).orElseThrow(() -> new IllegalArgumentException(String.format("cannot build map key element field (type %s)", keyDefinition.getType().getSimpleName()))),
                () -> callbackFactory.buildPropertyField(valueDefinition).orElseThrow(() -> new IllegalArgumentException(String.format("cannot build map value element field (type %s)", valueDefinition.getType().getSimpleName())))
        ));
    }

    private static void setLabel(Object component, String label) {
        if(component instanceof HasLabel)
            ((HasLabel) component).setLabel(label);
        else if(component instanceof org.vaadin.miki.markers.HasLabel)
            ((org.vaadin.miki.markers.HasLabel) component).setLabel(label);
    }

    @SuppressWarnings("unchecked")
    private SimplePropertyComponentBuilder buildFactory() {
        final SimplePropertyComponentBuilder result = new SimplePropertyComponentBuilder()
                .withRegisteredType(Boolean.class, Checkbox::new)
                .withRegisteredType(boolean.class, Checkbox::new)
                .withRegisteredType(Integer.class, SuperIntegerField::new)
                .withRegisteredType(int.class, SuperIntegerField::new)
                .withRegisteredType(Long.class, SuperLongField::new)
                .withRegisteredType(long.class, SuperLongField::new)
                .withRegisteredType(Double.class, SuperDoubleField::new)
                .withRegisteredType(double.class, SuperDoubleField::new)
                .withRegisteredType(BigDecimal.class, SuperBigDecimalField::new)
                .withRegisteredType(LocalDate.class, SuperDatePicker::new)
                .withRegisteredType(LocalDateTime.class, SuperDateTimePicker::new)
                .withRegisteredBuilder(String.class, def -> def.getMetadata().containsKey(MULTILINE_METADATA_PROPERTY)
                        && EXPECTED_BOOLEAN_TYPES.contains(def.getMetadata().get(MULTILINE_METADATA_PROPERTY).getValueType())
                        && Objects.equals(Boolean.TRUE, def.getMetadata().get(MULTILINE_METADATA_PROPERTY).getValue()) ?
                        new SuperTextArea() :
                        new SuperTextField());
        result.withRegisteredBuilder(def -> def.getMetadata().containsKey(COLLECTION_ELEMENT_TYPE_METADATA_PROPERTY), def -> {
                    final Class<?> collectionType = def.getType();
                    final Property<?, ?> listDef = (Property<?, ?>) def.getMetadata().get(COLLECTION_ELEMENT_TYPE_METADATA_PROPERTY).getValue();
                    return (HasValue<?, Object>) (HasValue<?, ?>) buildCollectionField(listDef, (Class<? extends Collection<?>>) collectionType, result);
                })
                .withRegisteredBuilder(def -> def.getMetadata().containsKey(MAP_KEY_TYPE_METADATA_PROPERTY) && def.getMetadata().containsKey(MAP_VALUE_TYPE_METADATA_PROPERTY), def -> {
                    final Property<?, ?> keyDef = (Property<?, ?>) def.getMetadata().get(MAP_KEY_TYPE_METADATA_PROPERTY).getValue();
                    final Property<?, ?> valueDef = (Property<?, ?>) def.getMetadata().get(MAP_VALUE_TYPE_METADATA_PROPERTY).getValue();
                    return (HasValue<?, Object>) (HasValue<?, ?>) buildMapField(keyDef, valueDef, result);
                });
        result.setDefaultBuilder(def -> {
            try {
                final Object instance = ReflectTools.newInstance(def.getType());
                final ObjectField<Object> field = new ObjectField<>(def.getType(), () -> instance, FlexLayoutHelpers::column);
                configureObjectField(field);
                return field;
            }
            catch(IllegalArgumentException iae) {
                return new LabelField<>();
            }
        });
        return result;
    }

    public <T> void configureObjectField(ObjectField<T> objectField) {
        objectField.setPropertyProvider(new ReflectivePropertyProvider().withMetadataProvider(new AnnotationMetadataProvider()
                        .withRegisteredAnnotation(GROUP_METADATA_PROPERTY, FieldGroup.class, String.class, FieldGroup::value)
                        .withRegisteredAnnotation(ORDER_METADATA_PROPERTY, FieldOrder.class, int.class, FieldOrder::value)
                        .withRegisteredAnnotation(MULTILINE_METADATA_PROPERTY, BigField.class)
                        .withRegisteredAnnotation(CAPTION_METADATA_PROPERTY, FieldCaption.class, String.class, FieldCaption::value),
                (name, field, setter, getter) -> setter == null ? Collections.singleton(new PropertyMetadata(READ_ONLY_METADATA_PROPERTY, boolean.class, true)) : Collections.emptySet(),
                (name, field, setter, getter) -> {
                    if(Collection.class.isAssignableFrom(field.getType()))
                        return ReflectTools.extractGenericType(field, 0).map(type -> new PropertyMetadata(COLLECTION_ELEMENT_TYPE_METADATA_PROPERTY, Property.class, new Property<>(field.getDeclaringClass(), name + ".element", type, null, null))).map(Collections::singleton).orElse(Collections.emptySet());
                    else return Collections.emptySet();
                },
                (name, field, setter, getter) -> {
                    if(Map.class.isAssignableFrom(field.getType())) {
                        final List<PropertyMetadata> metadata = new ArrayList<>();
                        ReflectTools.extractGenericType(field, 0).map(type -> new PropertyMetadata(MAP_KEY_TYPE_METADATA_PROPERTY, Property.class, new Property<>(field.getDeclaringClass(), name + ".key", type, null, null))).ifPresent(metadata::add);
                        ReflectTools.extractGenericType(field, 1).map(type -> new PropertyMetadata(MAP_VALUE_TYPE_METADATA_PROPERTY, Property.class, new Property<>(field.getDeclaringClass(), name + ".value", type, null, null))).ifPresent(metadata::add);
                        return metadata.size() == 2 ? metadata : Collections.emptySet();
                    }
                    else return Collections.emptySet();
                }
        ));
        objectField.setPropertyGroupingProvider(new MetadataBasedGroupingProvider().withGroupingMetadataName(GROUP_METADATA_PROPERTY).withSortingMetadataName(ORDER_METADATA_PROPERTY));
        objectField.setPropertyComponentBuilder(this.buildFactory());
        objectField.addComponentConfigurator((object, definition, component) -> {
            final Map<String, PropertyMetadata> metadataMap = definition.getMetadata();
            if(metadataMap.containsKey(CAPTION_METADATA_PROPERTY) && metadataMap.get(CAPTION_METADATA_PROPERTY).getValue() != null)
                setLabel(component, metadataMap.get(CAPTION_METADATA_PROPERTY).getValue().toString());
        });
        objectField.addComponentConfigurator((object, definition, component) -> {
            if(definition.getMetadata().containsKey(READ_ONLY_METADATA_PROPERTY) && definition.getMetadata().get(READ_ONLY_METADATA_PROPERTY).getValueType() == boolean.class)
                component.setReadOnly((boolean) definition.getMetadata().get(READ_ONLY_METADATA_PROPERTY).getValue());
        });
    }

    private ObjectFieldConfigurator() {
        // no instances allowed
    }
}
