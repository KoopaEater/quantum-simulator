package dk.maxkandersen.parsing.exception

open class CircuitParseException(line: Int, message: String) : RuntimeException("[$line]: $message")