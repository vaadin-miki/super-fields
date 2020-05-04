package org.vaadin.miki.superfields.lazyload;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.shared.Registration;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithValueMixin;

import java.util.ArrayList;

/**
 * A field that changes value depending on whether or not it is currently shown on screen.
 * @author miki
 * @since 2020-04-24
 */
public class ObservedField extends Composite<ObservedField.ObservedFieldElement>
        implements HasValue<AbstractField.ComponentValueChangeEvent<ObservedField, Boolean>, Boolean>, HasStyle,
                   WithValueMixin<AbstractField.ComponentValueChangeEvent<ObservedField, Boolean>, Boolean, ObservedField>,
                   WithIdMixin<ObservedField> {

    /**
     * This class gives a nice tag name to {@link ObservedField} in the browser.
     * The corresponding client-side module also extends {@link ComponentObserver}.
     */
    @Tag("observed-field")
    @JsModule("./observed-field.js")
    public static final class ObservedFieldElement extends ComponentObserver {}

    /**
     * Class name used when {@link #setRequiredIndicatorVisible(boolean)} is set to {@code true}.
     */
    public static final String REQUIRED_INDICATOR_VISIBLE = "required-indicator-visible";

    private final ArrayList<ValueChangeListener<? super AbstractField.ComponentValueChangeEvent<ObservedField, Boolean>>> listeners = new ArrayList<>();

    private final boolean onlyToggleOnce;

    private boolean value = false;

    private boolean readOnly = false;

    /**
     * Creates a field that will set its value to whether or not it is currently shown on screen.
     */
    public ObservedField() {
        this(false);
    }

    /**
     * Creates a field that will set its value to whether or not it is currently shown on screen.
     * It will also register given listener using {@link #addValueChangeListener(ValueChangeListener)}.
     * @param listener Listener to add.
     */
    public ObservedField(ValueChangeListener<HasValue.ValueChangeEvent<Boolean>> listener) {
        this();
        this.addValueChangeListener(listener);
    }

    /**
     * Creates a field that will optionally only toggle value once, on first showing.
     * @param onlyToggleOnce Whether or not to trigger value change only once.
     */
    public ObservedField(boolean onlyToggleOnce) {
        super();
        this.onlyToggleOnce = onlyToggleOnce;
        this.addClassName(this.getClass().getSimpleName().toLowerCase());
        this.getContent().addComponentObservationListener(this::onComponentObserved);
        this.getContent().addDetachListener(this::onObserverDetached);
        this.getContent().addAttachListener(this::onObserverAttached);
        this.getContent().observe(this);
    }

    /**
     * Creates a field that will optionally only toggle value once, on first showing.
     * It will also register given listener using {@link #addValueChangeListener(ValueChangeListener)}.
     * @param onlyToggleOnce Whether or not to trigger value change only once.
     * @param listener Listener to add.
     */
    public ObservedField(boolean onlyToggleOnce, ValueChangeListener<HasValue.ValueChangeEvent<Boolean>> listener) {
        this(onlyToggleOnce);
        this.addValueChangeListener(listener);
    }

    private void onComponentObserved(ComponentObservationEvent event) {
        this.setValue(event.isFullyVisible());
        if(event.isFullyVisible() && this.onlyToggleOnce)
            event.getSource().unobserve(this);
    }

    private void onObserverAttached(AttachEvent attachEvent) {
        // make sure the observer is observing
        if(!this.getContent().isObserving(this))
            this.getContent().observe(this);
    }

    private void onObserverDetached(DetachEvent event) {
        // when observer has been detached, it means this component has been also detached
        // so it is now invisible
        this.setValue(false);
    }

    /**
     * Fires value change to registered listeners.
     * @param currentValue Current value of the field.
     */
    protected void fireValueChangeEvent(boolean currentValue) {
        final AbstractField.ComponentValueChangeEvent<ObservedField, Boolean> event = new AbstractField.ComponentValueChangeEvent<>(this, this, !currentValue, false);
        this.listeners.forEach(listener -> listener.valueChanged(event));
    }

    @Override
    public void setValue(Boolean aBoolean) {
        if(!this.isReadOnly() && !aBoolean.equals(this.value)) {
            this.value = aBoolean;
            this.fireValueChangeEvent(this.value);
        }
    }

    @Override
    public Boolean getValue() {
        return this.value;
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<? super AbstractField.ComponentValueChangeEvent<ObservedField, Boolean>> valueChangeListener) {
        // why not eventBus? well, good luck in figuring out the generics
        this.listeners.add(valueChangeListener);
        return () -> this.listeners.remove(valueChangeListener);
    }

    @Override
    public void setReadOnly(boolean b) {
        this.readOnly = b;
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public void setRequiredIndicatorVisible(boolean visible) {
        if(visible)
            this.addClassName(REQUIRED_INDICATOR_VISIBLE);
        else this.removeClassName(REQUIRED_INDICATOR_VISIBLE);
        // this makes no sense, functionality-wise
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return this.getClassNames().contains(REQUIRED_INDICATOR_VISIBLE);
    }

}
