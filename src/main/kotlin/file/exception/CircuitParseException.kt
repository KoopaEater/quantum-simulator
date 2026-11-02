package dk.maxkandersen.file.exception

open class CircuitParseException(line: Int, message: String) : RuntimeException("[$line]: $message")