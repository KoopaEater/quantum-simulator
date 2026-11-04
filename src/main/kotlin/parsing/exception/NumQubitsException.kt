package dk.maxkandersen.parsing.exception

class NumQubitsException(line: Int = 1) : CircuitParseException(line, "First non-comment line must be a single integer greater than 0 (number of qubits)")
