package dk.maxkandersen.parsing.exception

class InvalidArgException(line: Int, val command: String, val argNum: Int, val expected: String, val actual: String) : CircuitParseException(line, "Command $command expects $expected as argument number $argNum, but got $actual")
