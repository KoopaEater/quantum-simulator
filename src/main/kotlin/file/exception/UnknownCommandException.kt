package dk.maxkandersen.file.exception

class UnknownCommandException(line: Int, command: String) : CircuitParseException(line, "Unknown command: $command")