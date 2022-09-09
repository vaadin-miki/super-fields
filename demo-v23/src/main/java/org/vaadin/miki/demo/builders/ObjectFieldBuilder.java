package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.demo.data.Book;
import org.vaadin.miki.demo.data.Person;
import org.vaadin.miki.superfields.object.ObjectField;

import java.time.LocalDate;
import java.util.function.Consumer;

@Order(61)
public class ObjectFieldBuilder implements ContentBuilder<ObjectField<Book>> {
    @Override
    public void buildContent(ObjectField<Book> component, Consumer<Component[]> callback) {
        final ComboBox<Book> values = new ComboBox<>("Select a value: ",
                Book.of("1984", 1948, "English", Person.of("George Orwell", LocalDate.of(1903, 6, 25), false)),
                Book.of("The God Delusion", 2006, "English", Person.of("Richard Dawkins", LocalDate.of(1941, 3, 26), false)),
                Book.of("Dolina Issy", 1955, "polski", Person.of("Czesław Miłosz", LocalDate.of(1911, 6, 30), true))
                );
        values.addValueChangeListener(event -> component.setValue(event.getValue()));
        values.setAllowCustomValue(false);
        values.setItemLabelGenerator(Book::getTitle);
        callback.accept(new Component[]{values});
    }
}
