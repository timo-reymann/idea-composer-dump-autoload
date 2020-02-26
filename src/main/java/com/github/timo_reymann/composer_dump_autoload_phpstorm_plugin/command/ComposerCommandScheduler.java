package com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.command;

import com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.task.ComposerAutoloadDumpRunner;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.concurrency.AppExecutorUtil;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Helper to schedule background task, that only executes the composer command if an change occured that
 * potentially needs to rebuild the composer autoload
 */
public class ComposerCommandScheduler implements Runnable {
    private final Logger logger = Logger.getInstance(ComposerCommandScheduler.class);

    private static final long INITIAL_DELAY = 1_000L;
    private static final long EXECUTION_DELAY = 500L;

    private final AtomicBoolean shouldRun = new AtomicBoolean();
    private final ComposerAutoloadDumpRunner composerAutoloadDumpRunner;

    public ComposerCommandScheduler(Project project) {
        this.composerAutoloadDumpRunner = new ComposerAutoloadDumpRunner(project);
    }

    public void setShouldRun(boolean shouldRun) {
        this.shouldRun.set(shouldRun);
    }

    public void schedule() {
        logger.info("Add composer command to scheduler");
        AppExecutorUtil.getAppScheduledExecutorService()
                .scheduleWithFixedDelay(this, INITIAL_DELAY, EXECUTION_DELAY, TimeUnit.MILLISECONDS);
    }

    @Override
    public synchronized void run() {
        if (!shouldRun.get()) {
            return;
        }

        composerAutoloadDumpRunner.execute();
        setShouldRun(false);
    }
}
