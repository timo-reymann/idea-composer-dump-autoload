package com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.util;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

public class MessageBusUtil {
    /**
     * Group id for notification identification
     */
    private static final String GROUP_ID = "Composer Dump-Autoload";

    public static Notification showMessage(NotificationType type, String title, String message) {
        Notification notification = new Notification(GROUP_ID + " (" + capitalize(type.name()) + ")", title, message, type);
        Notifications.Bus.notify(notification);
        return notification;
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.toLowerCase().substring(1);
    }
}
