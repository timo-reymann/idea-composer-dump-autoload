package de.timo_reymann.composer_dump_autoload_phpstorm_plugin.command

/**
 * Exception for failed composer command
 */
class ComposerCommandFailedException(message: String?, val commandOutput: String) : Exception(message)
