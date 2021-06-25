package de.timo_reymann.composer_dump_autoload_phpstorm_plugin.task

import com.intellij.notification.NotificationType
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task.Backgroundable
import com.intellij.openapi.project.Project
import de.timo_reymann.composer_dump_autoload_phpstorm_plugin.command.ComposerCommandExecutor
import de.timo_reymann.composer_dump_autoload_phpstorm_plugin.command.ComposerCommandFailedException
import de.timo_reymann.composer_dump_autoload_phpstorm_plugin.util.MessageBusUtil

class ComposerCommandBackgroundTask internal constructor(
    project: Project?,
    private val text: String,
    private val executor: ComposerCommandExecutor
) : Backgroundable(project, text) {
    override fun run(indicator: ProgressIndicator) {
        indicator.isIndeterminate = true
        indicator.text = text
        try {
            val stdout = executor.run()
            MessageBusUtil.showMessage(
                project,
                NotificationType.INFORMATION,
                "Autoload dumped successfully",
                java.lang.String.join("\n", stdout)
            )
        } catch (e: ComposerCommandFailedException) {
            MessageBusUtil.showMessage(
                project,
                NotificationType.ERROR,
                String.format("Error executing '%s'", text),
                String.format("<html>%s<br /><pre>%s</pre></html>", e.message, e.commandOutput)
            )
            indicator.cancel()
        }
    }
}
