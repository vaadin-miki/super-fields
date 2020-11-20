package org.vaadin.miki.demo;

/**
 * Marker interface for an object that needs access to {@link ValidatorStorage}.
 * @author miki
 * @since 2020-11-18
 */
@FunctionalInterface
public interface NeedsValidatorStorage {

    /**
     * Sets the {@link ValidatorStorage} associated with this object.
     * @param storage Storage. Must not be {@code null}.
     */
    void setValidatorStorage(ValidatorStorage storage);

}
