package dk.maxkandersen.parsing.exception

class InvalidArgException(line: Int, command: String, argNum: Int, expected: String, actual: String) : CircuitParseException(line, "Command $command expects $expected as argument number $argNum, but got $actual")