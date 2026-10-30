package dk.maxkandersen.action.gate

import dk.maxkandersen.util.expand
import dk.maxkandersen.util.expandWithID

interface SingleQubitGate : Gate {
    fun expandWithID(qubit: Int, numQubits: Int): Gate {
        require(numQubits >= 1) { "Cannot expand a gate to $numQubits qubits; need at least 1" }
        require(qubit in 0 until numQubits) { "Qubit index $qubit is out of range for $numQubits qubits (valid: 0..${numQubits - 1})" }
        val expanded = this.matrix.expandWithID(qubit, numQubits)
        return AnyGate(expanded)
    }
    fun expand(numQubits: Int): Gate {
        require(numQubits >= 1) { "Cannot expand a gate to $numQubits qubits; need at least 1" }
        val expanded = this.matrix.expand(numQubits)
        return AnyGate(expanded)
    }
}