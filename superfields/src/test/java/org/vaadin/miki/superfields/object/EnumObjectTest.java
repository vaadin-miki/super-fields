package org.vaadin.miki.superfields.object;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.provider.Query;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.vaadin.miki.superfields.collections.CollectionField;
import org.vaadin.miki.superfields.util.factory.ObjectFieldFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EnumObjectTest {

    private static final ObjectFieldFactory FACTORY = new ObjectFieldFactory();

    @BeforeClass
    public static void setupFactory() {
        FACTORY.registerInstanceProvider(EnumObject.class, EnumObject::new);
    }

    private ObjectField<EnumObject> field;
    private int eventCounter = 0;

    @Before
    public void setup() {
        MockVaadin.setup();
        this.eventCounter = 0;
        this.field = FACTORY.buildAndConfigureObjectField(EnumObject.class, EnumObject::new);
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEnumFieldIsCombobox() {
        final Map<Property<EnumObject, ?>, HasValue<?, ?>> map = this.field.getPropertiesAndComponents();
        final HasValue<?, ?> comboBox = map.keySet().stream().filter(def -> "mode".equals(def.getName())).map(map::get).findFirst().orElse(null);
        Assert.assertNotNull(comboBox);
        Assert.assertTrue(String.format("field should be a combobox, not a %s", comboBox.getClass().getSimpleName()), comboBox instanceof ComboBox);
        Assert.assertArrayEquals(TestingMode.values(), ((ComboBox<?>) comboBox).getDataProvider().fetch(new Query<>()).toArray(TestingMode[]::new));
        this.field.addValueChangeListener(event -> eventCounter++);
        // set a value
        final EnumObject data = new EnumObject();
        data.setMode(TestingMode.MANUAL);
        this.field.setValue(data);
        Assert.assertEquals(1, this.eventCounter);
        Assert.assertEquals(TestingMode.MANUAL, comboBox.getValue());
        // select a value
        ((ComboBox<TestingMode>)comboBox).setValue(TestingMode.AUTOMATIC);
        Assert.assertEquals(2, this.eventCounter);
        Assert.assertEquals(TestingMode.AUTOMATIC, field.getValue().getMode());
    }

    @Test
    // note: for v23+ this test uses MultiSelectComboBox, not available in V14
    public void testEnumListIsMultiselectComboBox() {
        final Map<Property<EnumObject, ?>, HasValue<?, ?>> map = this.field.getPropertiesAndComponents();
        final HasValue<?, ?> collectionField = map.keySet().stream().filter(def -> "modes".equals(def.getName())).map(map::get).findFirst().orElse(null);
        Assert.assertNotNull(collectionField);
        Assert.assertTrue(String.format("field should be a collection field, not a %s", collectionField.getClass().getSimpleName()), collectionField instanceof CollectionField);
        this.field.addValueChangeListener(event -> eventCounter++);
        // set a value
        final EnumObject data = new EnumObject();
        final Set<TestingMode> testingModes = new HashSet<>(Arrays.asList(TestingMode.MANUAL, TestingMode.AUTOMATIC));
        data.setModes(testingModes);
        this.field.setValue(data);
        Assert.assertEquals(1, this.eventCounter);
        Assert.assertEquals(testingModes, collectionField.getValue());
        // select a value
        ((CollectionField<TestingMode, Set<TestingMode>>)collectionField).setValue(Collections.singleton(TestingMode.NONE));
        Assert.assertEquals(2, this.eventCounter);
        Assert.assertEquals(Collections.singleton(TestingMode.NONE), field.getValue().getModes());
    }

}
