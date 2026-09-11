package org.vaadin.miki.superfields.layouts;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vaadin.miki.superfields.numbers.SuperIntegerField;

import java.util.Arrays;
import java.util.stream.Stream;

// tests for #403
public class HeaderFooterLayoutWrapperTest {

    @Test
    public void addingComponentToReadOnlyMakesReadOnly() {
        final HeaderFooterLayoutWrapper<FlexLayout, FlexLayout, FlexLayout, FlexLayout> layout = FlexLayoutHelpers.columnWithHeaderRowAndFooterRow();
        final Button button = new Button("this should be disabled");
        final TextArea area = new TextArea("this should be readonly, but enabled");
        layout.add(button, area);
        layout.setReadOnly(true);
        Assertions.assertFalse(button.isEnabled());
        Assertions.assertTrue(area.isEnabled());
        Assertions.assertTrue(area.isReadOnly());
        // adding more things
        final Button newButton = new Button("this should be turned into disabled");
        final TextField textField = new TextField("this should be turned into readonly");
        layout.add(newButton, textField);
        Assertions.assertFalse(button.isEnabled());
        Assertions.assertTrue(textField.isReadOnly());
        Assertions.assertTrue(textField.isEnabled());
        // now a similar trick, but with a layout
        final FlexLayout nested = FlexLayoutHelpers.column();
        final Button another = new Button("yet another");
        final SuperIntegerField integerField = new SuperIntegerField();
        nested.add(another, integerField);
        layout.add(nested);
        Assertions.assertTrue(nested.isEnabled());
        Assertions.assertFalse(another.isEnabled());
        Assertions.assertTrue(integerField.isEnabled());
        Assertions.assertTrue(integerField.isReadOnly());
        // now switch off read-only
        layout.setReadOnly(false);
        Stream.of(button, newButton, another, nested).forEach(b -> Assertions.assertTrue(b.isEnabled()));
        for(HasValue<?, ?> f: Arrays.asList(area, textField, integerField))
            Assertions.assertFalse(f.isReadOnly());
    }

    @Test
    public void addingReadOnlyMakesNoDifference() {
        final HeaderFooterLayoutWrapper<FlexLayout, FlexLayout, FlexLayout, FlexLayout> layout = FlexLayoutHelpers.columnWithHeaderRowAndFooterRow();
        final Button button = new Button("this should be disabled");
        button.setEnabled(false);
        final TextArea area = new TextArea("this should be readonly, but enabled");
        area.setReadOnly(true);
        layout.add(button, area);
        Assertions.assertFalse(layout.isReadOnly());
        Assertions.assertFalse(button.isEnabled());
        Assertions.assertTrue(area.isReadOnly());
    }

}