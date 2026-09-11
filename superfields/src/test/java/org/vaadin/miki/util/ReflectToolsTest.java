package org.vaadin.miki.util;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vaadin.miki.superfields.object.DataObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("squid:S1068")
public class ReflectToolsTest {

  public static final class TestDateTimePicker extends DateTimePicker {

  }

  private static void checkType(String field, int index, Class<?> expected) throws NoSuchFieldException {
    final Optional<Class<?>> perhaps = ReflectTools.extractGenericType(ReflectToolsTest.class.getDeclaredField(field), index);
    Assertions.assertTrue(perhaps.isPresent());
    Assertions.assertSame(expected, perhaps.get());
  }

  private static void checkNoType(String field, int index) throws NoSuchFieldException {
    final Optional<Class<?>> perhaps = ReflectTools.extractGenericType(ReflectToolsTest.class.getDeclaredField(field), index);
    Assertions.assertTrue(perhaps.isEmpty());
  }

  // these fields are used for reflection tests, do not remove them
  private final List<String> stringList = new ArrayList<>();
  private final Set<DataObject> dataObjects = new LinkedHashSet<>();
  private final Map<String, DataObject> map = new HashMap<>();
  private final int number = 0;

  @Test
  public void datePickerAvailableForSuperDateTimePicker() {
    final Optional<DatePicker> perhapsPicker = ReflectTools.getValueOfField(new TestDateTimePicker(), DatePicker.class, "datePicker");
    Assertions.assertTrue(perhapsPicker.isPresent());
    Assertions.assertNotNull(perhapsPicker.get());
  }

  @Test
  public void findGenericAttributeOfField() throws NoSuchFieldException {
    checkType("stringList", 0, String.class);
    checkNoType("stringList", 2);
    checkType("dataObjects", 0, DataObject.class);
    checkNoType("dataObjects", 1);
    checkNoType("dataObjects", -1);

    checkType("map", 0, String.class);
    checkType("map", 1, DataObject.class);
    checkNoType("map", 2);

    checkNoType("number", 0);
    checkNoType("number", -2);

  }

}