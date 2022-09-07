package org.vaadin.miki.superfields.object.reflect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a given type (and all of its subtypes) should not have superclass properties scanned.
 * Used by {@link ReflectivePropertyProvider}.
 *
 * @author miki
 * @since 2022-06-03
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface DoNotScanSuperclasses {
}
