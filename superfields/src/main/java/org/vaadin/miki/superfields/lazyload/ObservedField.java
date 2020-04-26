package org.vaadin.miki.superfields.lazyload;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;

/**
 * A field that changes value depending on whether or not it is currently shown on screen.
 * @author miki
 * @since 2020-04-24
 */
public class ObservedField extends IntersectionObserverComponent<ObservedField.ObservedFieldElement> implements HasValue<HasValue.ValueChangeEvent<Boolean>, Boolean>, HasStyle {

    /**
     * This class exists only so that there is a nice custom name to the field.
     */
    @Tag("observed-field")
    public static final class ObservedFieldElement extends Div {}

    /**
     * Class name used when {@link #setRequiredIndicatorVisible(boolean)} is set to {@code true}.
     */
    public static final String REQUIRED_INDICATOR_VISIBLE = "required-indicator-visible";

    private final ArrayList<ValueChangeListener<? super ValueChangeEvent<Boolean>>> listeners = new ArrayList<>();

    private boolean value = false;

    private boolean readOnly = false;

    /**
     * Creates a field that will set its value to whether or not it is currently shown on screen.
     */
    public ObservedField() {
        this(false);
    }

    /**
     * Creates a field that will optionally only toggle value once, on first showing.
     * @param onlyToggleOnce Whether or not to trigger value change only once.
     */
    public ObservedField(boolean onlyToggleOnce) {
        super(onlyToggleOnce);
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
    public Registration addValueChangeListener(ValueChangeListener<? super ValueChangeEvent<Boolean>> valueChangeListener) {
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

    @Override
    protected void onNowHidden() {
        this.setValue(false);
    }

    @Override
    protected void onNowVisible() {
        this.setValue(true);
    }

}
