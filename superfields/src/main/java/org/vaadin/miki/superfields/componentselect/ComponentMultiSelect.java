package org.vaadin.miki.superfields.componentselect;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableBiFunction;
import org.vaadin.miki.markers.WithMaximumSelectionSizeMixin;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author miki
 * @since 2023-12-01
 */
@Tag("component-multi-select")
@JsModule("./component-multi-select.js")
public class ComponentMultiSelect<C extends Component & ClickNotifier<C>, T>
    extends AbstractComponentSelect<C, Set<T>, ComponentMultiSelect<C, T>>
  implements WithMaximumSelectionSizeMixin<ComponentMultiSelect<C, T>> {

  private int maxSelectionSize = UNLIMITED;
  private final Set<Integer> selection = new LinkedHashSet<>();

  /**
   * Creates the select with given options.
   *
   * @param layoutSupplier      Provides layout for the component.
   * @param componentFactory    A function that creates components for the {@code options}.
   * @param selectionModifier   Action to perform on a component when it is selected.
   * @param deselectionModifier Action to perform on a component when it is deselected.
   * @param options             Items to select from.
   */
  public <L extends Component & HasComponents> ComponentMultiSelect(Supplier<L> layoutSupplier, SerializableBiFunction<Integer, Set<T>, C> componentFactory, SerializableBiConsumer<Integer, C> selectionModifier, SerializableBiConsumer<Integer, C> deselectionModifier, Set<T>... options) {
    super(layoutSupplier, componentFactory, selectionModifier, deselectionModifier, options);
  }

  public void selectAll() {

  }

  public void selectNone() {

  }

  @Override
  protected boolean isSelected(int index) {
    return false;
  }

  @Override
  protected boolean itemClicked(int index) {
    return false;
  }

  @Override
  protected Set<T> generateModelValue() {
    return Collections.emptySet();
  }

  @Override
  protected void setPresentationValue(Set<T> newPresentationValue) {

  }

  @Override
  public void setMaximumSelectionSize(int maximumSelectionSize) {

  }

  @Override
  public int getMaximumSelectionSize() {
    return this.maxSelectionSize;
  }
}
