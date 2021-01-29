package org.vaadin.miki.demo.providers;

import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.contentaware.ContentAware;

@Order(160)
public class ContentAwareProvider implements ComponentProvider<ContentAware> {

    @Override
    public ContentAware getComponent() {
        return new ContentAware();
    }
}
