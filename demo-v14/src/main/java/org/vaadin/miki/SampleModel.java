package org.vaadin.miki;

/**
 * A generic model to use in validation.
 * @param <T> Type of the object to hold.
 * @author miki
 * @since 2020-11-14
 */
public class SampleModel<T> {

    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

}
