package com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.command;

public class ComposerCommandFailedException extends Exception {
    private final String commandOutput;

    public ComposerCommandFailedException(String message, String commandOutput) {
        super(message);
        this.commandOutput = commandOutput;
    }

    public String getCommandOutput() {
        return commandOutput;
    }
}
