package org.vaadin.miki.superfields.object;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.miki.superfields.collections.CollectionController;
import org.vaadin.miki.superfields.collections.CollectionField;
import org.vaadin.miki.superfields.collections.MapField;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;
import org.vaadin.miki.superfields.text.LabelField;
import org.vaadin.miki.superfields.text.SuperTextField;
import org.vaadin.miki.superfields.util.factory.ObjectFieldFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NestedObjectFieldTest {

    private static final ObjectFieldFactory FACTORY = new ObjectFieldFactory();
    private static final Logger LOGGER = LoggerFactory.getLogger(NestedObjectFieldTest.class);

    @BeforeClass
    public static void setupFactory() {
        FACTORY.registerInstanceProvider(DataObject.class, DataObject::new);
    }

    private ObjectField<NestedObject> field;
    private int eventCounter = 0;

    @Before
    public void setup() {
        MockVaadin.setup();
        this.eventCounter = 0;
        this.field = FACTORY.buildAndConfigureObjectField(NestedObject.class, NestedObject::new);
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void testInitialisedProperly() {
        // this field is repainted
        Assert.assertFalse(this.field.getPropertiesAndComponents().isEmpty());
        Assert.assertFalse(this.field.getGroupLayouts().isEmpty());
        Assert.assertFalse(this.field.getComponentsNotInGroups().isEmpty());
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
    @SuppressWarnings("unchecked")
    public void testObjectsFieldIsCollectionField() {
        final DataObject dataObject = DataObject.build();
        final NestedObject nestedObject = new NestedObject();
        nestedObject.setObjects(Collections.singletonList(dataObject));

        this.field.setValue(nestedObject);

        final Map<Property<NestedObject, ?>, HasValue<?, ?>> map = this.field.getPropertiesAndComponents();
        final HasValue<?, ?> collectionField = map.keySet().stream().filter(def -> "objects".equals(def.getName())).map(map::get).findFirst().orElse(null);

        Assert.assertNotNull(collectionField);
        Assert.assertTrue("CollectionField should be returned, not "+collectionField.getClass().getSimpleName(), collectionField instanceof CollectionField);
        Assert.assertEquals(nestedObject.getObjects(), collectionField.getValue());
        Assert.assertEquals(1, ((CollectionController)collectionField).size());

        LOGGER.info("about to add a new element to the list");

        this.field.addValueChangeListener(event -> eventCounter++);
        // now also ensure events are fired properly when things are added
        ((CollectionField<?, ?>) collectionField).add();
        Assert.assertEquals(1, eventCounter);
        // make sure the thing is really added
        Assert.assertEquals(2, this.field.getValue().getObjects().size());

        // and when things are modified
        // component structure: (ObjectField of NestedObject -> layout -> ) CollectionField of DataObjects -> layout -> ObjectField of DataObject -> layout -> individual components
        final ObjectField<DataObject> objectField = (ObjectField<DataObject>) ((CollectionField<?, ?>) collectionField).getChildren().findFirst().map(layout -> layout.getChildren().toArray()[1]).orElseThrow(IllegalStateException::new);
        final Map<Property<DataObject, ?>, HasValue<?, ?>> nestedMap = objectField.getPropertiesAndComponents();
        final HasValue<?, String> nestedTextField = (HasValue<?, String>) nestedMap.keySet().stream().filter(def -> Objects.equals("text", def.getName())).map(nestedMap::get).findFirst().orElse(null);
        Assert.assertNotNull(nestedTextField);
        // that field should be empty
        Assert.assertEquals("", nestedTextField.getValue());
        // change its value
        final String elaborateValue = "oh hello, I just modified the text!";
        LOGGER.info("about to modify text");
        nestedTextField.setValue(elaborateValue);

        Assert.assertEquals(2, eventCounter);
        Assert.assertEquals(elaborateValue, this.field.getValue().getObjects().get(1).getText());
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

    @Test
    public void testFieldsRenderedAsExplicitlyRequested() {
        final NestedObject nestedObject = new NestedObject();
        nestedObject.setNumber(123);
        nestedObject.setText("hello, world");

        this.field.setValue(nestedObject);

        final Map<Property<NestedObject, ?>, HasValue<?, ?>> map = this.field.getPropertiesAndComponents();
        final HasValue<?, ?> numberField = map.keySet().stream().filter(def -> "number".equals(def.getName())).map(map::get).findFirst().orElse(null);

        Assert.assertNotNull(numberField);
        Assert.assertTrue("LabelField should be returned, not "+numberField.getClass().getSimpleName(), numberField instanceof LabelField);
        Assert.assertEquals(nestedObject.getNumber(), numberField.getValue());

        final HasValue<?, ?> textField = map.keySet().stream().filter(def -> "text".equals(def.getName())).map(map::get).findFirst().orElse(null);

        Assert.assertNotNull(textField);
        Assert.assertSame("TextField should be returned, not "+textField.getClass().getSimpleName(), TextField.class, textField.getClass());
        Assert.assertEquals(nestedObject.getText(), textField.getValue());
        Assert.assertEquals(TextFieldBuilder.TITLE_TEXT, ((TextField)textField).getTitle());

        this.field.addValueChangeListener(event -> eventCounter++);
        ((TextField)textField).setValue("New value!");
        Assert.assertEquals(1, eventCounter);
        final NestedObject newValue = this.field.getValue();
        Assert.assertNotEquals(nestedObject, newValue);
        Assert.assertEquals(textField.getValue(), newValue.getText());
    }

}
