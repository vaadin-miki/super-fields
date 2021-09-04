package org.vaadin.miki.superfields.collections;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Basic, simple unit tests for {@link CollectionField}.
 * @author miki
 * @since 2021-08-23
 */
public class StringCollectionFieldTest {

    private CollectionField<String, List<String>> collectionField;
    private CollectionController controller;
    private int eventCounter = 0;

    @Before
    public void setup() {
        MockVaadin.setup();
        this.collectionField = new CollectionField<>(ArrayList::new, controller -> {
           this.controller = controller;
           final FlexLayout result = new FlexLayout();
           result.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
           return result;
        }, (ValueComponentProvider<String, TextField>)(index, controller) -> new TextField("element at index "+index));
        this.collectionField.addValueChangeListener(event -> this.eventCounter++);
    }

    @Test
    public void testSimpleList() {
        final List<String> expected = new ArrayList<>(Arrays.asList("this", "is", "test"));
        this.collectionField.setValue(expected);
        final List<String> result = this.collectionField.getValue();
        Assert.assertEquals("after setting value, collection field should return equal list", expected, result);
        Assert.assertEquals("size of collection field must match collection size", expected.size(), this.collectionField.size());
        for(int zmp1 = 0; zmp1<expected.size(); zmp1++) {
            final HasValue<?, String> field = this.collectionField.getField(zmp1);
            Assert.assertTrue("field at index "+zmp1+" must be a text field", field instanceof TextField);
            Assert.assertEquals("label of field at index "+zmp1+" must match provided value", "element at index "+zmp1, ((TextField) field).getLabel());
            Assert.assertEquals("value of field at index "+zmp1+" must match collection's value", expected.get(zmp1), field.getValue());
        }
        this.eventCounter = 0;
        this.controller.add();
        final int newSize = expected.size()+1;
        Assert.assertEquals("new element should be added", newSize, this.collectionField.size());
        Assert.assertEquals(1, this.eventCounter);
        final String newValue = "hi there";
        expected.add(newValue);
        this.collectionField.getField(newSize - 1).setValue(newValue);
        result.clear();
        Assert.assertEquals("changing text field should trigger collection value event", 2, this.eventCounter);
        result.addAll(this.collectionField.getValue());
        Assert.assertEquals("after adding and setting, collection should be updated", expected, result);
    }

    @Test
    public void testRemoveAndAddElements() {
        final List<String> source = new ArrayList<>(Arrays.asList("test", "for", "removing", "elements"));
        this.collectionField.setValue(source);
        Assert.assertEquals(source.size(), this.collectionField.size());
        this.eventCounter = 0;
        this.controller.remove(1);
        source.remove(1);
        List<String> value = this.collectionField.getValue();
        Assert.assertEquals(source, value);
        Assert.assertEquals(1, this.eventCounter);
        this.controller.add(1);
        source.add(1, "");
        value = this.collectionField.getValue();
        Assert.assertEquals(source, value);
        Assert.assertEquals(2, this.eventCounter);
        this.collectionField.getField(1).setValue("adding");
        source.remove(1);
        source.add(1, "adding");
        Assert.assertEquals(source, this.collectionField.getValue());
        Assert.assertEquals(3, this.eventCounter);
    }

    @Test
    public void testEmptyCollectionOutOfTheBox() {
        Assert.assertTrue(this.collectionField.getValue().isEmpty());
        Assert.assertEquals(0, this.collectionField.size());
    }

    @Test
    public void testClearWorks() {
        final List<String> source = new ArrayList<>(Arrays.asList("this", "is", "a", "very", "long", "list"));
        this.collectionField.setValue(source);
        Assert.assertEquals(source.size(), this.collectionField.size());
        Assert.assertEquals(source, this.collectionField.getValue());
        Assert.assertEquals(1, this.eventCounter);
        this.controller.removeAll();
        Assert.assertEquals(2, this.eventCounter);
        Assert.assertTrue(this.collectionField.getValue().isEmpty());
        Assert.assertEquals(0, this.collectionField.size());
    }

    @Test
    public void testDisabling() {
        this.collectionField.setValue(Arrays.asList("hello", "world"));
        for(int zmp1 = 0; zmp1 < this.collectionField.size(); zmp1++)
            Assert.assertTrue(((TextField)this.collectionField.getField(zmp1)).isEnabled());
        // should be propagated to everywhere
        this.collectionField.setEnabled(false);
        for(int zmp1 = 0; zmp1 < this.collectionField.size(); zmp1++)
            Assert.assertFalse(((TextField)this.collectionField.getField(zmp1)).isEnabled());
    }

    @Test
    public void testReadOnly() {
        this.collectionField.setValue(Arrays.asList("hello", "world"));
        for(int zmp1 = 0; zmp1 < this.collectionField.size(); zmp1++)
            Assert.assertFalse(this.collectionField.getField(zmp1).isReadOnly());
        // should be propagated to everywhere
        this.collectionField.setReadOnly(true);
        for(int zmp1 = 0; zmp1 < this.collectionField.size(); zmp1++)
            Assert.assertTrue(this.collectionField.getField(zmp1).isReadOnly());
    }

}