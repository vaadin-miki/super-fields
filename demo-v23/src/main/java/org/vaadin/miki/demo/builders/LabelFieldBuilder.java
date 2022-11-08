package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.text.LabelField;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Consumer;

@Order(5)
public class LabelFieldBuilder implements ContentBuilder<LabelField<Object>> {
    @Override
    public void buildContent(LabelField<Object> component, Consumer<Component[]> callback) {
        final ComboBox<Object> allowedValues = new ComboBox<>("Select a value:", "a text", BigDecimal.valueOf(123.45), -30, LocalDate.of(2022, 11, 8), LocalDateTime.of(1999, 12, 31, 23, 59));
        allowedValues.addValueChangeListener(event -> component.setValue(event.getValue()));
        allowedValues.setClearButtonVisible(true);
        callback.accept(new Component[]{allowedValues});
    }
}
