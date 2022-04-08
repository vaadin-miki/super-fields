package org.vaadin.miki.superfields.text;

import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.customfield.CustomField;

/**
 * A label field. Basically, an unchangeable field that never modifies the value it is given.
 * Uses {@link #toString()} to show the text representation.
 *
 * @param <V> Value type.
 *
 * @author miki
 * @since 2022-04-08
 */
public class LabelField<V> extends CustomField<V> implements HasStyle {

    private final Text label = new Text("");

    private transient V value;

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
        this.label.setText(v == null ? "" : v.toString());
    }
}
