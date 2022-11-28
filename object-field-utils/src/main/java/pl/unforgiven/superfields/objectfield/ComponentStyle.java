package pl.unforgiven.superfields.objectfield;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates style names to be applied to the component.
 * @author miki
 * @since 2022-09-09
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ComponentStyle {

    /**
     * Note: these should be valid css identifier. No check is done.
     */
    String[] value();

}
