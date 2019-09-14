package com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.task;

import com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.command.ComposerCommandExecutor;
import com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.command.ComposerCommandFailedException;
import com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.util.MessageBusUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ComposerCommandBackgroundTask extends Task.Backgroundable {
    private final String text;
    private final ComposerCommandExecutor executor;

    ComposerCommandBackgroundTask(Project project, String text, ComposerCommandExecutor executor) {
        super(project, text);
        this.text = text;
        this.executor = executor;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        indicator.setIndeterminate(true);
        indicator.setText(this.text);
        try {
            List<String> stdout = executor.run();
            MessageBusUtil.showMessage(NotificationType.INFORMATION, "Autoload dumped successfully", String.join("\n", stdout)).hideBalloon();
        } catch (ComposerCommandFailedException e) {
            MessageBusUtil.showMessage(NotificationType.ERROR, String.format("Error executing '%s'", text), String.format("<html>%s<br /><pre>%s</pre></html>", e.getMessage(), e.getCommandOutput()));
            indicator.cancel();
        }
    }
}
