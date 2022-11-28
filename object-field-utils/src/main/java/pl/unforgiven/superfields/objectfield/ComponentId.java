package pl.unforgiven.superfields.objectfield;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates id of a component.
 * @author miki
 * @since 2022-09-09
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ComponentId {

    /**
     * Note: this must be a valid css identifier, meaning it must be unique across the web page.
     */
    String value();

}
