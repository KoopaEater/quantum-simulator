package dk.maxkandersen.file.exception

class NumQubitsException : CircuitParseException(1, "First line must be a single integer (number of qubits)")