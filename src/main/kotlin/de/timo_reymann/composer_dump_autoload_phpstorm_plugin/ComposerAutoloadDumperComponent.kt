package de.timo_reymann.composer_dump_autoload_phpstorm_plugin

import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.vfs.VirtualFileManager
import com.jetbrains.php.composer.ComposerDataService
import de.timo_reymann.composer_dump_autoload_phpstorm_plugin.command.ComposerCommandScheduler
import de.timo_reymann.composer_dump_autoload_phpstorm_plugin.listener.PhpFileListener
import de.timo_reymann.composer_dump_autoload_phpstorm_plugin.util.MessageBusUtil

/**
 * Plugin entrypoint component
 */
class ComposerAutoloadDumperComponent : StartupActivity {
    override fun runActivity(project: Project) {
        val composerDataService = ComposerDataService.getInstance(project)
        val scheduler = ComposerCommandScheduler(project)
        if (!composerDataService.isConfigWellConfigured) {
            logger.warn("Disable for project")
            MessageBusUtil.showMessage(
                project,
                NotificationType.WARNING,
                "Autodump for composer has been disabled",
                "It seems you are working on a non-composer project, or your config is invalid"
            )
            return
        }
        logger.info("Register file listener for current project")
        project.messageBus.connect().subscribe(VirtualFileManager.VFS_CHANGES, PhpFileListener(scheduler))
        scheduler.schedule()
    }

    companion object {
        private val logger = Logger.getInstance(
            ComposerAutoloadDumperComponent::class.java
        )
    }
}
