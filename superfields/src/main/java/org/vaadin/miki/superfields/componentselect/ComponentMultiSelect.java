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
import java.util.stream.Collectors;

/**
 * A multi-select that displays each option as a {@link ClickNotifier}s and then selects/deselects it on click.
 *
 * @author miki
 * @since 2023-12-01
 */
@Tag("component-multi-select")
@JsModule("./component-multi-select.js")
public class ComponentMultiSelect<C extends Component & ClickNotifier<C>, T>
    extends AbstractComponentSelect<C, Set<T>, T, ComponentMultiSelect<C, T>>
  implements WithMaximumSelectionSizeMixin<ComponentMultiSelect<C, T>> {

  private int maxSelectionSize = UNLIMITED;
  private final Set<Integer> selection = new LinkedHashSet<>();

  /**
   * Creates the select with given options.
   *
   * @param layoutSupplier      Provides layout for the component.
   * @param componentFactory    A function that creates components for the {@code options}.
   * @param options             Items to select from.
   */
  @SafeVarargs
  public <L extends Component & HasComponents> ComponentMultiSelect(Supplier<L> layoutSupplier, SerializableBiFunction<Integer, T, C> componentFactory, T... options) {
    this(layoutSupplier, componentFactory, noOp(), noOp(), options);
  }

  /**
   * Creates the select with given options.
   *
   * @param layoutSupplier      Provides layout for the component.
   * @param componentFactory    A function that creates components for the {@code options}.
   * @param selectionModifier   Action to perform on a component when it is selected.
   * @param deselectionModifier Action to perform on a component when it is deselected.
   * @param options             Items to select from.
   */
  @SafeVarargs
  public <L extends Component & HasComponents> ComponentMultiSelect(Supplier<L> layoutSupplier, SerializableBiFunction<Integer, T, C> componentFactory, SerializableBiConsumer<Integer, C> selectionModifier, SerializableBiConsumer<Integer, C> deselectionModifier, T... options) {
    super(Collections.emptySet(), layoutSupplier, componentFactory, selectionModifier, deselectionModifier, options);
  }

  @Override
  protected boolean isSelected(int index) {
    return this.selection.contains(index);
  }

  @Override
  protected boolean itemClicked(int index) {
    final boolean isAlreadySelected = this.isSelected(index);
    // if clicked item is currently selected AND null value is allowed, just deselect
    if (isAlreadySelected) {
      this.deselect(index);
      this.selection.remove(index);
      return true;
    }
    // selection may have its cap, so make sure there is space for it
    else if(this.getMaximumSelectionSize() == UNLIMITED || this.selection.size() < this.getMaximumSelectionSize()) {
      this.select(index);
      this.selection.add(index);
      return true;
    }
    else return false;
  }

  @Override
  protected Set<T> generateModelValue() {
    return this.selection.stream().map(this.getOptions()::get).collect(Collectors.toSet());
  }

  @Override
  protected void setPresentationValue(Set<T> newPresentationValue) {
    if(newPresentationValue == null)
      newPresentationValue = Collections.emptySet();

    this.selection.clear();

    for(int zmp1=0; zmp1<this.getOptions().size(); zmp1++)
      if(newPresentationValue.contains(this.getOptions().get(zmp1)) && (this.getMaximumSelectionSize() == UNLIMITED || this.selection.size() < this.getMaximumSelectionSize())) {
        this.select(zmp1);
        this.selection.add(zmp1);
      }
    else this.deselect(zmp1);
    this.updateValue(); // wanted value might contain more elements than allowed, so update just in case
  }

  @Override
  @SuppressWarnings({"squid:S3655", "OptionalGetWithoutIsPresent"})
  public void setMaximumSelectionSize(int maximumSelectionSize) {
    this.maxSelectionSize = Math.max(UNLIMITED, maximumSelectionSize);
    if(this.maxSelectionSize != UNLIMITED) {
      while (this.maxSelectionSize < this.selection.size()) {
        // selection size is at least 2 when the loop starts, and will be at least 1 when it ends
        final int indexToDeselect = this.selection.stream().findAny().get();
        this.deselect(indexToDeselect);
        this.selection.remove(indexToDeselect);
      }
      this.updateValue();
    }
  }

  @Override
  public int getMaximumSelectionSize() {
    return this.maxSelectionSize;
  }
}
