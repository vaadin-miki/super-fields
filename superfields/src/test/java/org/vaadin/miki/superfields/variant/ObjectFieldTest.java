package org.vaadin.miki.superfields.variant;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasValue;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;
import org.vaadin.miki.superfields.variant.reflect.AnnotationMetadataProvider;
import org.vaadin.miki.superfields.variant.reflect.ReflectiveDefinitionProvider;
import org.vaadin.miki.superfields.variant.util.MetadataBasedGroupingProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author miki
 * @since 2022-06-23
 */
public class ObjectFieldTest {

    private static String extractLabel(Object component) {
        if(component instanceof HasLabel)
            return ((HasLabel) component).getLabel();
        else if(component instanceof org.vaadin.miki.markers.HasLabel)
            return ((org.vaadin.miki.markers.HasLabel) component).getLabel();
        else return null;
    }

    private static void setLabel(Object component, String label) {
        if(component instanceof HasLabel)
            ((HasLabel) component).setLabel(label);
        else if(component instanceof org.vaadin.miki.markers.HasLabel)
            ((org.vaadin.miki.markers.HasLabel) component).setLabel(label);
    }

    private ObjectField<DataObject> field;

    @Before
    public void setup() {
        MockVaadin.setup();
        this.field = new ObjectField<>(DataObject.class, DataObject::new, FlexLayoutHelpers::column);
        this.field.setDefinitionProvider(new ReflectiveDefinitionProvider().withMetadataProvider(new AnnotationMetadataProvider()
                .withRegisteredAnnotation(DataObjectConfiguration.GROUP_METADATA_PROPERTY, FieldGroup.class, String.class, FieldGroup::value)
                .withRegisteredAnnotation(DataObjectConfiguration.ORDER_METADATA_PROPERTY, FieldOrder.class, int.class, FieldOrder::value)
                .withRegisteredAnnotation(DataObjectConfiguration.MULTILINE_METADATA_PROPERTY, BigField.class)
                .withRegisteredAnnotation(DataObjectConfiguration.CAPTION_METADATA_PROPERTY, FieldCaption.class, String.class, FieldCaption::value)
        ));
        this.field.setDefinitionGroupingProvider(new MetadataBasedGroupingProvider().withGroupingMetadataName(DataObjectConfiguration.GROUP_METADATA_PROPERTY).withSortingMetadataName(DataObjectConfiguration.ORDER_METADATA_PROPERTY));
        this.field.setObjectPropertyComponentFactory(DataObjectConfiguration.SUPERFIELDS_DEFAULT_FACTORY);
        this.field.addComponentConfigurator((object, definition, component) -> {
            final Map<String, ObjectPropertyMetadata> metadataMap = definition.getMetadata();
            if(metadataMap.containsKey(DataObjectConfiguration.CAPTION_METADATA_PROPERTY) && metadataMap.get(DataObjectConfiguration.CAPTION_METADATA_PROPERTY).getValue() != null)
                setLabel(component, metadataMap.get(DataObjectConfiguration.CAPTION_METADATA_PROPERTY).getValue().toString());
        });

    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void testInitialisedWithAllProperties() {
        // before setting a value there should be nothing
        Assert.assertTrue(this.field.getPropertiesAndComponents().isEmpty());
        Assert.assertTrue(this.field.getGroupLayouts().isEmpty());
        Assert.assertTrue(this.field.getComponentsNotInGroups().isEmpty());

        // force repaint
        this.field.repaint();

        final Map<ObjectPropertyDefinition<DataObject, ?>, HasValue<?, ?>> map = this.field.getPropertiesAndComponents();
        Assert.assertEquals(8, map.size()); // 9 properties, but one is ignored, so 8
        // order is defined in annotations
        Assert.assertArrayEquals(new String[]{"text", "description", "check", "currency", "timestamp", "date", "number", "fixed"},
                map.keySet().stream().map(ObjectPropertyDefinition::getName).toArray(String[]::new));
        // grouping is also defined in annotations
        final Map<String, Component> groupLayouts = this.field.getGroupLayouts();
        Assert.assertEquals(2, groupLayouts.size());
        Assert.assertArrayEquals(new String[]{"currency-check", "random-group"}, groupLayouts.keySet().toArray(String[]::new));
        // four components belong to groups, so four should also be outside
        final Set<Component> notGrouped = this.field.getComponentsNotInGroups();
        Assert.assertEquals(4, notGrouped.size());
        final List<HasValue<?, ?>> hasValues = Stream.of("text", "description", "timestamp", "fixed")
                .map(name -> map.keySet().stream().filter(def -> Objects.equals(name, def.getName())).findFirst().orElseThrow())
                .map(map::get)
                .collect(Collectors.toList());
        Assert.assertEquals(hasValues, new ArrayList<>(notGrouped));
    }

    @Test
    public void testFieldsCorrectlyMappedToComponents() {
        this.field.setValue(DataObject.build());

        final Map<ObjectPropertyDefinition<DataObject,?>, HasValue<?,?>> map = this.field.getPropertiesAndComponents();
        Assert.assertFalse(map.isEmpty());
        map.forEach((def, component) -> {
            Assert.assertTrue(String.format("field %s should be type %s, is %s", def.getName(), DataObjectConfiguration.EXPECTED_FIELDS.get(def.getName()).getSimpleName(), component.getClass().getSimpleName()), DataObjectConfiguration.EXPECTED_FIELDS.get(def.getName()).isInstance(component));
            Assert.assertEquals(String.format("field %s has invalid caption", def.getName()), DataObjectConfiguration.EXPECTED_CAPTIONS.get(def.getName()), extractLabel(component));
        });
    }

}