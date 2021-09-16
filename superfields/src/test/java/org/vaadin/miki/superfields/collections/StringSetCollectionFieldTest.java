package org.vaadin.miki.superfields.collections;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Basic tests where the collection field has a set.
 * @author miki
 * @since 2021-09-10
 */
public class StringSetCollectionFieldTest {

    private CollectionField<String, Set<String>> collectionField;
    private CollectionController controller;
    private int eventCounter = 0;

    @Before
    public void setup() {
        MockVaadin.setup();
        this.collectionField = new CollectionField<>(LinkedHashSet::new, (index, controller) -> {
            this.controller = controller;
            return new FlexLayout();
        },
                (CollectionValueComponentProvider<String, TextField>)(index, controller) -> new TextField("element at index "+index));
        this.collectionField.addValueChangeListener(event -> this.eventCounter++);
    }

    @Test
    public void testSimpleSet() {
        final Set<String> expected = new LinkedHashSet<>(Arrays.asList("this", "is", "test"));
        this.collectionField.setValue(expected);
        final Set<String> result = this.collectionField.getValue();
        Assert.assertEquals("after setting value, collection field should return equal list", expected, result);
        Assert.assertEquals("size of collection field must match collection size", expected.size(), this.collectionField.size());

        for(int zmp1 = 0; zmp1<expected.size(); zmp1++) {
            final HasValue<?, String> field = this.collectionField.getField(zmp1);
            Assert.assertTrue("field at index "+zmp1+" must be a text field", field instanceof TextField);
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
    public void testSetDoesNotHaveMoreElements() {
        final Set<String> expected = new LinkedHashSet<>(Arrays.asList("set", "has", "no", "duplicates"));
        this.collectionField.setValue(expected);

        // this is good
        this.controller.add();
        Assert.assertEquals(expected.size()+1, this.collectionField.size());

        // this now should not add an extra value, because empty field is already present and there are no duplicates in a set
        this.eventCounter = 0;
        this.controller.add();
        Assert.assertEquals(expected.size()+1, this.collectionField.size());

        // change the new field to something
        this.collectionField.getField(expected.size()).setValue("of course");
        Assert.assertEquals(1, this.eventCounter);

        // this value already exists, so it should disappear
        this.collectionField.getField(expected.size()).setValue("no");
        Assert.assertEquals(expected, this.collectionField.getValue());
        Assert.assertEquals(expected.size(), this.collectionField.size());

    }


}
