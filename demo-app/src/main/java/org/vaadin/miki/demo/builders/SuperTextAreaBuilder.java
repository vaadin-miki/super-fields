package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.text.SuperTextArea;

import java.util.function.Consumer;

/**
 * Content specific for {@link SuperTextArea}.
 *
 * @author miki
 * @since 2026-05-15
 */
@Order(1)
public class SuperTextAreaBuilder implements ContentBuilder<SuperTextArea> {
  @Override
  public void buildContent(SuperTextArea component, Consumer<Component[]> callback) {
    final Checkbox checkbox = new Checkbox("Set size full.", event -> {
      if (Boolean.TRUE.equals(event.getValue()))
        component.setSizeFull();
      else component.setSizeUndefined();
    });
    callback.accept(new Component[]{checkbox});
  }
}
