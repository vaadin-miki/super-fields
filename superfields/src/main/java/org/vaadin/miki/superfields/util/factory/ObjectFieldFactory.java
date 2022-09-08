package org.vaadin.miki.superfields.util.factory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.function.SerializableSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.miki.superfields.collections.CollectionController;
import org.vaadin.miki.superfields.collections.CollectionField;
import org.vaadin.miki.superfields.collections.CollectionLayoutProvider;
import org.vaadin.miki.superfields.collections.CollectionValueComponentProvider;
import org.vaadin.miki.superfields.collections.MapEntryField;
import org.vaadin.miki.superfields.collections.MapField;
import org.vaadin.miki.superfields.dates.SuperDatePicker;
import org.vaadin.miki.superfields.dates.SuperDateTimePicker;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;
import org.vaadin.miki.superfields.numbers.SuperBigDecimalField;
import org.vaadin.miki.superfields.numbers.SuperDoubleField;
import org.vaadin.miki.superfields.numbers.SuperIntegerField;
import org.vaadin.miki.superfields.numbers.SuperLongField;
import org.vaadin.miki.superfields.object.ComponentConfigurator;
import org.vaadin.miki.superfields.object.ComponentGroupConfigurator;
import org.vaadin.miki.superfields.object.ObjectField;
import org.vaadin.miki.superfields.object.Property;
import org.vaadin.miki.superfields.object.PropertyComponentBuilder;
import org.vaadin.miki.superfields.object.PropertyGroupLayoutProvider;
import org.vaadin.miki.superfields.object.PropertyGroupingProvider;
import org.vaadin.miki.superfields.object.PropertyMetadata;
import org.vaadin.miki.superfields.object.PropertyProvider;
import org.vaadin.miki.superfields.object.builder.FieldBuilder;
import org.vaadin.miki.superfields.object.builder.SimplePropertyComponentBuilder;
import org.vaadin.miki.superfields.object.reflect.AnnotationMetadataProvider;
import org.vaadin.miki.superfields.object.reflect.ReflectivePropertyProvider;
import org.vaadin.miki.superfields.object.util.MetadataBasedGroupingProvider;
import org.vaadin.miki.superfields.text.LabelField;
import org.vaadin.miki.superfields.text.SuperTextArea;
import org.vaadin.miki.superfields.text.SuperTextField;
import org.vaadin.miki.util.ReflectTools;
import org.vaadin.miki.util.StringTools;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Configures {@link ObjectField} to work "out of the box" for most common use cases.
 * Allows overriding most settings either directly or by subclassing.
 *
 * @author miki
 * @since 2022-09-08
 */
public class ObjectFieldFactory {

    public static final String MULTILINE_METADATA_PROPERTY = "multiline";
    public static final String GROUP_METADATA_PROPERTY = "group";
    public static final String ORDER_METADATA_PROPERTY = "order";
    public static final String CAPTION_METADATA_PROPERTY = "caption";
    public static final String READ_ONLY_METADATA_PROPERTY = "read-only";
    public static final String COLLECTION_ELEMENT_TYPE_METADATA_PROPERTY = "collection-element";
    public static final String MAP_KEY_TYPE_METADATA_PROPERTY = "map-key";
    public static final String MAP_VALUE_TYPE_METADATA_PROPERTY = "map-value";
    public static final String SHOW_AS_COMPONENT_METADATA_PROPERTY = "show-as";
    public static final String COMPONENT_BUILDER_METADATA_PROPERTY = "build-with";

