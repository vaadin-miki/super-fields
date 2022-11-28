package pl.unforgiven.superfields.objectfield;

import com.vaadin.flow.component.HasValue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the field should be shown using the provided component.
 * Note: the component <strong>must have</strong> a public, no-args constructor.
 * @author miki
 * @since 2022-09-08
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ShowFieldAs {

    /**
     * The component class to use. It must have a public, no argument constructor.
     */
    @SuppressWarnings("rawtypes") // there is no way to enforce types here
    Class<? extends HasValue> value();

}
