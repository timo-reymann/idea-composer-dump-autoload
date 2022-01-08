package de.timo_reymann.composer_dump_autoload_phpstorm_plugin.command

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandler
import com.intellij.openapi.util.Key
import java.util.concurrent.atomic.AtomicLong

/**
 * Wrapper to execute commands for composer, either by providing a phar with php prefixed or simply passing the executable
 */
class ComposerCommandExecutor(
    private val executable: String,
    private val workingDirectory: String,
    private val command: String
) {
    @Throws(ComposerCommandFailedException::class)
    fun run(): List<String> {
        val processHandler: ProcessHandler?
        val stdout: MutableList<String> = ArrayList()
        val line = AtomicLong(0)
        try {
            processHandler = OSProcessHandler(
                GeneralCommandLine(executable)
                    .withParameters(*command.split(" ").toTypedArray())
                    .withWorkDirectory(workingDirectory)
            )
            with(processHandler) {
                addProcessListener(object : ProcessAdapter() {
                    @Synchronized
                    override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                        // Empty line or line break
                        if (event.text.trim { it <= ' ' } == "") {
                            return
                        }

                        // First line is command output, remove it from stdout
                        if (line.incrementAndGet() == 1L) {
                            return
                        }
                        stdout.add(event.text.trim { it <= ' ' })
                    }
                })

                // Wait synchronous
                startNotify()
                waitFor()
            }

            if (processHandler.getExitCode() != null && processHandler.getExitCode() != 0) {
                throw ExecutionException("Exited with code " + processHandler.getExitCode())
            }
        } catch (e: ExecutionException) {
            throw ComposerCommandFailedException(
                "Failed to execute composer command $command",
                java.lang.String.join("\n", stdout)
            )
        }
        return stdout
    }

    companion object {
        const val DUMP_AUTOLOAD = "dump-autoload"
    }
}
