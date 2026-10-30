package dk.maxkandersen.action.gate

import dk.maxkandersen.util.expand
import dk.maxkandersen.util.expandWithID

interface SingleQubitGate : Gate {
    fun expandWithID(qubit: Int, numQubits: Int): Gate {
        val expanded = this.matrix.expandWithID(qubit, numQubits)
        return AnyGate(expanded)
    }
    fun expand(numQubits: Int): Gate {
        val expanded = this.matrix.expand(numQubits)
        return AnyGate(expanded)
    }
}