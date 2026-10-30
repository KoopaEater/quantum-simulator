package dk.maxkandersen.action.gate

import dk.maxkandersen.action.Action
import dk.maxkandersen.qubit.QState
import dk.maxkandersen.util.mmul
import dk.maxkandersen.util.pretty
import org.ejml.data.ZMatrixRMaj


interface Gate : Action {
    val matrix: ZMatrixRMaj
    override infix fun apply(state: QState): QState {
        require(matrix.numRows == state.tensor.numRows && matrix.numCols == state.tensor.numRows) { "Cannot apply a ${matrix.numRows}x${matrix.numCols} gate to a state of dimension ${state.tensor.numRows} (${state.nQubits} qubits)" }
        val newStateMatrix = matrix mmul state.tensor
        return QState(newStateMatrix)
    }
    val pretty: String get() = matrix.pretty
    infix fun seq(other: Gate): Gate {
        require(this.matrix.numCols == other.matrix.numRows) { "Cannot compose gates: ${this.matrix.numRows}x${this.matrix.numCols} followed by ${other.matrix.numRows}x${other.matrix.numCols} have incompatible dimensions" }
        val new = other.matrix mmul this.matrix
        return AnyGate(new)
    }
}