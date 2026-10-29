package dk.maxkandersen.action.gate

import dk.maxkandersen.action.Action
import dk.maxkandersen.qubit.QState
import dk.maxkandersen.util.mmul
import dk.maxkandersen.util.pretty
import org.ejml.data.ZMatrixRMaj


interface Gate : Action {
    val matrix: ZMatrixRMaj
    override infix fun apply(state: QState): QState {
        val newStateMatrix = matrix mmul state.tensor
        return QState(newStateMatrix)
    }
    val pretty: String get() = matrix.pretty
    infix fun seq(other: Gate): Gate {
        val new = other.matrix mmul this.matrix
        return AnyGate(new)
    }
}