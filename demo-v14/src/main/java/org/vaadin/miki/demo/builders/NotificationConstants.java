package org.vaadin.miki.demo.builders;

/**
 * Storage class for constants related to notifications.
 * @author miki
 * @since 2020-11-19
 */
class NotificationConstants {

    static final int NOTIFICATION_TIME = 1500;

    static final String FOCUS_MESSAGE = "Component %s received focus.";

    static final String BLUR_MESSAGE = "Component %s lost focus.";

    static final String STATE_MESSAGE = "Component %s changed its state.";

    private NotificationConstants() {
        // no instances allowed
    }

}
