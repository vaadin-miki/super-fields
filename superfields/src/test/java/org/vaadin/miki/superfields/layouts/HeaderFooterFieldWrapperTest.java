package org.vaadin.miki.superfields.layouts;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vaadin.miki.superfields.text.SuperTextField;

public class HeaderFooterFieldWrapperTest {

  @Test
  public void testDefaultValueIsFromComponent() {
    final SuperTextField textField = new SuperTextField();

    final HeaderFooterFieldWrapper<String, FlexLayout, FlexLayout> wrapper = new HeaderFooterFieldWrapper<>(
        FlexLayout::new, new FlexLayout(), textField, new FlexLayout()
    );

    Assertions.assertEquals("", textField.getValue());
    Assertions.assertEquals("", wrapper.getValue());
  }

}