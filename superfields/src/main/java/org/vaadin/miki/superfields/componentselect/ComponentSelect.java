package org.vaadin.miki.superfields.componentselect;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableBiFunction;
import org.vaadin.miki.markers.WithNullValueOptionallyAllowedMixin;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A selection component that shows options as {@link ClickNotifier}s.
 *
 * @param <C> Type of the component.
 * @param <T> Type of value.
 *
 * @author miki
 * @since 2023-11-14
 */
@Tag("component-select")
@JsModule("./component-select.js")
public class ComponentSelect<C extends Component & ClickNotifier<C>, T>
    extends AbstractComponentSelect<C, T, T, ComponentSelect<C, T>>
    implements WithNullValueOptionallyAllowedMixin<ComponentSelect<C, T>, AbstractField.ComponentValueChangeEvent<CustomField<T>, T>, T> {

  private boolean nullValueAllowed = true;
  private int selected = NO_SELECTION;

  /**
   * Creates the select with given options, but without any action to perform on selection and deselection.
   * @param layoutSupplier Provides layout for the component.
   * @param componentFactory A function that creates components for the {@code options}.
   * @param options Items to select from.
   * @param <L> Layout type.
   */
  @SafeVarargs
  public <L extends Component & HasComponents> ComponentSelect(Supplier<L> layoutSupplier, SerializableBiFunction<Integer, T, C> componentFactory, T... options) {
    this(layoutSupplier, componentFactory, null, null, options);
  }

  /**
   * Creates the select with given options.
   * @param layoutSupplier Provides layout for the component.
   * @param componentFactory A function that creates components for the {@code options}.
   * @param selectionModifier Action to perform on a component when it is selected.
   * @param deselectionModifier Action to perform on a component when it is deselected.
   * @param options Items to select from.
   * @param <L> Layout type.
   */
  @SafeVarargs
  public <L extends Component & HasComponents> ComponentSelect(Supplier<L> layoutSupplier, SerializableBiFunction<Integer, T, C> componentFactory, SerializableBiConsumer<Integer, C> selectionModifier, SerializableBiConsumer<Integer, C> deselectionModifier, T... options) {
    super(null, layoutSupplier, componentFactory, selectionModifier, deselectionModifier, options);
  }

  private void deselectCurrent() {
    if(this.isSelected())
      this.deselect(this.getSelectedIndex());
  }

  /**
   * Checks if any component is selected.
   * @return Whether {@link #getSelectedIndex()} is different from {@link #NO_SELECTION}.
   */
  public boolean isSelected() {
    return this.getSelectedIndex() != NO_SELECTION;
  }

  @Override
  protected boolean isSelected(int index) {
    return this.getSelectedIndex() == index;
  }

  @Override
  protected boolean itemClicked(int index) {
    final boolean isAlreadySelected = this.isSelected(index);
    // if clicked item is currently selected AND null value is allowed, just deselect
    if (isAlreadySelected && this.isNullValueAllowed()) {
      this.deselect(index);
      this.selected = NO_SELECTION;
      return true;
    }
    // if the clicked item is not selected, deselect current selection and select the new thing instead
    else if (!isAlreadySelected) {
      this.deselectCurrent();
      this.select(index);
      this.selected = index;
      return true;
    }
    // otherwise (clicked selected item, but null value not allowed) - no changes
    else return false;
  }

  /**
   * Sets selection index.
   * Note that when null value is not allowed and passed index is {@link #NO_SELECTION}, nothing will happen.
   * @param index New index.
   */
  public void setSelectedIndex(int index) {
    this.ensureValidIndex(index);
    this.itemClicked(index > NO_SELECTION ? index : this.getSelectedIndex());
    this.updateValue();
  }

  /**
   * Returns the index of the currently selected component.
   * @return Index of the currently selected component. Can be {@link #NO_SELECTION}.
   */
  public int getSelectedIndex() {
    return this.selected;
  }

  @Override
  protected T generateModelValue() {
    return this.getSelectedIndex() == NO_SELECTION ? null : this.getOptions().get(this.getSelectedIndex());
  }

  @Override
  protected void setPresentationValue(T t) {
    int selection = NO_SELECTION;
    if(t != null)
      for (int zmp1 = 0; zmp1 < this.getOptions().size() && selection == NO_SELECTION; zmp1++)
        if (Objects.equals(t, this.getOptions().get(zmp1)))
          selection = zmp1;

    this.setSelectedIndex(selection);
  }

  @Override
  public boolean isNullValueAllowed() {
    return this.nullValueAllowed;
  }

  @Override
  public void setNullValueAllowed(boolean allowingNullValue) {
    this.nullValueAllowed = allowingNullValue;
  }

}
