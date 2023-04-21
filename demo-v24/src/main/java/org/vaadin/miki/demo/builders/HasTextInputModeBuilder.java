package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.markers.HasTextInputMode;
import org.vaadin.miki.shared.text.TextInputMode;

import java.util.function.Consumer;

/**
 * Builds content for things that implement {@link HasTextInputMode}
 * @author miki
 * @since 2023-04-21
 */
@Order(200)
public class HasTextInputModeBuilder implements ContentBuilder<HasTextInputMode> {
  @Override
  public void buildContent(HasTextInputMode component, Consumer<Component[]> callback) {
    final ComboBox<TextInputMode> modes = new ComboBox<>("Select text input mode:", TextInputMode.values());
    modes.setHelperText("The change should affect the on-screen keyboard of your device.");
    modes.addValueChangeListener(event -> component.setTextInputMode(event.getValue()));
    callback.accept(new Component[]{modes});
  }
}
