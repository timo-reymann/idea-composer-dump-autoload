package de.timo_reymann.composer_dump_autoload_phpstorm_plugin.settings

import com.intellij.openapi.Disposable
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.layout.panel
import javax.swing.JComponent

open class ComposerDumpAutoloadSettingsConfigurable(project: Project) : Configurable, Disposable {
    private val pluginSettings = ComposerDumpAutoloadSettings.getInstance(project)
    private val panel = panel {
        row("Command line arguments") {
            textField(pluginSettings::arguments)
                .comment("Arguments to suffix for dump-autoload")
                .focused()
        }
    }

    override fun getDisplayName(): String = "Composer Dump-Autoload"
    override fun createComponent(): JComponent = panel

    override fun isModified(): Boolean = panel.isModified()
    override fun reset() = panel.reset()
    override fun apply() = panel.apply()

    override fun dispose() {}
}
