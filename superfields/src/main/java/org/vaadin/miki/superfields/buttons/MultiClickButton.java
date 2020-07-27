package org.vaadin.miki.superfields.buttons;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.shared.Registration;
import org.vaadin.miki.events.click.ComponentClickEvent;
import org.vaadin.miki.events.click.ComponentClickListener;
import org.vaadin.miki.events.click.ComponentClickNotifier;
import org.vaadin.miki.events.state.StateChangeEvent;
import org.vaadin.miki.events.state.StateChangeListener;
import org.vaadin.miki.events.state.StateChangeNotifier;
import org.vaadin.miki.markers.Clickable;
import org.vaadin.miki.markers.HasState;
import org.vaadin.miki.markers.WithIdMixin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A button that needs multiple clicks before a regular click listener can be invoked.
 *
 * @author miki
 * @since 2020-07-06
 */
@Tag("multi-click-button")
public class MultiClickButton extends Composite<Button>
        implements WithIdMixin<MultiClickButton>, ComponentClickNotifier<MultiClickButton>,
                   Clickable, HasStyle, HasSize, HasEnabled, HasState<ButtonState>,
                   StateChangeNotifier<ButtonState, MultiClickButton> {

    private final Button button = new Button("", this::registerButtonClick);

    private final List<ButtonState> states = new ArrayList<>();

    private int index = 0;

    /**
     * Creates a button with given states.
     * @param states An array of {@link ButtonState}s. Once button reaches last state, it fires click event.
     */
    public MultiClickButton(ButtonState... states) {
        super();
        this.setStates(states);
    }

    /**
     * Creates a button with given listener and states.
     * @param listener Listener that will be notified once button reaches its final state and is clicked.
     * @param states An array of {@link ButtonState}s. Once button reaches last state, it fires click event.
     */
    public MultiClickButton(ComponentClickListener<MultiClickButton> listener, ButtonState... states) {
        this(states);
        this.addClickListener(listener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Registration addClickListener(ComponentClickListener<MultiClickButton> listener) {
        return this.getEventBus().addListener((Class<ComponentClickEvent<MultiClickButton>>)(Class<?>)ComponentClickEvent.class, listener);
    }

    private void registerButtonClick(ClickEvent<Button> event) {
        if(this.states.size() > 1) {
            this.index = (this.index + 1) % this.states.size();
            this.updateButtonState(this.states.get(this.index));
        }
        if(this.index == 0)
            this.fireEvent(new ComponentClickEvent<>(this, new ClickEvent<>(this, event.isFromClient(), event.getScreenX(), event.getScreenY(), event.getClientX(), event.getClientY(), event.getClickCount(), event.getButton(), event.isCtrlKey(), event.isShiftKey(), event.isAltKey(), event.isMetaKey())));
    }

    /**
     * Updates the state of the button from old to new.
     * @param newState New state.
     */
    protected void updateButtonState(ButtonState newState) {
        this.button.getClassNames().clear();
        this.button.getThemeNames().clear(); // this clears both names and variants
        this.button.setText(newState.getText());
        this.button.setIcon(newState.getIcon());
        this.button.addThemeNames(newState.getThemeNames().toArray(new String[0]));
        this.button.addThemeVariants(newState.getThemeVariants().toArray(new ButtonVariant[0]));
        this.button.addClassNames(newState.getClassNames().toArray(new String[0]));
        this.fireEvent(new StateChangeEvent<>(this, false, this.getState()));
    }

    /**
     * Sets the states of the button that need to be clicked one after another before the real click happens.
     * @param states {@link ButtonState}s to use. Old states will be removed.
     */
    public void setStates(Collection<ButtonState> states) {
        this.clear();
        this.states.addAll(states);
        if(!this.states.isEmpty())
            this.updateButtonState(this.states.get(0));
    }

    /**
     * Sets the states of the button that need to be clicked one after another before the real click happens.
     * @param states {@link ButtonState}s to use. Old states will be removed.
     */
    public final void setStates(ButtonState... states) {
        this.setStates(Arrays.asList(states));
    }

    /**
     * Returns currently displayed text.
     * @return Currently displayed text.
     */
    public String getText() {
        return this.button.getText();
    }

    /**
     * Returns current icon.
     * @return Current icon.
     */
    public Component getIcon() {
        return this.button.getIcon();
    }

    @Override
    public ButtonState getState() {
        return this.states.isEmpty() ? ButtonState.of(this.button) : this.states.get(this.index);
    }

    /**
     * Removes all associated states.
     */
    public void clear() {
        this.states.clear();
        this.index = 0;
        this.button.setText("");
        this.button.setIcon(null);
        this.button.getClassNames().clear();
    }

    @Override
    protected Button initContent() {
        this.button.setSizeFull();
        this.button.addClassName("multi-click-inner-button");
        return this.button;
    }

    @Override
    public void click() {
        this.button.click();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.button.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return this.button.isEnabled();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Registration addStateChangeListener(StateChangeListener<ButtonState, MultiClickButton> listener) {
        return this.getEventBus().addListener((Class<StateChangeEvent<ButtonState, MultiClickButton>>)(Class<?>)StateChangeEvent.class, listener);
    }
}
