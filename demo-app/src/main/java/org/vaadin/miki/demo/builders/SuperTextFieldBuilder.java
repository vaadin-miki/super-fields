package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.text.SuperTextField;

import java.util.function.Consumer;

/**
 * @author miki
 * @since 2023-06-14
 */
@Order(1)
@SuppressWarnings("squid:S5411") // no way around unboxing
public class SuperTextFieldBuilder implements ContentBuilder<SuperTextField> {
  @Override
  public void buildContent(SuperTextField component, Consumer<Component[]> callback) {
    final Checkbox prevent = new Checkbox("Set pattern to [a-z]* and prevent invalid input?", e -> {
      if(e.getValue()) {
        component.setPattern("^[a-z]*$");
        component.setPreventingInvalidInput(true);
      }
      else {
        component.setPattern(null);
        component.setPreventingInvalidInput(false);
      }
    });
    callback.accept(new Component[]{prevent});
  }
}
