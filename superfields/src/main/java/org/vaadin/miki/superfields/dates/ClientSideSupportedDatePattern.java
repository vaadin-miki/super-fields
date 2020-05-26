package org.vaadin.miki.superfields.dates;

/**
 * Extension of {@link DatePattern} that is natively supported by client-side components.
 * @author miki
 * @since 2020-05-26
 */
public interface ClientSideSupportedDatePattern extends DatePattern {
    /**
     * Returns a non-null client-side supported pattern.
     * @return Pattern supported by the client side.
     */
    String getClientSidePattern();
}
