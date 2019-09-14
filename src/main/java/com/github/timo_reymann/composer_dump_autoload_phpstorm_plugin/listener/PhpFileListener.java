package com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.listener;

import com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.task.ComposerAutoloadDumpRunner;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileCopyEvent;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileMoveEvent;
import com.jetbrains.php.lang.PhpFileType;
import org.jetbrains.annotations.NotNull;

/**
 * Listener for lifecycle of php files
 */
public class PhpFileListener implements VirtualFileListener {
    private final Logger logger = Logger.getInstance(PhpFileListener.class);
    private final ComposerAutoloadDumpRunner composerAutoloadDumpRunner;
    private final String fileTypePhp;

    public PhpFileListener(Project project) {
        this.composerAutoloadDumpRunner = new ComposerAutoloadDumpRunner(project);
        this.fileTypePhp = PhpFileType.INSTANCE.getName();
    }

    private boolean isQualifiedForExecution(VirtualFileEvent event) {
        if (event.getFile().isDirectory()) {
            return false;
        }

        if (!event.getFile().getFileType().getName().equals(this.fileTypePhp)) {
            return false;
        }

        return true;
    }

    private void handle(VirtualFileEvent event) {
        if (!isQualifiedForExecution(event)) {
            return;
        }

        logger.info("Handle file change event");

        composerAutoloadDumpRunner.execute();
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        this.handle(event);
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        this.handle(event);
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        this.handle(event);
    }

    @Override
    public void fileCopied(@NotNull VirtualFileCopyEvent event) {
        this.handle(event);
    }
}
