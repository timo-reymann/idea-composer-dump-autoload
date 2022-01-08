package de.timo_reymann.composer_dump_autoload_phpstorm_plugin.task

import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.jetbrains.php.composer.ComposerDataService
import com.jetbrains.php.composer.execution.executable.ExecutableComposerExecution
import com.jetbrains.php.composer.execution.phar.PharComposerExecution
import de.timo_reymann.composer_dump_autoload_phpstorm_plugin.command.ComposerCommandExecutor
import de.timo_reymann.composer_dump_autoload_phpstorm_plugin.settings.ComposerDumpAutoloadSettings
import de.timo_reymann.composer_dump_autoload_phpstorm_plugin.util.MessageBusUtil
import java.io.File

/**
 * Runner for dumping autoload
 */
class ComposerAutoloadDumpRunner(private val project: Project) {
    private val logger = Logger.getInstance(
        ComposerAutoloadDumpRunner::class.java
    )

    private fun getArgs(): String {
        val parameters = ComposerDumpAutoloadSettings.getInstance(project).arguments
        val delimiter = when {
            parameters.isBlank() -> ""
            else -> " "
        }
        return "${ComposerCommandExecutor.DUMP_AUTOLOAD}${delimiter}${parameters}"
    }

    fun execute() {
        logger.info("Triggered autoload dump")

        // Gather needed information
        val composerDataService = ComposerDataService.getInstance(project)
        val workingDirectory = File(composerDataService.configPath).parent
        val args = getArgs()

        // Find executor for composer to run command with
        val executor = when (val execution = composerDataService.composerExecution) {
            is ExecutableComposerExecution -> ComposerCommandExecutor(
                execution.executablePath,
                workingDirectory,
                args
            )

            is PharComposerExecution -> ComposerCommandExecutor(
                "php",
                workingDirectory,
                "${execution.pharPath} $args"
            )

            else -> {
                MessageBusUtil.showMessage(
                    project,
                    NotificationType.ERROR,
                    "Unsupported composer type",
                    "Composer runtime type is not supported!"
                )
                return
            }
        }

        // Schedule task with fancy progress bar
        ProgressManager.getInstance()
            .run(ComposerCommandBackgroundTask(project, "Dump autoload", executor))
    }
}
