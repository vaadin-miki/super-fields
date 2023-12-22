package org.vaadin.miki.demo.providers;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.demo.data.Format;
import org.vaadin.miki.superfields.componentselect.ComponentSelect;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;

import java.util.Locale;

/**
 * Provides a {@link ComponentSelect} that uses themed buttons and {@link Format}.
 *
 * @author miki
 * @since 2023-11-17
 */
@Order(91)
public class ComponentSelectProvider implements ComponentProvider<ComponentSelect<Button, Format>> {
  @Override
  public ComponentSelect<Button, Format> getComponent() {
    return new ComponentSelect<>(FlexLayoutHelpers::row,
        (integer, format) -> new Button(String.format("%d. %s", integer + 1, format.name().toLowerCase(Locale.ROOT).replace('_', ' '))),
        Format.values()
    )
        .withHelperText("(click a button to select the corresponding option)")
        .withComponentSelectedAction((index, button) -> button.addThemeVariants(ButtonVariant.LUMO_PRIMARY))
        .withComponentDeselectedAction((index, button) -> button.removeThemeVariants(ButtonVariant.LUMO_PRIMARY))
        .withLabel("Select your favourite book format:");
  }
}
