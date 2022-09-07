package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.demo.data.Person;
import org.vaadin.miki.superfields.object.ObjectField;

import java.time.LocalDate;
import java.util.function.Consumer;

@Order(61)
public class ObjectFieldBuilder implements ContentBuilder<ObjectField<Person>> {
    @Override
    public void buildContent(ObjectField<Person> component, Consumer<Component[]> callback) {
        final ComboBox<Person> values = new ComboBox<>("Select a value: ",
                Person.of("Maria Skłodowska-Curie", LocalDate.of(1867, 11, 7), true),
                Person.of("Isaac Newton", LocalDate.of(1643, 1, 4), false)
                );
        values.addValueChangeListener(event -> component.setValue(event.getValue()));
        values.setAllowCustomValue(false);
        values.setItemLabelGenerator(Person::getName);
        callback.accept(new Component[]{values});
    }
}
