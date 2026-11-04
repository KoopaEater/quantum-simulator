package dk.maxkandersen.parsing.exception

class NumArgsException(line: Int, val command: String, val expected: String, val actual: Int) : CircuitParseException(line, "Command $command expects $expected params but got $actual")
