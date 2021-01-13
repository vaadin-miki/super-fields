package org.vaadin.miki.superfields.dates;

/**
 * Marker interface for objects that have a {@link SuperDatePickerI18n}.
 * Internal use only.
 *
 * @author miki
 * @since 2021-01-13
 */
@FunctionalInterface
interface HasSuperDatePickerI18N {

    /**
     * Returns the current instance of the {@link SuperDatePickerI18n} associated with this object.
     * @return A non-{@code null} instance.
     */
    SuperDatePickerI18n getSuperDatePickerI18n();

}
