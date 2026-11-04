package dk.maxkandersen.parsing.exception

open class CircuitParseException(val line: Int, message: String) : RuntimeException("[$line]: $message")
