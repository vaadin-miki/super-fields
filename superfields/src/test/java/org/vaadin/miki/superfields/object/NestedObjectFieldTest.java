package org.vaadin.miki.superfields.object;

import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NestedObjectFieldTest {

  private BrowserlessUIContext window;

  private static final ObjectFieldFactory FACTORY = new ObjectFieldFactory();
  private static final Logger LOGGER = LoggerFactory.getLogger(NestedObjectFieldTest.class);

  @BeforeAll
  public static void setupFactory() {
    FACTORY.registerInstanceProvider(DataObject.class, DataObject::new);
  }

  private ObjectField<NestedObject> field;
  private int eventCounter = 0;

  @BeforeEach
  public void setup() {
    this.eventCounter = 0;
    this.window = BrowserlessUIContext.forComponent(() -> {
      this.field = FACTORY.buildAndConfigureObjectField(NestedObject.class, NestedObject::new);
      return this.field;
    });
  }

  @AfterEach
  public void closeWindow() {
    if (this.window != null) {
      this.window.close();
    }
  }

  @Test
  public void testInitialisedProperly() {
    // this field is repainted
    Assertions.assertFalse(this.field.getPropertiesAndComponents().isEmpty());
    Assertions.assertFalse(this.field.getGroupLayouts().isEmpty());
    Assertions.assertFalse(this.field.getComponentsNotInGroups().isEmpty());
  }

  @Test
  public void testCollectionFieldRenderedProperly() {
    final NestedObject nestedObject = new NestedObject();
    final List<String> stringList = Arrays.asList("trolling", "is", "a", "art");
    nestedObject.setTexts(stringList);

    this.field.setValue(nestedObject);
    final Map<Property<NestedObject, ?>, HasValue<?, ?>> map = this.field.getPropertiesAndComponents();
    final HasValue<?, ?> collectionField = map.keySet().stream().filter(def -> "texts".equals(def.getName())).map(map::get).findFirst().orElse(null);
    Assertions.assertTrue(collectionField instanceof CollectionField);
    Assertions.assertEquals(stringList, collectionField.getValue());
    final List<Component> listComponents = ((CollectionField<?, ?>) collectionField).getChildren().findFirst().orElseGet(FlexLayoutHelpers::column).getChildren().toList();
    Assertions.assertEquals(stringList.size(), listComponents.size());
    for (int zmp1 = 0; zmp1 < stringList.size(); zmp1++)
      Assertions.assertTrue(listComponents.get(zmp1) instanceof SuperTextField && Objects.equals(stringList.get(zmp1), ((SuperTextField) listComponents.get(zmp1)).getValue()));
  }

  @Test
  public void testDataObjectFieldIsObjectField() {
    final DataObject dataObject = DataObject.build();
    final NestedObject nestedObject = new NestedObject();
    nestedObject.setDataObject(dataObject);

    this.field.setValue(nestedObject);

    final Map<Property<NestedObject, ?>, HasValue<?, ?>> map = this.field.getPropertiesAndComponents();
    final HasValue<?, ?> objectField = map.keySet().stream().filter(def -> "dataObject".equals(def.getName())).map(map::get).findFirst().orElse(null);

    Assertions.assertNotNull(objectField);
    Assertions.assertTrue(objectField instanceof ObjectField, "ObjectField should be returned, not " + objectField.getClass().getSimpleName());
    Assertions.assertEquals(DataObject.class, ((ObjectField<?>) objectField).getDataType());
    Assertions.assertEquals(dataObject, objectField.getValue());
    // all properties from data object should be present
    Assertions.assertEquals(DataObjectConfiguration.EXPECTED_FIELDS.size(), ((ObjectField<?>) objectField).getPropertiesAndComponents().size());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testObjectsFieldIsCollectionField() {
    final DataObject dataObject = DataObject.build();
    final NestedObject nestedObject = new NestedObject();
    nestedObject.setObjects(List.of(dataObject));

    this.field.setValue(nestedObject);

    final Map<Property<NestedObject, ?>, HasValue<?, ?>> map = this.field.getPropertiesAndComponents();
    final HasValue<?, ?> collectionField = map.keySet().stream().filter(def -> "objects".equals(def.getName())).map(map::get).findFirst().orElse(null);

    Assertions.assertNotNull(collectionField);
    Assertions.assertTrue(collectionField instanceof CollectionField, "CollectionField should be returned, not " + collectionField.getClass().getSimpleName());
    Assertions.assertEquals(nestedObject.getObjects(), collectionField.getValue());
    Assertions.assertEquals(1, ((CollectionController) collectionField).size());

    LOGGER.info("about to add a new element to the list");

    this.field.addValueChangeListener(event -> eventCounter++);
    // now also ensure events are fired properly when things are added
    ((CollectionField<?, ?>) collectionField).add();
    Assertions.assertEquals(1, eventCounter);
    // make sure the thing is really added
    Assertions.assertEquals(2, this.field.getValue().getObjects().size());

    // and when things are modified
    // component structure: (ObjectField of NestedObject -> layout -> ) CollectionField of DataObjects -> layout -> ObjectField of DataObject -> layout -> individual components
    final ObjectField<DataObject> objectField = (ObjectField<DataObject>) ((CollectionField<?, ?>) collectionField).getChildren().findFirst().map(layout -> layout.getChildren().toArray()[1]).orElseThrow(IllegalStateException::new);
    final Map<Property<DataObject, ?>, HasValue<?, ?>> nestedMap = objectField.getPropertiesAndComponents();
    final HasValue<?, String> nestedTextField = (HasValue<?, String>) nestedMap.keySet().stream().filter(def -> Objects.equals("text", def.getName())).map(nestedMap::get).findFirst().orElse(null);
    Assertions.assertNotNull(nestedTextField);
    // that field should be empty
    Assertions.assertEquals("", nestedTextField.getValue());
    // change its value
    final String elaborateValue = "oh hello, I just modified the text!";
    LOGGER.info("about to modify text");
    nestedTextField.setValue(elaborateValue);

    Assertions.assertEquals(2, eventCounter);
    Assertions.assertEquals(elaborateValue, this.field.getValue().getObjects().get(1).getText());
  }

  @Test
  public void testMapFieldIsCorrect() {
    final Map<String, DataObject> dataObjectMap = Stream.of("hello", "world").collect(Collectors.toMap(Function.identity(), s -> DataObject.build()));
    final NestedObject nestedObject = new NestedObject();
    nestedObject.setObjectMap(dataObjectMap);

    this.field.setValue(nestedObject);

    final Map<Property<NestedObject, ?>, HasValue<?, ?>> map = this.field.getPropertiesAndComponents();
    final HasValue<?, ?> mapField = map.keySet().stream().filter(def -> "objectMap".equals(def.getName())).map(map::get).findFirst().orElse(null);

    Assertions.assertNotNull(mapField);
    Assertions.assertTrue(mapField instanceof MapField, "MapField should be returned, not " + mapField.getClass().getSimpleName());
    Assertions.assertEquals(dataObjectMap, mapField.getValue());
  }

  @Test
  public void testFieldsRenderedAsExplicitlyRequested() {
    final NestedObject nestedObject = new NestedObject();
    nestedObject.setNumber(123);
    nestedObject.setText("hello, world");

    this.field.setValue(nestedObject);

    final Map<Property<NestedObject, ?>, HasValue<?, ?>> map = this.field.getPropertiesAndComponents();
    final HasValue<?, ?> numberField = map.keySet().stream().filter(def -> "number".equals(def.getName())).map(map::get).findFirst().orElse(null);

    Assertions.assertNotNull(numberField);
    Assertions.assertTrue(numberField instanceof LabelField, "LabelField should be returned, not " + numberField.getClass().getSimpleName());
    Assertions.assertEquals(nestedObject.getNumber(), numberField.getValue());

    final HasValue<?, ?> textField = map.keySet().stream().filter(def -> "text".equals(def.getName())).map(map::get).findFirst().orElse(null);

    Assertions.assertNotNull(textField);
    Assertions.assertSame(TextField.class, textField.getClass(), "TextField should be returned, not " + textField.getClass().getSimpleName());
    Assertions.assertEquals(nestedObject.getText(), textField.getValue());
    Assertions.assertEquals(TextFieldBuilder.TITLE_TEXT, ((TextField) textField).getTitle());

    this.field.addValueChangeListener(event -> eventCounter++);
    ((TextField) textField).setValue("New value!");
    Assertions.assertEquals(1, eventCounter);
    final NestedObject newValue = this.field.getValue();
    Assertions.assertNotEquals(nestedObject, newValue);
    Assertions.assertEquals(textField.getValue(), newValue.getText());
  }

}
