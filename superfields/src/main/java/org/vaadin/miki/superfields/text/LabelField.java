package org.vaadin.miki.superfields.text;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.function.SerializableFunction;
import org.vaadin.miki.markers.WithHelperMixin;
import org.vaadin.miki.markers.WithHelperPositionableMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithValueMixin;

/**
 * A label field. Basically, an unchangeable field that never modifies the value it is given.
 * It uses {@link #toString()} to show the text representation and {@link #DEFAULT_NULL_REPRESENTATION} to show nulls by default.
 *
 * @param <V> Value type.
 *
 * @author miki
 * @since 2022-04-08
 */
public class LabelField<V> extends CustomField<V> implements HasStyle, WithLabelMixin<LabelField<V>>,
        WithHelperPositionableMixin<LabelField<V>>, WithHelperMixin<LabelField<V>>,
        WithValueMixin<AbstractField.ComponentValueChangeEvent<CustomField<V>, V>, V, LabelField<V>> {

    public static final String DEFAULT_NULL_REPRESENTATION = "";

    private final Text label = new Text(DEFAULT_NULL_REPRESENTATION);

    private transient V value;

    private String nullRepresentation = DEFAULT_NULL_REPRESENTATION;

    private SerializableFunction<V, String> converter = Object::toString;

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

    public String getNullRepresentation() {
        return nullRepresentation == null ? DEFAULT_NULL_REPRESENTATION : "";
    }

    public void setNullRepresentation(String nullRepresentation) {
        this.nullRepresentation = nullRepresentation;
    }

    public final LabelField<V> withNullRepresentation(String nullRepresentation) {
        this.setNullRepresentation(nullRepresentation);
        return this;
    }

    public SerializableFunction<V, String> getConverter() {
        return converter == null ? Object::toString : converter;
    }

    public void setConverter(SerializableFunction<V, String> converter) {
        this.converter = converter;
    }

    public final LabelField<V> withConverter(SerializableFunction<V, String> converter) {
        this.setConverter(converter);
        return this;
    }
}
