package org.vaadin.miki.superfields.util.factory;

import org.vaadin.miki.superfields.object.builder.FieldBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Overrides the default builder and forces the field to be rendered with whatever the output of the builder is.
 * Note: the specified {@link FieldBuilder} <strong>must have</strong> a public, no-arg constructor.
 * @author miki
 * @since 2022-09-08
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface BuildFieldWith {

    /**
     * The builder to use. It <strong>must have</strong> a public, no argument constructor.
     */
    @SuppressWarnings("rawtypes") // no way to enforce the types here
    Class<? extends FieldBuilder> value();

}
