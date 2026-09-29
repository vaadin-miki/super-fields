package org.vaadin.miki.superfields.text;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.vaadin.browserless.BrowserlessUIContext;

public class LabelFieldTest {

    private BrowserlessUIContext window;

    public static final String STRING_VALUE = "testuję sobie";

    private LabelField<String> field;

    @BeforeEach
    public void setup() {
        this.window = BrowserlessUIContext.forComponent(() -> {
            this.field = new LabelField<>();
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
    public void testChangingConverterChangesText() {
        this.field.setValue(STRING_VALUE);
        Assertions.assertEquals(STRING_VALUE, this.field.getValue());
        Assertions.assertEquals(STRING_VALUE, this.field.getText().getText());
        this.field.setConverter(String::toUpperCase);
        Assertions.assertEquals(STRING_VALUE, this.field.getValue());
        Assertions.assertEquals(STRING_VALUE.toUpperCase(), this.field.getText().getText());
    }

    @Test
    public void testChangingNullRepresentationWorks() {
        final String newNull = "(null)";
        Assertions.assertNull(this.field.getValue());
        Assertions.assertEquals(LabelField.DEFAULT_NULL_REPRESENTATION, this.field.getText().getText());
        this.field.setNullRepresentation(newNull);
        Assertions.assertNull(this.field.getValue());
        Assertions.assertEquals(newNull, this.field.getText().getText());
    }

}