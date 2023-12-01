package org.vaadin.miki.superfields.componentselect;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableBiFunction;
import org.vaadin.miki.markers.WithHelperMixin;
import org.vaadin.miki.markers.WithHelperPositionableMixin;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithItemsMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithLabelPositionableMixin;
import org.vaadin.miki.markers.WithValueMixin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Base class for Component(Multi)Select.
 * @author miki
 * @since 2023-11-28
 */
@SuppressWarnings("squid:S119") // SELF is a fine generic name that is more descriptive than S
public abstract class AbstractComponentSelect<C extends Component & ClickNotifier<C>, T, SELF extends AbstractComponentSelect<C, T, SELF>> extends CustomField<T>
 implements HasStyle, WithItemsMixin<T, SELF>, WithIdMixin<SELF>,
    WithLabelMixin<SELF>, WithLabelPositionableMixin<SELF>,
    WithHelperMixin<ComponentSelect<C, T>>, WithHelperPositionableMixin<SELF>,
    WithValueMixin<AbstractField.ComponentValueChangeEvent<CustomField<T>, T>, T, SELF> {

  /**
   * Index for no selection.
   */
  public static final int NO_SELECTION = -1;

  /**
   * A no-op, default do-nothing operation for selection/deselection.
   * @return A bi-consumer that does nothing.
   * @param <X> First argument type.
   * @param <Y> Second argument type.
   */
  protected static <X, Y> SerializableBiConsumer<X, Y> noOp() {return (x, y) -> {};}

  private final List<T> options = new ArrayList<>();
  private final List<C> components = new ArrayList<>();
  private final HasComponents layout;

  private SerializableBiFunction<Integer, T, C> factory;
  private SerializableBiConsumer<Integer, C> whenSelected = noOp();
  private SerializableBiConsumer<Integer, C> whenDeselected = noOp();

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
  protected <L extends Component & HasComponents> AbstractComponentSelect(Supplier<L> layoutSupplier, SerializableBiFunction<Integer, T, C> componentFactory, SerializableBiConsumer<Integer, C> selectionModifier, SerializableBiConsumer<Integer, C> deselectionModifier, T... options) {
    this.layout = layoutSupplier.get();
    this.setComponentFactory(componentFactory);
    this.setComponentSelectedAction(selectionModifier);
    this.setComponentDeselectedAction(deselectionModifier);
    this.add((Component) this.layout);
    this.setItems(Arrays.asList(options));
  }

  /**
   * Checks if the item at the given index is currently selected.
   * @param index Index of an item.
   * @return {@code true} when the item is selected, {@code false} otherwise.
   */
  protected abstract boolean isSelected(int index);

  /**
   * Clicks an item at the given index.
   * @param index Index of an item to be clicked.
   * @return Whether value should be updated.
   */
  protected abstract boolean itemClicked(int index);

  private void onComponentClicked(ClickEvent<C> event) {
    if(this.itemClicked(this.getComponentIndex(event.getSource())))
      this.updateValue();
  }

  /**
   * Deselects component at the given index.
   * @param index Index of a component to deselect.
   */
  protected void deselect(int index) {
    this.ensureValidIndex(index);
    if(index != NO_SELECTION)
      this.whenDeselected.accept(index, this.components.get(index));
  }

  /**
   * Selects component at the given index.
   * @param index Index of a component to select.
   */
  protected void select(int index) {
    this.ensureValidIndex(index);
    if(index != NO_SELECTION)
      this.whenSelected.accept(index, this.components.get(index));
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
      if(this.isSelected(index))
        this.getComponentSelectedAction().accept(index, component);
      else this.getComponentDeselectedAction().accept(index, component);
      this.components.add(component);
      this.layout.add(component);
    }
  }

  /**
   * Returns the current list of options. Modifying this list will modify the options available to this component, but will not rebuild components.
   * @return List of options. Never {@code null}, but possibly empty.
   */
  protected List<T> getOptions() {
    return options;
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

  /**
   * Ensures the index is within bounds.
   * @param index Index to check.
   * @throws IllegalArgumentException when the index is outside the allowed bounds.
   */
  protected void ensureValidIndex(int index) {
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
  @SuppressWarnings("unchecked") // should be fine
  public final SELF withComponentFactory(SerializableBiFunction<Integer, T, C> factory) {
    this.setComponentFactory(factory);
    return (SELF) this;
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
  @SuppressWarnings("unchecked") // should be fine
  public final SELF withComponentSelectedAction(SerializableBiConsumer<Integer, C> action) {
    this.setComponentSelectedAction(action);
    return (SELF) this;
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
  @SuppressWarnings("unchecked") // should be fine
  public final SELF withComponentDeselectedAction(SerializableBiConsumer<Integer, C> action) {
    this.setComponentDeselectedAction(action);
    return (SELF) this;
  }

  @Override
  public void setItems(Collection<T> items) {
    this.options.clear();
    this.options.addAll(items);
    this.rebuildComponents();
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
   *
   * @return The number of components.
   */
  int getComponentCount() {
    return this.components.size();
  }

}
