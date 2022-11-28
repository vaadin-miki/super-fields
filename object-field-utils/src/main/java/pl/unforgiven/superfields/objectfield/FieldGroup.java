package pl.unforgiven.superfields.objectfield;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a group the field belongs to.
 * @author miki
 * @since 2022-09-08
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface FieldGroup {

    String value() default "";

}
