package org.vaadin.miki.demo.providers;

import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.demo.data.Book;
import org.vaadin.miki.demo.data.Person;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;
import org.vaadin.miki.superfields.object.ObjectField;
import org.vaadin.miki.superfields.util.CollectionComponentProviders;
import org.vaadin.miki.superfields.util.factory.ObjectFieldFactory;

import java.util.Collections;

/**
 * Provides an {@link ObjectField}.
 *
 * @author miki
 * @since 2022-06-16
 */
@Order(87)
public class ObjectFieldProvider implements ComponentProvider<ObjectField<Book>> {

    private final ObjectFieldFactory factory = new ObjectFieldFactory();

    public ObjectFieldProvider() {
        this.factory.registerInstanceProvider(Book.class, Book::new);
        this.factory.registerInstanceProvider(Person.class, Person::new);
        this.factory.setObjectFieldGroupLayoutProvider(FlexLayoutHelpers::row);

        this.factory.setCollectionFieldLayoutProvider(CollectionComponentProviders.columnWithHeaderAndFooterRows(
                Collections.singletonList(CollectionComponentProviders.removeAllButton("Clear list")),
                Collections.singletonList(CollectionComponentProviders.addLastButton("Add new"))
        ));
    }

    @Override
    public ObjectField<Book> getComponent() {
        return this.factory.buildAndConfigureObjectField(Book.class)
                .withHelperText("(this field with all its components is automatically generated from a model class)")
                .withHelperAbove();
    }
}
