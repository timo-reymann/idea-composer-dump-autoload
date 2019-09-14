package com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.util;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

public class MessageBusUtil {
    /**
     * Group id for notification identification
     */
    private static final String GROUP_ID = MessageBusUtil.class.getCanonicalName();

    public static Notification showMessage(NotificationType type, String title, String message) {
        Notification notification = new Notification(GROUP_ID, title, message, type);
        Notifications.Bus.notify(notification);
        return notification;
    }
}
