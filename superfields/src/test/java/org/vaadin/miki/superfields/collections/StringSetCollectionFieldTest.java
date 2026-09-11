package org.vaadin.miki.superfields.collections;

import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Basic tests where the collection field has a set.
 * @author miki
 * @since 2021-09-10
 */
public class StringSetCollectionFieldTest {

    private BrowserlessUIContext window;

    private CollectionField<String, Set<String>> collectionField;
    private CollectionController controller;
    private int eventCounter = 0;

    @BeforeEach
    public void setup() {
        this.window = BrowserlessUIContext.forComponent(() -> {
            this.collectionField = new CollectionField<>(LinkedHashSet::new, (index, controller) -> {
                this.controller = controller;
                return new FlexLayout();
            },
                    (CollectionValueComponentProvider<String, TextField>)(index, controller) -> new TextField("element at index "+index));
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
    public void testSimpleSet() {
        final Set<String> expected = new LinkedHashSet<>(Arrays.asList("this", "is", "test"));
        this.collectionField.setValue(expected);
        final Set<String> result = this.collectionField.getValue();
        Assertions.assertEquals(expected, result, "after setting value, collection field should return equal list");
        Assertions.assertEquals(expected.size(), this.collectionField.size(), "size of collection field must match collection size");

        for(int zmp1 = 0; zmp1<expected.size(); zmp1++) {
            final HasValue<?, String> field = this.collectionField.getField(zmp1);
            Assertions.assertTrue(field instanceof TextField, "field at index "+zmp1+" must be a text field");
        }
        this.eventCounter = 0;
        this.controller.add();
        final int newSize = expected.size()+1;
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
    public void testSetDoesNotHaveMoreElements() {
        final Set<String> expected = new LinkedHashSet<>(Arrays.asList("set", "has", "no", "duplicates"));
        this.collectionField.setValue(expected);

        // this is good
        this.controller.add();
        Assertions.assertEquals(expected.size()+1, this.collectionField.size());

        // this now should not add an extra value, because empty field is already present and there are no duplicates in a set
        this.eventCounter = 0;
        this.controller.add();
        Assertions.assertEquals(expected.size()+1, this.collectionField.size());

        // change the new field to something
        this.collectionField.getField(expected.size()).setValue("of course");
        Assertions.assertEquals(1, this.eventCounter);

        // this value already exists, so it should disappear
        this.collectionField.getField(expected.size()).setValue("no");
        Assertions.assertEquals(expected, this.collectionField.getValue());
        Assertions.assertEquals(expected.size(), this.collectionField.size());

    }


}
