package pl.unforgiven.superfields.objectfield;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates order in which the field should be displayed.
 * @author miki
 * @since 2022-09-08
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface FieldOrder {

    int value() default 0;

}
