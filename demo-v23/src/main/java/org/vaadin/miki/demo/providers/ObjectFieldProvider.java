package org.vaadin.miki.demo.providers;

import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.demo.data.Person;
import org.vaadin.miki.superfields.object.ObjectField;

/**
 * Provides an {@link ObjectField}.
 *
 * @author miki
 * @since 2022-06-16
 */
@Order(95)
public class ObjectFieldProvider implements ComponentProvider<ObjectField<Person>> {
    @Override
    public ObjectField<Person> getComponent() {
        return new ObjectField<>(Person.class, Person::new);
    }
}
