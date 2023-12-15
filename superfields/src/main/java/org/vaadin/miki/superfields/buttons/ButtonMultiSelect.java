package org.vaadin.miki.superfields.buttons;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableBiFunction;
import org.vaadin.miki.superfields.componentselect.ComponentMultiSelect;
import org.vaadin.miki.superfields.componentselect.ComponentSelectHelpers;

import java.util.function.Supplier;

/**
 * The simples possible extension of {@link ComponentMultiSelect} that uses {@link Button}s.
 *
 * @author miki
 * @since 2023-12-08
 */
@Tag("button-multi-select")
@JsModule("./button-multi-select.js")
@SuppressWarnings("squid:S110") // more than 5 superclasses, but that is ok
public class ButtonMultiSelect<T> extends ComponentMultiSelect<Button, T> {

  /**
   * Creates a {@link ButtonMultiSelect} that uses style names to visually distinguish the selected button.
   * @param layoutProvider Provides the root layout of the component.
   * @param selectedClassName Style name used when a button is selected.
   * @param deselectedClassName Style name used when a button is deselected.
   * @param items Items.
   * @param <L> Layout type.
   */
  @SafeVarargs
  public <L extends Component & HasComponents> ButtonMultiSelect(Supplier<L> layoutProvider, String selectedClassName, String deselectedClassName, T... items) {
    this(layoutProvider, ComponentSelectHelpers.simpleComponentFactory(Button::new),
        ComponentSelectHelpers.changeStyle(deselectedClassName, selectedClassName),
        ComponentSelectHelpers.changeStyle(selectedClassName, deselectedClassName),
        items);
  }

  /**
   * Creates a {@link ButtonMultiSelect} that uses {@link ButtonVariant} to visually distinguish the selected button.
   * @param layoutProvider Provides the root layout of the component.
   * @param selectedVariant Variant to use for the selected button. The lack of this variant indicates a non-selected button.
   * @param items Items.
   * @param <L> Layout type.
   */
  @SafeVarargs
  public <L extends Component & HasComponents> ButtonMultiSelect(Supplier<L> layoutProvider, ButtonVariant selectedVariant, T... items) {
    this(layoutProvider, ComponentSelectHelpers.simpleComponentFactory(Button::new, Object::toString),
        ComponentSelectHelpers.addVariant(selectedVariant),
        ComponentSelectHelpers.removeVariant(selectedVariant),
        items);
  }

  /**
   * Creates a {@link ButtonMultiSelect}.
   * @param layoutSupplier Provides the root layout of the component.
   * @param componentFactory A factory to create {@link Button}s for each option.
   * @param selectionModifier Action to perform when a button is selected.
   * @param deselectionModifier Action to perform when a button is deselected.
   * @param options Items.
   * @param <L> Layout type.
   */
  @SafeVarargs
  public <L extends Component & HasComponents> ButtonMultiSelect(Supplier<L> layoutSupplier, SerializableBiFunction<Integer, T, Button> componentFactory, SerializableBiConsumer<Integer, Button> selectionModifier, SerializableBiConsumer<Integer, Button> deselectionModifier, T... options) {
    super(layoutSupplier, componentFactory, selectionModifier, deselectionModifier, options);
  }

}
