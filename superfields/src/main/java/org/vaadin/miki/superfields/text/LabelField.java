package org.vaadin.miki.superfields.text;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.function.SerializableFunction;
import org.vaadin.miki.markers.WithHelperMixin;
import org.vaadin.miki.markers.WithHelperPositionableMixin;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithLabelPositionableMixin;
import org.vaadin.miki.markers.WithValueMixin;

/**
 * A label field. Basically, an unchangeable field that does not allow any UI controls to modify the value.
 * By default, it uses {@link #toString()} to show the text representation and {@link #DEFAULT_NULL_REPRESENTATION} to show {@code null} values.
 *
 * @param <V> Value type.
 *
 * @author miki
 * @since 2022-04-08
 */
@CssImport(value = "./styles/label-positions.css", themeFor = "vaadin-custom-field")
@Tag("label-field")
@JsModule("./label-field.js")
public class LabelField<V> extends CustomField<V> implements HasStyle, WithLabelMixin<LabelField<V>>,
        WithHelperPositionableMixin<LabelField<V>>, WithHelperMixin<LabelField<V>>,
        WithLabelPositionableMixin<LabelField<V>>, WithIdMixin<LabelField<V>>,
        WithValueMixin<AbstractField.ComponentValueChangeEvent<CustomField<V>, V>, V, LabelField<V>> {

    public static final String DEFAULT_NULL_REPRESENTATION = "";

    private final Text label = new Text(DEFAULT_NULL_REPRESENTATION);

    private transient V value;

    private String nullRepresentation = DEFAULT_NULL_REPRESENTATION;

    private SerializableFunction<V, String> converter = Object::toString;

    /**
     * Creates a label field.
     */
    public LabelField() {
        super();
        this.add(label);
    }

    @Override
    protected V generateModelValue() {
        return this.value;
    }

    @Override
    protected void setPresentationValue(V v) {
        this.value = v;
        this.label.setText(v == null ? this.getNullRepresentation() : this.getConverter().apply(v));
    }

    /**
     * Returns current representation for {@code null} value.
     * @return A text that is displayed when the value is {@code null}. Will never be {@code null} itself.
     */
    public final String getNullRepresentation() {
        return nullRepresentation;
    }

    /**
     * Sets text to be displayed when the value is {@code null}.
     * @param nullRepresentation New text. If it is {@code null}, {@link #DEFAULT_NULL_REPRESENTATION} will be used instead.
     */
    public final void setNullRepresentation(String nullRepresentation) {
        this.nullRepresentation = nullRepresentation == null ? DEFAULT_NULL_REPRESENTATION : nullRepresentation;
        if(this.value == null)
            this.label.setText(nullRepresentation);
    }

    /**
     * Chains {@link #setNullRepresentation(String)} and returns itself.
     * @param nullRepresentation Text to display when value is {@code null}.
     * @return This.
     */
    public final LabelField<V> withNullRepresentation(String nullRepresentation) {
        this.setNullRepresentation(nullRepresentation);
        return this;
    }

    /**
     * Returns converter from value to {@link String}.
     * @return A non-{@code null} function that accepts {@code V} and returns a {@link String}. {@link Object#toString()} by default.
     */
    public SerializableFunction<V, String> getConverter() {
        return converter == null ? Object::toString : converter;
    }

    /**
     * Sets a converter used to translate a value of type {@code V} to {@link String}. Updates the currently shown text.
     * @param converter A converter to use. {@code null} is never passed to the converter.
     * @see #setNullRepresentation(String)
     */
    public void setConverter(SerializableFunction<V, String> converter) {
        this.converter = converter;
        if(this.value != null)
            this.label.setText(this.getConverter().apply(this.value));
    }

    /**
     * Chains {@link #setConverter(SerializableFunction)} and returns itself.
     * @param converter Converter to use.
     * @return This.
     */
    public final LabelField<V> withConverter(SerializableFunction<V, String> converter) {
        this.setConverter(converter);
        return this;
    }

    /**
     * Returns the {@link Text} displaying the current value of the label.
     * Used for tests.
     * @return A {@link Text}.
     */
    Text getText() {
        return this.label;
    }

}
