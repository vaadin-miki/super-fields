package org.vaadin.miki.superfields.collections;

import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldTester;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vaadin.miki.markers.HasIndex;
import org.vaadin.miki.superfields.util.CollectionComponentProviders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Basic, simple unit tests for {@link CollectionField}.
 *
 * @author miki
 * @since 2021-08-23
 */
public class StringListCollectionFieldTest {

  private BrowserlessUIContext window;

  private CollectionField<String, List<String>> collectionField;
  private CollectionController controller;
  private int eventCounter = 0;

  @BeforeEach
  public void setup() {
    this.window = BrowserlessUIContext.forComponent(() -> {
      this.collectionField = new CollectionField<>(ArrayList::new, (index, ctrl) -> {
        this.controller = ctrl;
        final FlexLayout result = new FlexLayout();
        result.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        return result;
      }, (CollectionValueComponentProvider<String, TextField>) (index, ctrl) -> new TextField("element at index " + index));
      return this.collectionField;
    });
    this.collectionField.addValueChangeListener(event -> this.eventCounter++);
  }

  @AfterEach
  public void closeWindow() {
    if (this.window != null) {
      this.window.close();
    }
  }

  @Test
  public void testSimpleList() {
    final List<String> expected = new ArrayList<>(Arrays.asList("this", "is", "test"));
    this.collectionField.setValue(expected);
    final List<String> result = this.collectionField.getValue();
    Assertions.assertEquals(expected, result, "after setting value, collection field should return equal list");
    Assertions.assertEquals(expected.size(), this.collectionField.size(), "size of collection field must match collection size");
    for (int zmp1 = 0; zmp1 < expected.size(); zmp1++) {
      final HasValue<?, String> field = this.collectionField.getField(zmp1);
      Assertions.assertTrue(field instanceof TextField, "field at index " + zmp1 + " must be a text field");
      Assertions.assertEquals("element at index " + zmp1, ((TextField) field).getLabel(), "label of field at index " + zmp1 + " must match provided value");
      Assertions.assertEquals(expected.get(zmp1), field.getValue(), "value of field at index " + zmp1 + " must match collection's value");
    }
    this.eventCounter = 0;
    this.controller.add();
    final int newSize = expected.size() + 1;
    Assertions.assertEquals(newSize, this.collectionField.size(), "new element should be added");
    Assertions.assertEquals(1, this.eventCounter);
    final String newValue = "hi there";
    expected.add(newValue);
    this.collectionField.getField(newSize - 1).setValue(newValue);
    result.clear();
    Assertions.assertEquals(2, this.eventCounter, "changing text field should trigger collection value event");
    result.addAll(this.collectionField.getValue());
    Assertions.assertEquals(expected, result, "after adding and setting, collection should be updated");
  }

  @Test
  public void testRemoveAndAddElements() {
    final List<String> source = new ArrayList<>(Arrays.asList("test", "for", "removing", "elements"));
    this.collectionField.setValue(source);
    Assertions.assertEquals(source.size(), this.collectionField.size());
    this.eventCounter = 0;
    this.controller.remove(1);
    source.remove(1);
    List<String> value = this.collectionField.getValue();
    Assertions.assertEquals(source, value);
    Assertions.assertEquals(1, this.eventCounter);
    this.controller.add(1);
    source.add(1, "");
    value = this.collectionField.getValue();
    Assertions.assertEquals(source, value);
    Assertions.assertEquals(2, this.eventCounter);
    this.collectionField.getField(1).setValue("adding");
    source.remove(1);
    source.add(1, "adding");
    Assertions.assertEquals(source, this.collectionField.getValue());
    Assertions.assertEquals(3, this.eventCounter);
  }

  @Test
  public void testEmptyCollectionOutOfTheBox() {
    Assertions.assertTrue(this.collectionField.getValue().isEmpty());
    Assertions.assertEquals(0, this.collectionField.size());
  }

