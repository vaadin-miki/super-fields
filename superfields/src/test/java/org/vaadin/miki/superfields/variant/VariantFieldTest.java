package org.vaadin.miki.superfields.variant;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.Text;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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

    @Before
    public void setup() {
        MockVaadin.setup();
        this.eventCounter = 0;
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
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
        Assert.assertEquals(STRING_VALUE, field.getValue());
        Assert.assertTrue(field.getField() instanceof SuperTextField);
        final SuperTextField textField = (SuperTextField) field.getField();
        Assert.assertEquals(STRING_VALUE, textField.getValue());
        Assert.assertEquals(1, this.eventCounter);

        field.setValue(INT_VALUE);
        Assert.assertEquals(INT_VALUE, field.getValue());
        Assert.assertTrue(field.getField() instanceof SuperIntegerField);
        final SuperIntegerField integerField = (SuperIntegerField) field.getField();
        Assert.assertEquals(INT_VALUE, integerField.getValue());
        Assert.assertEquals(2, this.eventCounter);

        final Integer modified = -INT_VALUE*2;
        ((SuperIntegerField)field.getField()).setValue(modified);
        Assert.assertEquals(modified, field.getValue());
        Assert.assertEquals(3, this.eventCounter);
        Assert.assertSame(integerField, field.getField());

        field.setValue(STRING_VALUE);
        Assert.assertNotSame(textField, field.getField());
        Assert.assertEquals(STRING_VALUE, field.getValue());
        Assert.assertEquals(4, this.eventCounter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssigningValueOnFresh() {
        final VariantField field = new VariantField();
        field.setValue("this must fail, as no type is registered");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssigningUnknownValue() {
        final VariantField field = new VariantField().withTypedFieldProvider(TypedFieldProvider.of(String.class, SuperTextField::new));
        field.setValue(INT_VALUE);
    }

    @Test
    public void testClearSetsNullAndLabelFieldIsDefaultNullRepresentation() {
        final VariantField field = new VariantField().withTypedFieldProvider(TypedFieldProvider.of(String.class, SuperTextField::new));
        field.setValue(STRING_VALUE);
        field.clear();
        Assert.assertNull(field.getValue());
        Assert.assertTrue("field is of type "+field.getField().getClass().getSimpleName(), field.getField() instanceof LabelField);
        Assert.assertNull(((LabelField<?>) field.getField()).getValue());
    }

    @Test
    public void testSettingNullShouldAlwaysWork() {
        final VariantField field = new VariantField();
        Assert.assertNull(field.getValue());
        field.setValue(null);
    }

    @Test
    public void testSettingNullProviderChangesComponentIfValueNull() {
        final VariantField field = new VariantField().withNullComponentProvider(() -> new Text(STRING_VALUE));
        field.addValueChangeListener(event -> this.eventCounter++);
        Assert.assertTrue(field.getField() instanceof Text);
        Assert.assertEquals(STRING_VALUE, ((Text) field.getField()).getText());
        Assert.assertNull(field.getValue());
        Assert.assertEquals(0, this.eventCounter);
    }

}