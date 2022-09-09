package org.vaadin.miki.superfields.object.reflect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a property should be ignored and not included in form building.
 * Used by {@link ReflectivePropertyProvider} to filter out fields marked with it.
 *
 * @author miki
 * @since 2022-06-03
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Ignore {
}