  @Test
  public void testClearWorks() {
    final List<String> source = new ArrayList<>(Arrays.asList("this", "is", "a", "very", "long", "list"));
    this.collectionField.setValue(source);
    Assertions.assertEquals(source.size(), this.collectionField.size());
    Assertions.assertEquals(source, this.collectionField.getValue());
    Assertions.assertEquals(1, this.eventCounter);
    this.controller.removeAll();
    Assertions.assertEquals(2, this.eventCounter);
    Assertions.assertTrue(this.collectionField.getValue().isEmpty());
    Assertions.assertEquals(0, this.collectionField.size());
  }

  @Test
  public void testDisabling() {
    this.collectionField.setValue(Arrays.asList("hello", "world"));
    for (int zmp1 = 0; zmp1 < this.collectionField.size(); zmp1++)
      Assertions.assertTrue(((TextField) this.collectionField.getField(zmp1)).isEnabled());
    // should be propagated to everywhere
    this.collectionField.setEnabled(false);
    for (int zmp1 = 0; zmp1 < this.collectionField.size(); zmp1++)
      Assertions.assertFalse(((TextField) this.collectionField.getField(zmp1)).isEnabled());
  }

  @Test
  public void testReadOnly() {
    this.collectionField.setValue(Arrays.asList("hello", "world"));
    for (int zmp1 = 0; zmp1 < this.collectionField.size(); zmp1++)
      Assertions.assertFalse(this.collectionField.getField(zmp1).isReadOnly());
    // should be propagated to everywhere
    this.collectionField.setReadOnly(true);
    for (int zmp1 = 0; zmp1 < this.collectionField.size(); zmp1++)
      Assertions.assertTrue(this.collectionField.getField(zmp1).isReadOnly());
    // the flag is only half the story - a user must actually be unable to change anything
    final TextFieldTester<TextField, String> firstField = this.window.test((TextField) this.collectionField.getField(0));
    Assertions.assertThrows(IllegalStateException.class, () -> firstField.setValue("nope"),
        "a user must not be able to type into a field of a read-only collection");
    Assertions.assertEquals(Arrays.asList("hello", "world"), this.collectionField.getValue());
  }

  @Test
  public void testReindexing() {
    // wrapper is needed, as it HasIndex
    this.collectionField.setCollectionValueComponentProvider(CollectionComponentProviders.rowWithRemoveButtonFirst(CollectionComponentProviders::textField, "remove"));
    this.collectionField.setValue(Arrays.asList("this", "is", "an", "elaborate", "test"));
    Assertions.assertEquals(5, this.collectionField.size());
    // each field needs to have the same index as its position
    for (int zmp1 = 0; zmp1 < this.collectionField.size(); zmp1++)
      Assertions.assertEquals(zmp1, ((HasIndex) this.collectionField.getField(zmp1)).getIndex());

    this.controller.add(2);
    // each field still needs to have the same index as its position
    for (int zmp1 = 0; zmp1 < this.collectionField.size(); zmp1++)
      Assertions.assertEquals(zmp1, ((HasIndex) this.collectionField.getField(zmp1)).getIndex());

    // this extra added field should be the default value of it
    Assertions.assertEquals(this.collectionField.getField(2).getEmptyValue(), this.collectionField.getField(2).getValue());
  }

  @Test
  public void testChangingRenderNoValueChangeTriggered() {
    this.collectionField.setValue(Arrays.asList("hello", "world"));
    this.eventCounter = 0;
    // changing renderer should not trigger value change - none needed
    this.collectionField.setCollectionValueComponentProvider(CollectionComponentProviders.rowWithRemoveButtonFirst(CollectionComponentProviders::textField, "remove"));
    Assertions.assertEquals(0, this.eventCounter);

    // now this should not trigger a value change
    this.controller.add();
    Assertions.assertEquals(1, this.eventCounter);
  }

}