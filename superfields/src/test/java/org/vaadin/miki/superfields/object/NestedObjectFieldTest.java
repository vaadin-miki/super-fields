package org.vaadin.miki.superfields.object;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.miki.superfields.collections.CollectionField;
import org.vaadin.miki.superfields.collections.MapField;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;
import org.vaadin.miki.superfields.text.SuperTextField;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NestedObjectFieldTest {

    private ObjectField<NestedObject> field;

    @Before
    public void setup() {
        MockVaadin.setup();

        this.field = new ObjectField<>(NestedObject.class, NestedObject::new);
        ObjectFieldConfigurator.INSTANCE.configureObjectField(this.field);
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void testCollectionFieldRenderedProperly() {
        final NestedObject nestedObject = new NestedObject();
        final List<String> stringList = Arrays.asList("trolling", "is", "a", "art");
        nestedObject.setTexts(stringList);

        this.field.setValue(nestedObject);
        final Map<Property<NestedObject, ?>, HasValue<?, ?>> map = this.field.getPropertiesAndComponents();
        final HasValue<?, ?> collectionField = map.keySet().stream().filter(def -> "texts".equals(def.getName())).map(map::get).findFirst().orElse(null);
        Assert.assertTrue(collectionField instanceof CollectionField);
        Assert.assertEquals(stringList, collectionField.getValue());
        final List<Component> listComponents = ((CollectionField<?, ?>) collectionField).getChildren().findFirst().orElseGet(FlexLayoutHelpers::column).getChildren().collect(Collectors.toList());
        Assert.assertEquals(stringList.size(), listComponents.size());
        for(int zmp1 = 0; zmp1<stringList.size(); zmp1++)
            Assert.assertTrue(listComponents.get(zmp1) instanceof SuperTextField && Objects.equals(stringList.get(zmp1), ((SuperTextField) listComponents.get(zmp1)).getValue()));
    }

    @Test
    public void testDataObjectFieldIsObjectField() {
        final DataObject dataObject = DataObject.build();
        final NestedObject nestedObject = new NestedObject();
        nestedObject.setDataObject(dataObject);

        this.field.setValue(nestedObject);

        final Map<Property<NestedObject, ?>, HasValue<?, ?>> map = this.field.getPropertiesAndComponents();
        final HasValue<?, ?> objectField = map.keySet().stream().filter(def -> "dataObject".equals(def.getName())).map(map::get).findFirst().orElse(null);

        Assert.assertNotNull(objectField);
        Assert.assertTrue("ObjectField should be returned, not "+objectField.getClass().getSimpleName(), objectField instanceof ObjectField);
        Assert.assertEquals(DataObject.class, ((ObjectField<?>) objectField).getDataType());
        Assert.assertEquals(dataObject, objectField.getValue());
    }

    @Test
    public void testMapFieldIsCorrect() {
        final Map<String, DataObject> dataObjectMap = Stream.of("hello", "world").collect(Collectors.toMap(Function.identity(), s -> DataObject.build()));
        final NestedObject nestedObject = new NestedObject();
        nestedObject.setObjectMap(dataObjectMap);

        this.field.setValue(nestedObject);

        final Map<Property<NestedObject, ?>, HasValue<?, ?>> map = this.field.getPropertiesAndComponents();
        final HasValue<?, ?> mapField = map.keySet().stream().filter(def -> "objectMap".equals(def.getName())).map(map::get).findFirst().orElse(null);

        Assert.assertNotNull(mapField);
        Assert.assertTrue("MapField should be returned, not "+mapField.getClass().getSimpleName(), mapField instanceof MapField);
        Assert.assertEquals(dataObjectMap, mapField.getValue());
    }

}
