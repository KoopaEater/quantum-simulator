package dk.maxkandersen.parsing.exception

class UnknownCommandException(line: Int, command: String) : CircuitParseException(line, "Unknown command: $command")