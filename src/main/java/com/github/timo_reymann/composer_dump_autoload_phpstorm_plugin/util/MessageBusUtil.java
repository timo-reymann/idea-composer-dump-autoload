package com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.util;

import com.intellij.notification.*;
import com.intellij.openapi.project.Project;

public class MessageBusUtil {
    private static final String GROUP_ID = "Composer Dump-Autoload";

    public static final NotificationGroup NOTIFICATION_GROUP = NotificationGroupManager.getInstance().getNotificationGroup(GROUP_ID);

    public static void showMessage(Project project, NotificationType type, String title, String message) {
        NOTIFICATION_GROUP.createNotification(title, message, type, null)
                .notify(project);
    }
}
