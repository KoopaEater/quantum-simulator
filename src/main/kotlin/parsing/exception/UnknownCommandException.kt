package dk.maxkandersen.parsing.exception

class UnknownCommandException(line: Int, val command: String) : CircuitParseException(line, "Unknown command: $command")
