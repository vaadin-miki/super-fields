package org.vaadin.miki.superfields.variant;

import com.vaadin.flow.component.Text;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vaadin.miki.superfields.numbers.SuperIntegerField;
import org.vaadin.miki.superfields.text.LabelField;
import org.vaadin.miki.superfields.text.SuperTextField;

/**
 * @author miki
 * @since 2022-04-11
 */
public class VariantFieldTest {

    public static final String STRING_VALUE = "this must work";
    public static final Integer INT_VALUE = -42;

    private int eventCounter = 0;

    @BeforeEach
    public void setup() {
        this.eventCounter = 0;
    }

    @Test
    public void testSettingValueOfRecognisedTypeWorks() {
        final VariantField field = new VariantField()
                .withTypedFieldProvider(
                        TypedFieldProvider.of(String.class, SuperTextField::new),
                        TypedFieldProvider.of(Integer.class, SuperIntegerField::new)
                );
        field.addValueChangeListener(event -> this.eventCounter++);

        field.setValue(STRING_VALUE);
        Assertions.assertEquals(STRING_VALUE, field.getValue());
        Assertions.assertTrue(field.getField() instanceof SuperTextField);
        final SuperTextField textField = (SuperTextField) field.getField();
        Assertions.assertEquals(STRING_VALUE, textField.getValue());
        Assertions.assertEquals(1, this.eventCounter);

        field.setValue(INT_VALUE);
        Assertions.assertEquals(INT_VALUE, field.getValue());
        Assertions.assertTrue(field.getField() instanceof SuperIntegerField);
        final SuperIntegerField integerField = (SuperIntegerField) field.getField();
        Assertions.assertEquals(INT_VALUE, integerField.getValue());
        Assertions.assertEquals(2, this.eventCounter);

        final Integer modified = -INT_VALUE*2;
        ((SuperIntegerField)field.getField()).setValue(modified);
        Assertions.assertEquals(modified, field.getValue());
        Assertions.assertEquals(3, this.eventCounter);
        Assertions.assertSame(integerField, field.getField());

        field.setValue(STRING_VALUE);
        Assertions.assertNotSame(textField, field.getField());
        Assertions.assertEquals(STRING_VALUE, field.getValue());
        Assertions.assertEquals(4, this.eventCounter);
    }

    @Test
    public void testAssigningValueOnFresh() {
        final VariantField field = new VariantField();
        Assertions.assertThrows(IllegalArgumentException.class, () -> field.setValue("this must fail, as no type is registered"));
    }

    @Test
    public void testAssigningUnknownValue() {
        final VariantField field = new VariantField().withTypedFieldProvider(TypedFieldProvider.of(String.class, SuperTextField::new));
        Assertions.assertThrows(IllegalArgumentException.class, () -> field.setValue(INT_VALUE));
    }

    @Test
    public void testClearSetsNullAndLabelFieldIsDefaultNullRepresentation() {
        final VariantField field = new VariantField().withTypedFieldProvider(TypedFieldProvider.of(String.class, SuperTextField::new));
        field.setValue(STRING_VALUE);
        field.clear();
        Assertions.assertNull(field.getValue());
        Assertions.assertTrue(field.getField() instanceof LabelField, "field is of type "+field.getField().getClass().getSimpleName());
        Assertions.assertNull(((LabelField<?>) field.getField()).getValue());
    }

    @Test
    public void testSettingNullShouldAlwaysWork() {
        final VariantField field = new VariantField();
        Assertions.assertNull(field.getValue());
        field.setValue(null);
    }

    @Test
    public void testSettingNullProviderChangesComponentIfValueNull() {
        final VariantField field = new VariantField()
                .withNullComponentProvider(() -> new Text(STRING_VALUE));
        field.addValueChangeListener(event -> this.eventCounter++);
        Assertions.assertTrue(field.getField() instanceof Text);
        Assertions.assertEquals(STRING_VALUE, ((Text) field.getField()).getText());
        Assertions.assertNull(field.getValue());
        Assertions.assertEquals(0, this.eventCounter);
    }

    @Test
    public void testReadOnlyPropagates() {
        final VariantField field = new VariantField().withTypedFieldProvider(TypedFieldProvider.of(Integer.class, SuperIntegerField::new));
        field.setValue(INT_VALUE);
        field.setReadOnly(true);
        Assertions.assertTrue(field.isReadOnly());
        Assertions.assertTrue(((SuperIntegerField)field.getField()).isReadOnly());
    }

}