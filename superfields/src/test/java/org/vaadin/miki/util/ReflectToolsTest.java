package org.vaadin.miki.util;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class ReflectToolsTest {

    public static final class TestDateTimePicker extends DateTimePicker {

    }

    @Before
    public void setUp() {
        MockVaadin.setup();
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void datePickerAvailableForSuperDateTimePicker() {
        final Optional<DatePicker> perhapsPicker = ReflectTools.getValueOfField(new TestDateTimePicker(), DatePicker.class, "datePicker");
        Assert.assertTrue(perhapsPicker.isPresent());
        Assert.assertNotNull(perhapsPicker.get());
    }

}