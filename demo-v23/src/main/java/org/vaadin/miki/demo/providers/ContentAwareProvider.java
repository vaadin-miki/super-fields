package org.vaadin.miki.demo.providers;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.contentaware.ContentAware;

@Order(160)
public class ContentAwareProvider implements ComponentProvider<ContentAware> {

    @Override
    public ContentAware getComponent() {
        final ContentAware result = new ContentAware();
        result.add(new HorizontalLayout(), new HorizontalLayout(), new HorizontalLayout());
        return result;
    }
}
