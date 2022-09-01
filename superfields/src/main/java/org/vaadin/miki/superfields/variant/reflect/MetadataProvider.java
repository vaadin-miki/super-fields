package org.vaadin.miki.superfields.variant.reflect;

import org.vaadin.miki.superfields.variant.ObjectPropertyMetadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

@FunctionalInterface
public interface MetadataProvider {

    Collection<ObjectPropertyMetadata> getMetadata(String name, Field field, Method setter, Method getter);

}
