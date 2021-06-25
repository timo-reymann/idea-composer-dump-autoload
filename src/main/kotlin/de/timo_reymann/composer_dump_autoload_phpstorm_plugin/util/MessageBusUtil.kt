package de.timo_reymann.composer_dump_autoload_phpstorm_plugin.util

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

object MessageBusUtil {
    private const val GROUP_ID = "Composer Dump-Autoload"
    private val NOTIFICATION_GROUP = NotificationGroupManager.getInstance().getNotificationGroup(GROUP_ID)

    fun showMessage(project: Project?, type: NotificationType?, title: String?, message: String?) {
        NOTIFICATION_GROUP.createNotification(title!!, message!!, type!!, null)
            .notify(project)
    }
}
