package org.vaadin.miki.superfields.object;

import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

  private BrowserlessUIContext window;

  private static final ObjectFieldFactory FACTORY = new ObjectFieldFactory();

  private static String extractLabel(Object component) {
    if (component instanceof HasLabel)
      return ((HasLabel) component).getLabel();
    else return null;
  }

  private ObjectField<DataObject> field;

  @BeforeEach
  public void setup() {
    this.window = BrowserlessUIContext.forComponent(() -> {
      this.field = FACTORY.buildAndConfigureObjectField(DataObject.class, DataObject::new, false);
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
  public void testInitialisedWithAllProperties() {
    // before setting a value there should be nothing
    // note that this field is not repainted when built
    Assertions.assertTrue(this.field.getPropertiesAndComponents().isEmpty());
    Assertions.assertTrue(this.field.getGroupLayouts().isEmpty());
    Assertions.assertTrue(this.field.getComponentsNotInGroups().isEmpty());

    // force repaint
    this.field.repaint();

    final Map<Property<DataObject, ?>, HasValue<?, ?>> map = this.field.getPropertiesAndComponents();
    Assertions.assertEquals(8, map.size()); // 9 properties, but one is ignored, so 8
    // order is defined in annotations
    Assertions.assertArrayEquals(new String[]{"text", "description", "check", "currency", "timestamp", "date", "number", "fixed"}, map.keySet().stream().map(Property::getName).toArray(String[]::new));
    // grouping is also defined in annotations
    final Map<String, Component> groupLayouts = this.field.getGroupLayouts();
    Assertions.assertEquals(2, groupLayouts.size());
    Assertions.assertArrayEquals(new String[]{"currency-check", "random-group"}, groupLayouts.keySet().toArray(String[]::new));
    // four components belong to groups, so four should also be outside
    final Set<Component> notGrouped = this.field.getComponentsNotInGroups();
    Assertions.assertEquals(4, notGrouped.size());
    @SuppressWarnings("squid:S6204") // using .toList does not work in this case (double generics)
    final List<HasValue<?, ?>> hasValues = Stream.of("text", "description", "timestamp", "fixed")
        .map(name -> map.keySet().stream().filter(def -> Objects.equals(name, def.getName())).findFirst().orElseThrow())
        .map(map::get)
        .collect(Collectors.toList());
    Assertions.assertEquals(hasValues, new ArrayList<>(notGrouped));
  }

  @Test
  public void testFieldsCorrectlyMappedToComponents() {
    this.field.setValue(DataObject.build());

    final Map<Property<DataObject, ?>, HasValue<?, ?>> map = this.field.getPropertiesAndComponents();
    Assertions.assertFalse(map.isEmpty());
    map.forEach((def, component) -> {
      Assertions.assertTrue(DataObjectConfiguration.EXPECTED_FIELDS.get(def.getName()).isInstance(component), String.format("field %s should be type %s, is %s", def.getName(), DataObjectConfiguration.EXPECTED_FIELDS.get(def.getName()).getSimpleName(), component.getClass().getSimpleName()));
      Assertions.assertEquals(DataObjectConfiguration.EXPECTED_CAPTIONS.get(def.getName()), extractLabel(component), String.format("field %s has invalid caption", def.getName()));
    });

    // field for "fixed" must be read-only (there is no setter for the property
    Assertions.assertTrue(map.keySet().stream().filter(def -> "fixed".equals(def.getName())).findFirst().map(map::get).map(HasValue::isReadOnly).orElse(false));

    // also check if id and style names are ok
    Assertions.assertTrue(map.keySet().stream().filter(def -> "currency".equals(def.getName())).findFirst().map(map::get).map(HasStyle.class::cast).map(HasStyle::getClassNames).map(list -> list.contains("stylish")).orElse(false));
    Assertions.assertEquals("something", map.keySet().stream().filter(def -> "description".equals(def.getName())).findFirst().map(map::get).map(Component.class::cast).map(Component::getId).flatMap(perhaps -> perhaps).orElse(null));
    Assertions.assertTrue(map.keySet().stream().filter(def -> "timestamp".equals(def.getName())).findFirst().map(map::get).map(HasStyle.class::cast).map(HasStyle::getClassNames).map(Set::isEmpty).orElse(false));
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
    this.field.getPropertiesAndComponents().forEach((def, component) -> Assertions.assertEquals(def.getGetter().get().apply(value), component.getValue(), String.format("value of property %s differs", def.getName())));
  }

}