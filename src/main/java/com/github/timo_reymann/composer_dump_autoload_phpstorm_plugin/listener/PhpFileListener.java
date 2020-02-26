package com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.listener;

import com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.command.ComposerCommandScheduler;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.jetbrains.php.lang.PhpFileType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Listener for virtual file system events
 */
public class PhpFileListener implements BulkFileListener {
    private static final String FILE_TYPE_PHP = PhpFileType.INSTANCE.getName();
    private static final Logger logger = Logger.getInstance(PhpFileListener.class);

    private final ComposerCommandScheduler scheduler;

    public PhpFileListener(ComposerCommandScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        for (VFileEvent event : events) {
            if(isQualified(event)) {
                logger.info("Got qualification");
                scheduler.setShouldRun(true);
                break;
            }
        }
    }

    public boolean isQualified(VFileEvent event) {
        // Exclude life cycle pseudo changes not relevant for autoloading
        if(event.isFromSave() || event.isFromRefresh()  || event.getFile() == null) {
            return false;
        }

        // Directory is interesting, as it may contain php files
        if (event.getFile().isDirectory()) {
            return true;
        }

        // PHP files are the always interesting use case
        return event.getFile().getFileType().getName().equals(FILE_TYPE_PHP);
    }
}
