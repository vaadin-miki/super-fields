package org.vaadin.miki.superfields.text;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LabelFieldTest {

    public static final String STRING_VALUE = "testuję sobie";

    private LabelField<String> field;

    @Before
    public void setup() {
        MockVaadin.setup();
        this.field = new LabelField<>();
    }

    @After
    public void teardown() {
        MockVaadin.tearDown();
    }

    @Test
    public void testChangingConverterChangesText() {
        this.field.setValue(STRING_VALUE);
        Assert.assertEquals(STRING_VALUE, this.field.getValue());
        Assert.assertEquals(STRING_VALUE, this.field.getText().getText());
        this.field.setConverter(String::toUpperCase);
        Assert.assertEquals(STRING_VALUE, this.field.getValue());
        Assert.assertEquals(STRING_VALUE.toUpperCase(), this.field.getText().getText());
    }

    @Test
    public void testChangingNullRepresentationWorks() {
        final String newNull = "(null)";
        Assert.assertNull(this.field.getValue());
        Assert.assertEquals(LabelField.DEFAULT_NULL_REPRESENTATION, this.field.getText().getText());
        this.field.setNullRepresentation(newNull);
        Assert.assertNull(this.field.getValue());
        Assert.assertEquals(newNull, this.field.getText().getText());
    }

}