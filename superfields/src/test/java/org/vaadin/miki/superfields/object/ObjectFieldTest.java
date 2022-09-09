package org.vaadin.miki.superfields.object;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValue;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.miki.superfields.util.factory.ObjectFieldFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private static final ObjectFieldFactory FACTORY = new ObjectFieldFactory();

    private static String extractLabel(Object component) {
        if(component instanceof HasLabel)
            return ((HasLabel) component).getLabel();
        else if(component instanceof org.vaadin.miki.markers.HasLabel)
            return ((org.vaadin.miki.markers.HasLabel) component).getLabel();
        else return null;
    }

    private ObjectField<DataObject> field;

    @Before
    public void setup() {
        MockVaadin.setup();
        this.field = FACTORY.buildAndConfigureObjectField(DataObject.class, DataObject::new, false);
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void testInitialisedWithAllProperties() {
        // before setting a value there should be nothing
        // note that this field is not repainted when built
        Assert.assertTrue(this.field.getPropertiesAndComponents().isEmpty());
        Assert.assertTrue(this.field.getGroupLayouts().isEmpty());
        Assert.assertTrue(this.field.getComponentsNotInGroups().isEmpty());

        // force repaint
        this.field.repaint();

        final Map<Property<DataObject, ?>, HasValue<?, ?>> map = this.field.getPropertiesAndComponents();
        Assert.assertEquals(8, map.size()); // 9 properties, but one is ignored, so 8
        // order is defined in annotations
        Assert.assertArrayEquals(new String[]{"text", "description", "check", "currency", "timestamp", "date", "number", "fixed"},
                map.keySet().stream().map(Property::getName).toArray(String[]::new));
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

        final Map<Property<DataObject,?>, HasValue<?,?>> map = this.field.getPropertiesAndComponents();
        Assert.assertFalse(map.isEmpty());
        map.forEach((def, component) -> {
            Assert.assertTrue(String.format("field %s should be type %s, is %s", def.getName(), DataObjectConfiguration.EXPECTED_FIELDS.get(def.getName()).getSimpleName(), component.getClass().getSimpleName()), DataObjectConfiguration.EXPECTED_FIELDS.get(def.getName()).isInstance(component));
            Assert.assertEquals(String.format("field %s has invalid caption", def.getName()), DataObjectConfiguration.EXPECTED_CAPTIONS.get(def.getName()), extractLabel(component));
        });

        // field for "fixed" must be read-only (there is no setter for the property
        Assert.assertTrue(map.keySet().stream().filter(def -> "fixed".equals(def.getName())).findFirst().map(map::get).map(HasValue::isReadOnly).orElse(false));

        // also check if id and style names are ok
        Assert.assertTrue(map.keySet().stream().filter(def -> "currency".equals(def.getName())).findFirst().map(map::get).map(HasStyle.class::cast).map(HasStyle::getClassNames).map(list -> list.contains("stylish")).orElse(false));
        Assert.assertEquals("something", map.keySet().stream().filter(def -> "description".equals(def.getName())).findFirst().map(map::get).map(Component.class::cast).map(Component::getId).flatMap(perhaps -> perhaps).orElse(null));
        Assert.assertTrue(map.keySet().stream().filter(def -> "timestamp".equals(def.getName())).findFirst().map(map::get).map(HasStyle.class::cast).map(HasStyle::getClassNames).map(Set::isEmpty).orElse(false));
    }

    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent") // getter is present
    public void testComponentsHaveCorrectValues() {
        final DataObject value = new DataObject();
        value.setText("hello, world");
        value.setHidden(123L);
        value.setCheck(true);
        value.setDescription("Jupiter Hell is a very good turn-based survival game - a rougealike.");
        value.setCurrency(BigDecimal.valueOf(1234));
        value.setDate(LocalDate.of(2021, 1, 12));
        value.setNumber(9931);
        value.setTimestamp(LocalDateTime.of(2012, 2, 17, 20, 45, 30));

        this.field.setValue(value);
        this.field.getPropertiesAndComponents().forEach((def, component) -> Assert.assertEquals(String.format("value of property %s differs", def.getName()), def.getGetter().get().apply(value), component.getValue()));
    }

}