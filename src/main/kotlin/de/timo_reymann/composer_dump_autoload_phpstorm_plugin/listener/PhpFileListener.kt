package de.timo_reymann.composer_dump_autoload_phpstorm_plugin.listener

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.jetbrains.php.lang.PhpFileType
import de.timo_reymann.composer_dump_autoload_phpstorm_plugin.command.ComposerCommandScheduler

/**
 * Listener for virtual file system events
 */
class PhpFileListener(private val scheduler: ComposerCommandScheduler) : BulkFileListener {
    override fun after(events: List<VFileEvent>) {
        events.firstOrNull { isQualified(it) }
            ?.also {
                logger.info("Got qualification")
                scheduler.setShouldRun(true)
            }
    }

    private fun isQualified(event: VFileEvent): Boolean {
        // Exclude life cycle pseudo changes not relevant for autoloading
        if (event.isFromSave || event.isFromRefresh || event.file == null) {
            return false
        }

        return when {
            // Directory is interesting, as it may contain php files
            event.file!!.isDirectory -> true

            // PHP files are the always interesting use case
            else -> event.file!!.fileType.name == FILE_TYPE_PHP
        }
    }

    companion object {
        private val FILE_TYPE_PHP = PhpFileType.INSTANCE.name
        private val logger = Logger.getInstance(
            PhpFileListener::class.java
        )
    }
}
