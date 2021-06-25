package de.timo_reymann.composer_dump_autoload_phpstorm_plugin.command

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.AppExecutorUtil
import de.timo_reymann.composer_dump_autoload_phpstorm_plugin.task.ComposerAutoloadDumpRunner
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Helper to schedule background task, that only executes the composer command if an change occured that
 * potentially needs to rebuild the composer autoload
 */
class ComposerCommandScheduler(project: Project?) : Runnable {
    private val logger = Logger.getInstance(
        ComposerCommandScheduler::class.java
    )
    private val shouldRun = AtomicBoolean()
    private val composerAutoloadDumpRunner: ComposerAutoloadDumpRunner = ComposerAutoloadDumpRunner(project)

    fun setShouldRun(shouldRun: Boolean) {
        this.shouldRun.set(shouldRun)
    }

    fun schedule() {
        logger.info("Add composer command to scheduler")
        AppExecutorUtil.getAppScheduledExecutorService()
            .scheduleWithFixedDelay(this, INITIAL_DELAY, EXECUTION_DELAY, TimeUnit.MILLISECONDS)
    }

    @Synchronized
    override fun run() {
        if (!shouldRun.get()) {
            return
        }
        composerAutoloadDumpRunner.execute()
        setShouldRun(false)
    }

    companion object {
        private const val INITIAL_DELAY = 1000L
        private const val EXECUTION_DELAY = 500L
    }

}
