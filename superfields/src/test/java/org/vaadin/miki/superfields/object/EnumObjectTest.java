package org.vaadin.miki.superfields.object;

import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.data.provider.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.vaadin.miki.superfields.util.factory.ObjectFieldFactory;

import java.util.Map;
import java.util.Set;

public class EnumObjectTest {

    private BrowserlessUIContext window;

    private static final ObjectFieldFactory FACTORY = new ObjectFieldFactory();

    @BeforeAll
    public static void setupFactory() {
        FACTORY.registerInstanceProvider(EnumObject.class, EnumObject::new);
    }

    private ObjectField<EnumObject> field;
    private int eventCounter = 0;

    @BeforeEach
    public void setup() {
        this.eventCounter = 0;
        this.window = BrowserlessUIContext.forComponent(() -> {
            this.field = FACTORY.buildAndConfigureObjectField(EnumObject.class, EnumObject::new);
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
    @SuppressWarnings("unchecked")
    public void testEnumFieldIsCombobox() {
        final Map<Property<EnumObject, ?>, HasValue<?, ?>> map = this.field.getPropertiesAndComponents();
        final HasValue<?, ?> comboBox = map.keySet().stream().filter(def -> "mode".equals(def.getName())).map(map::get).findFirst().orElse(null);
        Assertions.assertNotNull(comboBox);
        Assertions.assertTrue(comboBox instanceof ComboBox, String.format("field should be a combobox, not a %s", comboBox.getClass().getSimpleName()));
        Assertions.assertArrayEquals(TestingMode.values(), ((ComboBox<?>) comboBox).getDataProvider().fetch(new Query<>()).toArray(TestingMode[]::new));
        this.field.addValueChangeListener(event -> eventCounter++);
        // set a value
        final EnumObject data = new EnumObject();
        data.setMode(TestingMode.MANUAL);
        this.field.setValue(data);
        Assertions.assertEquals(1, this.eventCounter);
        Assertions.assertEquals(TestingMode.MANUAL, comboBox.getValue());
        // select a value
        ((ComboBox<TestingMode>)comboBox).setValue(TestingMode.AUTOMATIC);
        Assertions.assertEquals(2, this.eventCounter);
        Assertions.assertEquals(TestingMode.AUTOMATIC, field.getValue().getMode());
    }

    @Test
    public void testEnumListIsMultiselectComboBox() {
        final Map<Property<EnumObject, ?>, HasValue<?, ?>> map = this.field.getPropertiesAndComponents();
        final HasValue<?, ?> comboBox = map.keySet().stream().filter(def -> "modes".equals(def.getName())).map(map::get).findFirst().orElse(null);
        Assertions.assertNotNull(comboBox);
        Assertions.assertTrue(comboBox instanceof MultiSelectComboBox, String.format("field should be a multi-select combobox, not a %s", comboBox.getClass().getSimpleName()));
        Assertions.assertArrayEquals(TestingMode.values(), ((MultiSelectComboBox<?>) comboBox).getDataProvider().fetch(new Query<>()).toArray(TestingMode[]::new));
        this.field.addValueChangeListener(event -> eventCounter++);
        // set a value
        final EnumObject data = new EnumObject();
        final Set<TestingMode> testingModes = Set.of(TestingMode.MANUAL, TestingMode.AUTOMATIC);
        data.setModes(testingModes);
        this.field.setValue(data);
        Assertions.assertEquals(1, this.eventCounter);
        Assertions.assertEquals(testingModes, comboBox.getValue());
        // select a value
        ((MultiSelectComboBox<TestingMode>)comboBox).setValue(TestingMode.NONE);
        Assertions.assertEquals(2, this.eventCounter);
        Assertions.assertEquals(Set.of(TestingMode.NONE), field.getValue().getModes());
    }

}
