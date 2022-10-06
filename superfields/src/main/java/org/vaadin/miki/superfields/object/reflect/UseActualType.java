package org.vaadin.miki.superfields.object.reflect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an actual type of the value of the given field should be used, rather than the declared one.
 * @author miki
 * @since 2022-10-06
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface UseActualType {
}