    private static final Set<Class<?>> EXPECTED_BOOLEAN_TYPES = Set.of(Boolean.class, boolean.class);

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectFieldFactory.class);

    private static final Map<Class<?>, SerializableSupplier<?>> REFERENCE_COLLECTION_TYPES = Map.of(
            List.class, ArrayList::new,
            Set.class, LinkedHashSet::new
    );

    private static void setLabel(Object component, String label) {
        if(component instanceof HasLabel)
            ((HasLabel) component).setLabel(label);
        else if(component instanceof org.vaadin.miki.markers.HasLabel)
            ((org.vaadin.miki.markers.HasLabel) component).setLabel(label);
    }

    private static String[] sanitiseStyles(Collection<String> strings, String groupName) {
        return strings.stream().map(style -> String.format(style, groupName.replace(' ', '_'))).toArray(String[]::new);
    }

    private CollectionLayoutProvider<?> collectionFieldLayoutProvider = (index, controller) -> FlexLayoutHelpers.column();
    private CollectionLayoutProvider<?> mapFieldLayoutProvider = (index, controller) -> FlexLayoutHelpers.column();
    private SerializableSupplier<? extends HasComponents> mapEntryFieldLayoutProvider = FlexLayoutHelpers::row;
    private SerializableSupplier<? extends HasComponents> objectFieldLayoutProvider = FlexLayoutHelpers::column;
    private SerializableSupplier<? extends HasComponents> objectFieldGroupLayoutProvider = FlexLayoutHelpers::row;
    private Collection<String> groupLayoutStyleNames = new ArrayList<>(Arrays.asList("object-field-group-%s", "object-field-group"));
    private Collection<String> groupComponentStyleNames = new ArrayList<>(Arrays.asList("object-field-group-element", "object-field-group-element-%s"));

    private final Map<Class<?>, SerializableSupplier<?>> instanceSuppliers = new HashMap<>();
    private final Map<Class<?>, SerializableSupplier<?>> emptyCollectionSuppliers = new HashMap<>();
    private SerializableSupplier<Map<?, ?>> emptyMapSupplier = LinkedHashMap::new;

    /**
     * Constructs the factory.
     */
    public ObjectFieldFactory() {
        this.emptyCollectionSuppliers.putAll(REFERENCE_COLLECTION_TYPES);
    }

    /**
     * Builds a collection field.
     * @param elementProperty Property for the element of the collection.
     * @param collectionType Type of the collection.
     * @param callbackFactory Callback factory to build components.
     * @return An instance of a {@link CollectionField}.
     * @param <T> Type of element in the collection.
     * @param <C> Type of collection.
     */
    @SuppressWarnings("unchecked") // should be fine
    protected  <T, C extends Collection<T>> CollectionField<T, C> buildCollectionField(Property<?, T> elementProperty, Class<? extends Collection<?>> collectionType, PropertyComponentBuilder callbackFactory) {
        return new CollectionField<>(
                this.getEmptyCollectionProvider((Class<C>) collectionType),
                this.getCollectionFieldLayoutProvider(),
                () -> callbackFactory.buildPropertyField(elementProperty).orElseThrow(() -> new IllegalArgumentException(String.format("cannot build collection element field (type %s)", elementProperty.getType().getSimpleName())))
        );
    }

    /**
     * Builds a map field.
     * @param keyProperty Property for the key of the map.
     * @param valueProperty Property for the value of the map.
     * @param callbackFactory Factory to produce components.
     * @return A {@link MapField}.
     * @param <K> Type of key.
     * @param <V> Type of value.
     */
    @SuppressWarnings({"unchecked", "squid:S1604"}) // some weird types and generics here, but all should be fine
    protected <K, V> MapField<K, V> buildMapField(Property<?, K> keyProperty, Property<?, V> valueProperty, PropertyComponentBuilder callbackFactory) {
        return new MapField<>(
                (SerializableSupplier<Map<K, V>>) (SerializableSupplier<?>) this.getEmptyMapSupplier(),
                this.getMapFieldLayoutProvider(),
                // due to generics magic this probably cannot be simplified to a lambda
                new CollectionValueComponentProvider<Map.Entry<K, V>, MapEntryField<K, V>>() {
                    @Override
                    public MapEntryField<K, V> provideComponent(int index, CollectionController controller) {
                        return new MapEntryField<>(
                                getMapEntryFieldLayoutProvider(),
                                () -> callbackFactory.buildPropertyField(keyProperty).orElseThrow(() -> new IllegalArgumentException(String.format("cannot build map key element field (type %s)", keyProperty.getType().getSimpleName()))),
                                () -> callbackFactory.buildPropertyField(valueProperty).orElseThrow(() -> new IllegalArgumentException(String.format("cannot build map value element field (type %s)", valueProperty.getType().getSimpleName())))
                        );
                    }
                }
        );
    }

    /**
     * Builds a {@link SimplePropertyComponentBuilder} and configures it for building most default components:<ul>
     *     <li>properties marked with {@link #SHOW_AS_COMPONENT_METADATA_PROPERTY} as requested</li>
     *     <li>boolean properties as {@link Checkbox}</li>
     *     <li>integer properties as {@link SuperIntegerField}</li>
     *     <li>long properties as {@link SuperLongField}</li>
     *     <li>double properties as {@link SuperDoubleField}</li>
     *     <li>{@link BigDecimal} properties as {@link SuperBigDecimalField}</li>
     *     <li>{@link LocalDate} properties as {@link SuperDatePicker}</li>
     *     <li>{@link LocalDateTime} properties as {@link SuperDateTimePicker}</li>
     *     <li>{@code String} properties as either {@link SuperTextField} or {@link SuperTextArea} (depending on {@link #MULTILINE_METADATA_PROPERTY})</li>
     *     <li>registered collection (lists and sets) properties as {@link CollectionField}</li>
     *     <li>map properties as {@link MapField}</li>
     *     <li>{@link ObjectField} using this factory as default (that falls back to {@link LabelField})</li>
     * </ul>
     * @return A {@link SimplePropertyComponentBuilder}.
     */
    @SuppressWarnings("unchecked")
    protected SimplePropertyComponentBuilder buildAndConfigureComponentBuilder() {
        final SimplePropertyComponentBuilder result = new SimplePropertyComponentBuilder()
                .withoutDefaultLabel()
                .withRegisteredBuilder(
                        // value of metadata must be a class that implements HasValue
                        property -> property.getMetadata().containsKey(SHOW_AS_COMPONENT_METADATA_PROPERTY) && property.getMetadata().get(SHOW_AS_COMPONENT_METADATA_PROPERTY).hasValueOfType(Class.class) && HasValue.class.isAssignableFrom((Class<?>) property.getMetadata().get(SHOW_AS_COMPONENT_METADATA_PROPERTY).getValue()),
                        property -> (HasValue<?, Object>) ReflectTools.newInstance((Class<?>) property.getMetadata().get(SHOW_AS_COMPONENT_METADATA_PROPERTY).getValue())
                )
                .withRegisteredBuilder(
                        // value of metadata must be a class that implements FieldBuilder
                        property -> property.getMetadata().containsKey(COMPONENT_BUILDER_METADATA_PROPERTY) && property.getMetadata().get(COMPONENT_BUILDER_METADATA_PROPERTY).hasValueOfType(Class.class) && FieldBuilder.class.isAssignableFrom((Class<?>) property.getMetadata().get(COMPONENT_BUILDER_METADATA_PROPERTY).getValue()),
                        // here is a builder that delegates to another builder
                        property -> ((FieldBuilder<Object>)ReflectTools.newInstance((Class<?>) property.getMetadata().get(COMPONENT_BUILDER_METADATA_PROPERTY).getValue())).buildPropertyField(property)
                )
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
                    // removing the seemingly unnecessary cast causes compilation errors
                    return (HasValue<?, Object>) (HasValue<?, ?>) buildCollectionField(listDef, (Class<? extends Collection<?>>) collectionType, result);
                })
                .withRegisteredBuilder(def -> def.getMetadata().containsKey(MAP_KEY_TYPE_METADATA_PROPERTY) && def.getMetadata().containsKey(MAP_VALUE_TYPE_METADATA_PROPERTY), def -> {
                    final Property<?, ?> keyDef = (Property<?, ?>) def.getMetadata().get(MAP_KEY_TYPE_METADATA_PROPERTY).getValue();
                    final Property<?, ?> valueDef = (Property<?, ?>) def.getMetadata().get(MAP_VALUE_TYPE_METADATA_PROPERTY).getValue();
                    return (HasValue<?, Object>) (HasValue<?, ?>) buildMapField(keyDef, valueDef, result);
                });
        result.setDefaultBuilder(def -> {
            try {
                final Object instance = this.getInstanceProvider(def.getType()).get();
                final ObjectField<Object> field = new ObjectField<>(def.getType(), () -> instance, this.getObjectFieldLayoutProvider());
                configureObjectField(field);
                field.repaint();
                return field;
            }
            catch(IllegalArgumentException iae) {
                LOGGER.info("could not construct an instance of {} due to {}; as a result LabelField is created instead of ObjectField", def.getType().getSimpleName(), iae.getMessage());
                return new LabelField<>();
            }
        });
        return result;
    }

    /**
     * Builds a {@link ReflectivePropertyProvider} and configures it to a typical use case based on annotations:<ul>
     *     <li>{@link FieldGroup} is mapped to {@link #GROUP_METADATA_PROPERTY}</li>
     *     <li>{@link FieldOrder} is mapped to {@link #ORDER_METADATA_PROPERTY}</li>
     *     <li>{@link BigField} is mapped to {@link #MULTILINE_METADATA_PROPERTY}</li>
     *     <li>{@link FieldCaption} is mapped to {@link #CAPTION_METADATA_PROPERTY}</li>
     *     <li>{@link ShowFieldAs} is mapped to {@link #SHOW_AS_COMPONENT_METADATA_PROPERTY}</li>
     *     <li>{@link BuildFieldWith} is mapped to {@link #COMPONENT_BUILDER_METADATA_PROPERTY}</li>
     * </ul>
     * In addition fields without a setter are marked with {@link #READ_ONLY_METADATA_PROPERTY}, and collections and maps using {@link #COLLECTION_ELEMENT_TYPE_METADATA_PROPERTY}, {@link #MAP_KEY_TYPE_METADATA_PROPERTY} and {@link #MAP_VALUE_TYPE_METADATA_PROPERTY}.
     * @return A {@link ReflectivePropertyProvider}.
     */
    protected ReflectivePropertyProvider buildAndConfigurePropertyProvider() {
        return new ReflectivePropertyProvider().withMetadataProvider(new AnnotationMetadataProvider()
                        .withRegisteredAnnotation(GROUP_METADATA_PROPERTY, FieldGroup.class, String.class, FieldGroup::value)
                        .withRegisteredAnnotation(ORDER_METADATA_PROPERTY, FieldOrder.class, int.class, FieldOrder::value)
                        .withRegisteredAnnotation(MULTILINE_METADATA_PROPERTY, BigField.class)
                        .withRegisteredAnnotation(CAPTION_METADATA_PROPERTY, FieldCaption.class, String.class, FieldCaption::value)
                        .withRegisteredAnnotation(SHOW_AS_COMPONENT_METADATA_PROPERTY, ShowFieldAs.class, Class.class, ShowFieldAs::value)
                        .withRegisteredAnnotation(COMPONENT_BUILDER_METADATA_PROPERTY, BuildFieldWith.class, Class.class, BuildFieldWith::value)
                ,
                // mark fields as read-only when there is no setter
                (name, field, setter, getter) -> setter == null ? Collections.singleton(new PropertyMetadata(READ_ONLY_METADATA_PROPERTY, boolean.class, true)) : Collections.emptySet(),
                // metadata for collections
                (name, field, setter, getter) -> {
                    if(Collection.class.isAssignableFrom(field.getType()))
                        return ReflectTools.extractGenericType(field, 0).map(type -> new PropertyMetadata(COLLECTION_ELEMENT_TYPE_METADATA_PROPERTY, Property.class, new Property<>(field.getDeclaringClass(), name, type, null, null))).map(Collections::singleton).orElse(Collections.emptySet());
                    else return Collections.emptySet();
                },
                // metadata for maps
                (name, field, setter, getter) -> {
                    if(Map.class.isAssignableFrom(field.getType())) {
                        final List<PropertyMetadata> metadata = new ArrayList<>();
                        ReflectTools.extractGenericType(field, 0).map(type -> new PropertyMetadata(MAP_KEY_TYPE_METADATA_PROPERTY, Property.class, new Property<>(field.getDeclaringClass(), name, type, null, null))).ifPresent(metadata::add);
                        ReflectTools.extractGenericType(field, 1).map(type -> new PropertyMetadata(MAP_VALUE_TYPE_METADATA_PROPERTY, Property.class, new Property<>(field.getDeclaringClass(), name, type, null, null))).ifPresent(metadata::add);
                        return metadata.size() == 2 ? metadata : Collections.emptySet();
                    }
                    else return Collections.emptySet();
                }
        );
    }

    /**
     * Builds a {@link PropertyGroupingProvider} based on presence of {@link #GROUP_METADATA_PROPERTY} and {@link #ORDER_METADATA_PROPERTY}.
     * @return A {@link MetadataBasedGroupingProvider}.
     */
    protected MetadataBasedGroupingProvider buildAndConfigureGroupingProvider() {
        return new MetadataBasedGroupingProvider()
                .withGroupingMetadataName(GROUP_METADATA_PROPERTY)
                .withSortingMetadataName(ORDER_METADATA_PROPERTY);
    }

    /**
     * Builds {@link ComponentConfigurator}s for a given data type:<ul>
     *     <li>components have their label set up according to {@link #CAPTION_METADATA_PROPERTY} or the field name</li>
     *     <li>components are set to read only based on {@link #READ_ONLY_METADATA_PROPERTY}</li>
     * </ul>
     *
     * @param dataType Type of object (the type of the {@link ObjectField} the returned configurators will be added to).
     * @return A non-{@code null}, but possibly empty collection.
     * @param <T> Type of object.
     */
    protected  <T> Collection<ComponentConfigurator<T>> buildComponentConfigurators(Class<T> dataType) {
        return Arrays.asList(
                (object, definition, component) -> {
                    final Map<String, PropertyMetadata> metadataMap = definition.getMetadata();
                    if(metadataMap.containsKey(CAPTION_METADATA_PROPERTY) && metadataMap.get(CAPTION_METADATA_PROPERTY).getValue() != null)
                        setLabel(component, metadataMap.get(CAPTION_METADATA_PROPERTY).getValue().toString());
                    else setLabel(component, StringTools.humanReadable(definition.getName()));
                },
                (object, definition, component) -> {
                    if(definition.getMetadata().containsKey(READ_ONLY_METADATA_PROPERTY) && definition.getMetadata().get(READ_ONLY_METADATA_PROPERTY).getValueType() == boolean.class)
                        component.setReadOnly((boolean) definition.getMetadata().get(READ_ONLY_METADATA_PROPERTY).getValue());
                }
        );
    }

    /**
     * Builds a {@link PropertyGroupLayoutProvider} that provides a separate layout for a group with two or more properties in it.
     * Each produced layout has style names added (from {@link #getGroupLayoutStyleNames()}), if possible.
     * @return A {@link PropertyGroupLayoutProvider}.
     */
    protected PropertyGroupLayoutProvider buildAndConfigureGroupLayoutProvider() {
        return new PropertyGroupLayoutProvider() {
            @Override
            public <T, C extends Component & HasComponents> Optional<C> buildGroupLayout(String groupName, List<Property<T, ?>> definitions) {
                if(definitions.size() < 2)
                    return Optional.empty();
                else {
                    final C layout = (C) getObjectFieldGroupLayoutProvider().get();
                    // apply style names if possible
                    if(layout instanceof HasStyle)
                        ((HasStyle) layout).addClassNames(sanitiseStyles(getGroupLayoutStyleNames(), groupName));
                    return Optional.of(layout);
                }
            }
        };
    }

    /**
     * Builds {@link ComponentGroupConfigurator}s:<ul>
     *     <li>each component in a named group has style names added to it (obtained from {@link #getGroupComponentStyleNames()}</li>
     * </ul>
     * @return A non-{@code null} collection of {@link ComponentGroupConfigurator}s.
     */
    protected Collection<ComponentGroupConfigurator> buildComponentGroupConfigurators() {
        return Collections.singleton(new ComponentGroupConfigurator() {
            @Override
            public <T> void configureComponentGroup(T object, String groupName, List<Property<T, ?>> definitions, List<? extends HasValue<?, ?>> components) {
                if(groupName != null)
                    components.stream()
                        .filter(HasStyle.class::isInstance)
                        .map(HasStyle.class::cast)
                        .forEach(component -> component.addClassNames(sanitiseStyles(getGroupComponentStyleNames(), groupName)));
            }
        });
    }

    /**
     * Builds and configures an {@link ObjectField} for a given type, using instance provider from {@link #getInstanceProvider(Class)}.
     * @param type Type to build an {@link ObjectField} for.
     * @return An {@link ObjectField}.
     * @param <T> Type of object to display.
     */
    public <T> ObjectField<T> buildAndConfigureObjectField(Class<T> type) {
        return this.buildAndConfigureObjectField(type, this.getInstanceProvider(type));
    }

    /**
     * Builds and configures an {@link ObjectField} for a given type using given instance provider.
     * @param type Type to build an {@link ObjectField} for.
     * @param newInstanceProvider A way to produce new instances of the object.
     * @return An {@link ObjectField}.
     * @param <T> Type of object to display.
     */
    public <T> ObjectField<T> buildAndConfigureObjectField(Class<T> type, SerializableSupplier<T> newInstanceProvider) {
        final ObjectField<T> objectField = new ObjectField<>(type, newInstanceProvider, this.getObjectFieldLayoutProvider());
        this.configureObjectField(objectField);
        return objectField;
    }

    /**
     * Configures a given object field. This will call the following methods, in the given order:<ol>
     *     <li>{@link ObjectField#withPropertyProvider(PropertyProvider)} using {@link #buildAndConfigurePropertyProvider()}</li>
     *     <li>{@link ObjectField#withPropertyGroupingProvider(PropertyGroupingProvider)} using {@link #buildAndConfigureGroupingProvider()}</li>
     *     <li>{@link ObjectField#withPropertyComponentBuilder(PropertyComponentBuilder)} using {@link #buildAndConfigureComponentBuilder()}</li>
     *     <li>{@link ObjectField#withGroupLayoutProvider(PropertyGroupLayoutProvider)} using {@link #buildAndConfigureGroupLayoutProvider()}</li>
     *     <li>{@link ObjectField#withComponentConfigurators(Collection)} using {@link #buildComponentConfigurators(Class)}</li>
     *     <li>{@link ObjectField#withComponentGroupConfigurators(Collection)} using {@link #buildComponentGroupConfigurators()}</li>
     * </ol>
     * @param objectField The passed parameter, but with the above-mentioned configuration applied.
     * @param <T> Type of object to display.
     */
    public final <T> ObjectField<T> configureObjectField(ObjectField<T> objectField) {
        return objectField.withPropertyProvider(this.buildAndConfigurePropertyProvider())
            .withPropertyGroupingProvider(this.buildAndConfigureGroupingProvider())
            .withPropertyComponentBuilder(this.buildAndConfigureComponentBuilder())
            .withGroupLayoutProvider(this.buildAndConfigureGroupLayoutProvider())
            .withComponentConfigurators(this.buildComponentConfigurators(objectField.getDataType()))
            .withComponentGroupConfigurators(this.buildComponentGroupConfigurators());
    }

    /**
     * Registers an instance provider for a given type. This speeds up creating new instances of the type in {@link ObjectField}s.
     * @param type Type to register instance provider for.
     * @param supplier A method called to create a new instance.
     * @param <T> Type.
     */
    public <T> void registerInstanceProvider(Class<T> type, SerializableSupplier<T> supplier) {
        this.instanceSuppliers.put(type, supplier);
    }

    /**
     * Returns the instance provider associated with the given type, if any, or a default one.
     * @param type Type to return the instance provider for.
     * @return A method that produces new instances of the given type.
     * @param <T> Type.
     */
    @SuppressWarnings("unchecked") // should be fine
    public <T> SerializableSupplier<T> getInstanceProvider(Class<T> type) {
        if(!this.instanceSuppliers.containsKey(type))
            return () -> ReflectTools.newInstance(type);
        else return (SerializableSupplier<T>) this.instanceSuppliers.get(type);

    }

    /**
     * Registers an empty collection provider for a given collection type.
     * @param collectionType Base collection type.
     * @param supplier Supplier for an empty collection.
     * @param <E> Element type. Ignored.
     * @param <C> Collection type. Ignored.
     */
    public <E, C extends Collection<E>> void registerEmptyCollectionProvider(Class<C> collectionType, SerializableSupplier<C> supplier) {
        this.emptyCollectionSuppliers.put(collectionType, supplier);
    }

    /**
     * Returns an empty collection provider for a given collection type.
     * @param type Type of collection.
     * @return A method to produce an empty collection. Used in {@link #buildCollectionField(Property, Class, PropertyComponentBuilder)}.
     * @param <E> Element type. Ignored.
     * @param <C> Collection type. Ignored.
     */
    @SuppressWarnings("unchecked")
    public <E, C extends Collection<E>> SerializableSupplier<C> getEmptyCollectionProvider(Class<C> type) {
        if(this.emptyCollectionSuppliers.containsKey(type))
            return (SerializableSupplier<C>) this.emptyCollectionSuppliers.get(type);
        else throw new IllegalArgumentException(String.format("unknown collection type %s", type.getName()));
    }

    /**
     * Returns a supplier for an empty map. Used in {@link #buildMapField(Property, Property, PropertyComponentBuilder)}.
     * @return A way to get an empty map.
     */
    public SerializableSupplier<Map<?, ?>> getEmptyMapSupplier() {
        return emptyMapSupplier;
    }

    /**
     * Sets a supplier for empty maps.
     * @param emptyMapSupplier A way to produce an empty map.
     */
    public void setEmptyMapSupplier(SerializableSupplier<Map<?, ?>> emptyMapSupplier) {
        this.emptyMapSupplier = emptyMapSupplier;
    }

    /**
     * Returns a {@link CollectionLayoutProvider} to be used in new {@link CollectionField}s.
     * @return A {@link CollectionLayoutProvider}. Used in {@link #buildCollectionField(Property, Class, PropertyComponentBuilder)}.
     */
    public CollectionLayoutProvider<?> getCollectionFieldLayoutProvider() {
        return collectionFieldLayoutProvider;
    }

    /**
     * Sets a new {@link CollectionLayoutProvider} to be used in new {@link CollectionField}s.
     * @param collectionFieldLayoutProvider A {@link CollectionLayoutProvider}.
     */
    public void setCollectionFieldLayoutProvider(CollectionLayoutProvider<?> collectionFieldLayoutProvider) {
        this.collectionFieldLayoutProvider = collectionFieldLayoutProvider;
    }

    /**
     * Returns a {@link CollectionLayoutProvider} for new {@link MapField}s.
     * @return A {@link CollectionLayoutProvider}. Used in {@link #buildMapField(Property, Property, PropertyComponentBuilder)}.
     */
    public CollectionLayoutProvider<?> getMapFieldLayoutProvider() {
        return mapFieldLayoutProvider;
    }

    /**
     * Sets a new {@link CollectionLayoutProvider} to be used in new {@link MapField}s.
     * @param mapFieldLayoutProvider A {@link CollectionLayoutProvider}.
     */
    public void setMapFieldLayoutProvider(CollectionLayoutProvider<?> mapFieldLayoutProvider) {
        this.mapFieldLayoutProvider = mapFieldLayoutProvider;
    }

    /**
     * Returns a way to obtain a layout for a {@link MapEntryField} used in {@link MapField}s.
     * @return A way to obtain a layout. Used in {@link #buildMapField(Property, Property, PropertyComponentBuilder)}.
     * @param <L> Type of layout.
     */
    @SuppressWarnings("unchecked")
    public <L extends Component & HasComponents> SerializableSupplier<L> getMapEntryFieldLayoutProvider() {
        return (SerializableSupplier<L>) mapEntryFieldLayoutProvider;
    }

    /**
     * Sets a new way to obtain layouts for {@link MapEntryField} in {@link MapField}.
     * @param mapEntryFieldLayoutProvider A new way to obtain layouts.
     * @param <L> Layout type.
     */
    public <L extends Component & HasComponents> void setMapEntryFieldLayoutProvider(SerializableSupplier<L> mapEntryFieldLayoutProvider) {
        this.mapEntryFieldLayoutProvider = mapEntryFieldLayoutProvider;
    }

    /**
     * Returns a way to obtain layouts for new {@link ObjectField}s.
     * @return A way to obtain a layout.
     * @param <L> Type of layout.
     */
    @SuppressWarnings("unchecked")
    public <L extends Component & HasComponents> SerializableSupplier<L> getObjectFieldLayoutProvider() {
        return (SerializableSupplier<L>) objectFieldLayoutProvider;
    }

    /**
     * Sets a new way to obtain layouts for new {@link ObjectField}s.
     * @param objectFieldLayoutProvider Layout provider.
     * @param <L> Layout type.
     */
    public <L extends Component & HasComponents> void setObjectFieldLayoutProvider(SerializableSupplier<L> objectFieldLayoutProvider) {
        this.objectFieldLayoutProvider = objectFieldLayoutProvider;
    }

    /**
     * Returns a way to obtain layouts for each group in an {@link ObjectField}. This is used in {@link #buildAndConfigureGroupLayoutProvider()}.
     * @return A layout provider.
     * @param <L> Layout type.
     */
    @SuppressWarnings("unchecked")
    public <L extends Component & HasComponents> SerializableSupplier<L> getObjectFieldGroupLayoutProvider() {
        return (SerializableSupplier<L>) objectFieldGroupLayoutProvider;
    }

    /**
     * Sets a new way to obtain layouts for each group in an {@link ObjectField}.
     * @param objectFieldGroupLayoutProvider A layout provider.
     * @param <L> Type of layout.
     */
    public <L extends Component & HasComponents> void setObjectFieldGroupLayoutProvider(SerializableSupplier<L> objectFieldGroupLayoutProvider) {
        this.objectFieldGroupLayoutProvider = objectFieldGroupLayoutProvider;
    }

    /**
     * Returns styles to be added to each group layout in an {@link ObjectField}. This is used in {@link #buildAndConfigureGroupLayoutProvider()}.
     * @return A non-{@code null} collection of styles.
     */
    public Collection<String> getGroupLayoutStyleNames() {
        return groupLayoutStyleNames;
    }

    /**
     * Sets new styles to be added to each group layout in an {@link ObjectField}. Each style name will have {@code .format(groupName)} applied to it.
     * @param groupLayoutStyleNames Style names.
     */
    public void setGroupLayoutStyleNames(Collection<String> groupLayoutStyleNames) {
        this.groupLayoutStyleNames = groupLayoutStyleNames;
    }

    /**
     * Returns styles to be added to each component in a group in {@link ObjectField}. This is used in {@link #buildComponentGroupConfigurators()}.
     * @return A non-{@code null} collection of styles.
     */
    public Collection<String> getGroupComponentStyleNames() {
        return groupComponentStyleNames;
    }

    /**
     * Sets new styles to be added to each component in a group in {@link ObjectField}. Each style name will have {@code .format(groupName)} applied to it.
     * @param groupComponentStyleNames Style names.
     */
    public void setGroupComponentStyleNames(Collection<String> groupComponentStyleNames) {
        this.groupComponentStyleNames = groupComponentStyleNames;
    }
}
