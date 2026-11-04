package dk.maxkandersen.parsing.exception

class NumQubitsException : CircuitParseException(1, "First line must be a single integer greater than 0 (number of qubits)")