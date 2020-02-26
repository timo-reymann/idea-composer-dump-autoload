package com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.task;

import com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.command.ComposerCommandExecutor;
import com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.util.MessageBusUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.composer.ComposerDataService;
import com.jetbrains.php.composer.execution.ComposerExecution;
import com.jetbrains.php.composer.execution.executable.ExecutableComposerExecution;
import com.jetbrains.php.composer.execution.phar.PharComposerExecution;

import java.io.File;

import static com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.command.ComposerCommandExecutor.DUMP_AUTOLOAD;

/**
 * Runner for dumping autoload
 */
public class ComposerAutoloadDumpRunner {
    private final Project project;
    private final Logger logger = Logger.getInstance(ComposerAutoloadDumpRunner.class);

    public ComposerAutoloadDumpRunner(Project project) {
        this.project = project;
    }

    public void execute() {
        logger.info("Triggered autoload dump");

        // Gather needed information
        ComposerDataService composerDataService = ComposerDataService.getInstance(project);
        String workingDirectory = new File(composerDataService.getConfigPath()).getParent();
        ComposerExecution execution = composerDataService.getComposerExecution();
        ComposerCommandExecutor executor;

        // Find executor for composer to run command with
        if (execution instanceof ExecutableComposerExecution) {
            executor = new ComposerCommandExecutor(((ExecutableComposerExecution) execution).getExecutablePath(), workingDirectory, DUMP_AUTOLOAD);
        } else if (execution instanceof PharComposerExecution) {
            executor = new ComposerCommandExecutor("php", workingDirectory, ((PharComposerExecution) execution).getPharPath() + " " + DUMP_AUTOLOAD);
        } else {
            MessageBusUtil.showMessage(NotificationType.ERROR, "Unsupported composer type", "Composer runtime type is not supported!");
            return;
        }

        // Schedule task with fancy progress bar
        ProgressManager.getInstance()
                .run(new ComposerCommandBackgroundTask(project, "Dump autoload", executor));
    }
}
