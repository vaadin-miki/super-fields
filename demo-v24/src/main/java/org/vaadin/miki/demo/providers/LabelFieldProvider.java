package org.vaadin.miki.demo.providers;

import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.text.LabelField;

@Order(88)
public class LabelFieldProvider implements ComponentProvider<LabelField<Object>> {
    @Override
    public LabelField<Object> getComponent() {
        return new LabelField<>().withHelperText("(this is a LabelField<Object> using .toString())")
                .withHelperBelow()
                .withLabel("See the value:")
                .withNullRepresentation("(no value specified)");
    }
}
