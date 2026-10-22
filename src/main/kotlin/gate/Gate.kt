package dk.maxkandersen.gate

import dk.maxkandersen.qubit.QState
import dk.maxkandersen.util.pretty
import org.ejml.data.ZMatrixRMaj
import org.ejml.dense.row.CommonOps_ZDRM


interface Gate {
    val matrix: ZMatrixRMaj
    infix fun apply(state: QState): QState {
        val newStateMatrix = ZMatrixRMaj(state.tensor.numRows, state.tensor.numCols)
        CommonOps_ZDRM.mult(matrix, state.tensor, newStateMatrix)
        return QState(newStateMatrix)
    }
    val pretty: String get() = matrix.pretty
    infix fun seq(other: Gate): Gate {
        val new = ZMatrixRMaj(matrix.numRows, matrix.numCols)
        CommonOps_ZDRM.mult(other.matrix, this.matrix, new)
        return AnyGate(new)
    }
}