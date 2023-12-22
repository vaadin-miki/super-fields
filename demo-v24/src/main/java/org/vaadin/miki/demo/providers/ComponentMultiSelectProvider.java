package org.vaadin.miki.demo.providers;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.componentselect.ComponentMultiSelect;
import org.vaadin.miki.superfields.componentselect.ComponentSelectHelpers;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Provides a {@link ComponentMultiSelect} of {@link Button}s and {@link String}s.
 *
 * @author miki
 * @since 2023-12-08
 */
@Order(92)
public class ComponentMultiSelectProvider implements ComponentProvider<ComponentMultiSelect<Button, String>>, Validator<Set<String>> {

  private static final Set<String> ANSWER = new LinkedHashSet<>(Arrays.asList("Athens", "Berlin", "Rome", "Tallinn", "Warsaw"));

  @Override
  public ComponentMultiSelect<Button, String> getComponent() {
    return new ComponentMultiSelect<Button, String>(
        FlexLayoutHelpers::row,
        ComponentSelectHelpers.simpleComponentFactory(Button::new),
        (index, button) -> button.addThemeVariants(ButtonVariant.LUMO_ERROR),
        (index, button) -> button.removeThemeVariants(ButtonVariant.LUMO_ERROR),
        "Athens", "Belgrade", "Berlin", "London", "Rome", "Tallinn", "Warsaw"
    )
        .withHelperText("(EU as of the end of 2023)")
        .withLabel("Select the capital cities of EU countries:")
        ;
  }

  @Override
  public ValidationResult apply(Set<String> strings, ValueContext valueContext) {
    return ANSWER.equals(strings) ? ValidationResult.ok() : ValidationResult.error("your answer is not correct!");
  }
}
