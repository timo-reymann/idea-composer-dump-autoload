package de.timo_reymann.composer_dump_autoload_phpstorm_plugin.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

@State(
    name = "de.timo_reymann.composer_dump_autoload_phpstorm_plugin.settings.ComposerDumpAutoloadSettings",
    storages = [Storage("composerDumpAutoloadPlugin.xml")]
)
open class ComposerDumpAutoloadSettings : PersistentStateComponent<ComposerDumpAutoloadSettings>, BaseState() {
    var arguments: String by nonNullString("--optimize")

    override fun getState(): ComposerDumpAutoloadSettings = this
    override fun loadState(state: ComposerDumpAutoloadSettings) = copyFrom(state)
    private fun nonNullString(initialValue: String = "") = property(initialValue) { it == initialValue }

    companion object {
        fun getInstance(project: Project) = project.service<ComposerDumpAutoloadSettings>()
    }
}
