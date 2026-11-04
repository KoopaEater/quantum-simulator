package dk.maxkandersen.parsing.exception

class NumArgsException(line: Int, command: String, expected: String, actual: Int) : CircuitParseException(line, "Command $command expects $expected params but got $actual")