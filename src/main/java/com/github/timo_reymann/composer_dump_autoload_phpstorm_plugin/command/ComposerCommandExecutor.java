package com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.command;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Wrapper to execute commands for composer, either by providing a phar with php prefixed or simply passing the executable
 */
public class ComposerCommandExecutor {
    public static final String DUMP_AUTOLOAD = "dump-autoload --optimize";

    private final String executable;
    private final String workingDirectory;
    private final String command;

    public ComposerCommandExecutor(String executable, String workingDirectory, String command) {
        this.executable = executable;
        this.workingDirectory = workingDirectory;
        this.command = command;
    }

    public List<String> run() throws ComposerCommandFailedException {
        ProcessHandler processHandler = null;
        List<String> stdout = new ArrayList<>();
        AtomicLong line = new AtomicLong(0);
        try {
            processHandler = new OSProcessHandler(
                    new GeneralCommandLine(this.executable)
                            .withParameters(command.split(" "))
                            .withWorkDirectory(workingDirectory)
            );
            processHandler.addProcessListener(new ProcessAdapter() {
                @Override
                public synchronized void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                    // Empty line or line break
                    if (event.getText().trim().equals("")) {
                        return;
                    }

                    // First line is command output, remove it from stdout
                    if (line.incrementAndGet() == 1) {
                        return;
                    }

                    stdout.add(event.getText().trim());
                }
            });

            // Wait synchronous
            processHandler.startNotify();
            processHandler.waitFor();

            if (processHandler.getExitCode() != null && processHandler.getExitCode() != 0) {
                throw new ExecutionException("Exited with code " + processHandler.getExitCode());
            }
        } catch (ExecutionException e) {
            throw new ComposerCommandFailedException("Failed to execute composer command " + command, String.join("\n", stdout));
        }

        return stdout;
    }

}
