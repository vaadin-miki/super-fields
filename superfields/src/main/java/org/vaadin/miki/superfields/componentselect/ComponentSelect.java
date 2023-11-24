package org.vaadin.miki.superfields.componentselect;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableBiFunction;
import org.vaadin.miki.markers.WithHelperMixin;
import org.vaadin.miki.markers.WithHelperPositionableMixin;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithItemsMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithLabelPositionableMixin;
import org.vaadin.miki.markers.WithNullValueOptionallyAllowedMixin;
import org.vaadin.miki.markers.WithValueMixin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
public class ComponentSelect<C extends Component & ClickNotifier<C>, T> extends CustomField<T>
    implements WithItemsMixin<T, ComponentSelect<C, T>>, HasStyle, WithIdMixin<ComponentSelect<C, T>>,
    WithLabelMixin<ComponentSelect<C, T>>, WithLabelPositionableMixin<ComponentSelect<C, T>>,
    WithHelperMixin<ComponentSelect<C, T>>, WithHelperPositionableMixin<ComponentSelect<C, T>>,
    WithValueMixin<AbstractField.ComponentValueChangeEvent<CustomField<T>, T>, T, ComponentSelect<C, T>>,
    WithNullValueOptionallyAllowedMixin<ComponentSelect<C, T>, AbstractField.ComponentValueChangeEvent<CustomField<T>, T>, T> {
  public static final int NO_SELECTION = -1;

  private static <X, Y> SerializableBiConsumer<X, Y> noOp() {return (x, y) -> {};}

  private final List<T> options = new ArrayList<>();
  private final List<C> components = new ArrayList<>();
  private final HasComponents layout;

  private SerializableBiFunction<Integer, T, C> factory;
  private SerializableBiConsumer<Integer, C> whenSelected = noOp();
  private SerializableBiConsumer<Integer, C> whenDeselected = noOp();
  private int selected = NO_SELECTION;
  private boolean nullValueAllowed = true;

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
    this.layout = layoutSupplier.get();
    this.setComponentFactory(componentFactory);
    this.setComponentSelectedAction(selectionModifier);
    this.setComponentDeselectedAction(deselectionModifier);
    this.add((Component) this.layout);
    this.setItems(Arrays.asList(options));
  }

  private void rebuildComponents(Collection<T> items) {
    this.options.clear();
    this.options.addAll(items);
    this.rebuildComponents();
  }

  /**
   * Rebuilds the components - removes the existing ones then calls the factory to produce new ones.
   * Selected/deselected actions will be called on the newly created components, depending on their state.
   */
  protected final void rebuildComponents() {
    this.layout.removeAll();
    this.components.clear();
    int index = NO_SELECTION;
    for(T option: this.options) {
      index++;
      final C component = this.getComponentFactory().apply(index, option);
      component.addClickListener(this::onComponentClicked);
      if(index == this.getSelectedIndex())
        this.getComponentSelectedAction().accept(index, component);
      else this.getComponentDeselectedAction().accept(index, component);
      this.components.add(component);
      this.layout.add(component);
    }
  }

  /**
   * Returns the index of the given component.
   * @param component Component to find.
   * @return The index of the component, if found, otherwise {@link #NO_SELECTION}.
   */
  protected final int getComponentIndex(C component) {
    int result = NO_SELECTION;
    for(int zmp1 = 0; zmp1<this.components.size() && result == -1; zmp1++)
      if(Objects.equals(this.components.get(zmp1), component))
        result = zmp1;
    return result;
  }

  private void onComponentClicked(ClickEvent<C> event) {
    this.setSelectedIndex(this.getComponentIndex(event.getSource()));
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

  /**
   * Deselects component at the given index.
   * @param index Index of a component to deselect.
   */
  protected void deselect(int index) {
    this.ensureValidIndex(index);
    if(index != NO_SELECTION)
      this.whenDeselected.accept(index, this.components.get(index));

    this.selected = NO_SELECTION;
  }

  /**
   * Selects component at the given index.
   * @param index Index of a component to select.
   */
  protected void select(int index) {
    this.ensureValidIndex(index);
    if(index != NO_SELECTION)
      this.whenSelected.accept(index, this.components.get(index));
    this.selected = index;
  }

  /**
   * Sets selection index.
   * Note that when null value is not allowed and passed index is {@link #NO_SELECTION}, nothing will happen.
   * @param index New index.
   */
  public void setSelectedIndex(int index) {
    this.ensureValidIndex(index);
    // change selection
    if(index != this.getSelectedIndex()) {
      this.deselectCurrent();
      if (index != NO_SELECTION)
        this.select(index);
    }
    // deselect selection only if null value is explicitly allowed
    else if(this.isNullValueAllowed() && index != NO_SELECTION)
      this.deselectCurrent();

    this.updateValue();
  }

  /**
   * Returns the index of the currently selected component.
   * @return Index of the currently selected component. Can be {@link #NO_SELECTION}.
   */
  public int getSelectedIndex() {
    return this.selected;
  }

  private void ensureValidIndex(int index) {
    if(index < NO_SELECTION || index >= this.options.size())
      throw new IllegalArgumentException("incorrect index, expected 0 <= index < " + this.options.size());
  }

  /**
   * Sets the factory that will be used to create new components.
   * @param factory Factory. Must not be {@code null}.
   */
  public final void setComponentFactory(SerializableBiFunction<Integer, T, C> factory) {
    this.factory = Objects.requireNonNull(factory);
    this.rebuildComponents();
  }

  /**
   * Returns the current component factory.
   * @return The factory. Never {@code null}.
   */
  public SerializableBiFunction<Integer, T, C> getComponentFactory() {
    return this.factory;
  }

  /**
   * Chains {@link #setComponentFactory(SerializableBiFunction)} and returns itself.
   * @param factory Factory. Must not be {@code null}.
   * @return This.
   * @see #setComponentFactory(SerializableBiFunction)
   */
  public final ComponentSelect<C, T> withComponentFactory(SerializableBiFunction<Integer, T, C> factory) {
    this.setComponentFactory(factory);
    return this;
  }

  /**
   * Sets the action to be performed on a selected component.
   * @param action Action to use. If {@code null} is passed, result of {@link #noOp()} will be used instead.
   */
  public final void setComponentSelectedAction(SerializableBiConsumer<Integer, C> action) {
    this.whenSelected = Objects.requireNonNullElseGet(action, ComponentSelect::noOp);
    this.rebuildComponents();
  }

  /**
   * Returns the action that is currently performed when a component gets selected.
   * @return An action. Never {@code null}.
   */
  public SerializableBiConsumer<Integer, C> getComponentSelectedAction() {
    return this.whenSelected;
  }

  /**
   * Chains {@link #setComponentSelectedAction(SerializableBiConsumer)} and returns itself.
   * @param action Action.
   * @return This.
   * @see #setComponentSelectedAction(SerializableBiConsumer)
   */
  public final ComponentSelect<C, T> withComponentSelectedAction(SerializableBiConsumer<Integer, C> action) {
    this.setComponentSelectedAction(action);
    return this;
  }

  /**
   * Sets the action to be performed on a deselected component.
   * @param action Action to use. If {@code null} is passed, result of {@link #noOp()} will be used instead.
   */
  public final void setComponentDeselectedAction(SerializableBiConsumer<Integer, C> action) {
    this.whenDeselected = Objects.requireNonNullElseGet(action, ComponentSelect::noOp);
    this.rebuildComponents();
  }

  /**
   * Returns the action that is currently performed when a component gets deselected.
   * @return An action. Never {@code null}.
   */
  public SerializableBiConsumer<Integer, C> getComponentDeselectedAction() {
    return this.whenDeselected;
  }

  /**
   * Chains {@link #setComponentDeselectedAction(SerializableBiConsumer)} and returns itself.
   * @param action Action.
   * @return This.
   */
  public final ComponentSelect<C, T> withComponentDeselectedAction(SerializableBiConsumer<Integer, C> action) {
    this.setComponentDeselectedAction(action);
    return this;
  }

  @Override
  protected T generateModelValue() {
    return this.getSelectedIndex() == NO_SELECTION ? null : this.options.get(this.getSelectedIndex());
  }

  @Override
  protected void setPresentationValue(T t) {
    int selection = NO_SELECTION;
    if(t != null)
      for (int zmp1 = 0; zmp1 < this.options.size() && selection == NO_SELECTION; zmp1++)
        if (Objects.equals(t, this.options.get(zmp1)))
          selection = zmp1;

    if(selection != NO_SELECTION || this.isNullValueAllowed())
      this.setSelectedIndex(selection);
    else this.updateValue();
  }

  @Override
  public void setItems(Collection<T> collection) {
    this.rebuildComponents(collection);
  }

  @Override
  public boolean isNullValueAllowed() {
    return this.nullValueAllowed;
  }

  @Override
  public void setNullValueAllowed(boolean allowingNullValue) {
    this.nullValueAllowed = allowingNullValue;
  }

  @Override
  public void focus() {
    if(!this.components.isEmpty() && this.components.get(0) instanceof Focusable<?> first)
      first.focus();
    else super.focus();
  }

  /**
   * Returns the component at the given index.
   * Used in tests only.
   *
   * @param index Index.
   * @return The component.
   */
  C getComponent(int index) {
    this.ensureValidIndex(index);
    return this.components.get(index);
  }

  /**
   * Returns the number of components currently available.
   * Used in tests only.
   * @return The number of components.
   */
  int getComponentCount() {
    return this.components.size();
  }
}
