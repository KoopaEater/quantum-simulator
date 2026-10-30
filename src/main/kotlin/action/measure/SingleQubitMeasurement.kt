package dk.maxkandersen.action.measure

import dk.maxkandersen.qubit.QState
import dk.maxkandersen.random.RandomStrategy
import dk.maxkandersen.random.StandardRandomStrategy
import dk.maxkandersen.util.ediv
import dk.maxkandersen.util.getProjMatrix
import dk.maxkandersen.util.expandWithID
import dk.maxkandersen.util.mmul
import dk.maxkandersen.util.norm
import dk.maxkandersen.util.one
import dk.maxkandersen.util.zero
import org.ejml.data.ZMatrixRMaj

class SingleQubitMeasurement(val qubit: Int, val numQubits: Int, val basis0: ZMatrixRMaj = zero, val basis1: ZMatrixRMaj = one, val rand: RandomStrategy = StandardRandomStrategy()) : Measurement {
    init {
        require(numQubits >= 1) { "A measurement needs at least 1 qubit, got $numQubits" }
        require(qubit in 0 until numQubits) { "Qubit index $qubit is out of range for $numQubits qubits (valid: 0..${numQubits - 1})" }
        require(basis0.numRows == 2 && basis0.numCols == 1) { "basis0 must be a 2x1 column vector, got ${basis0.numRows}x${basis0.numCols}" }
        require(basis1.numRows == 2 && basis1.numCols == 1) { "basis1 must be a 2x1 column vector, got ${basis1.numRows}x${basis1.numCols}" }
    }

    override fun measure(state: QState): QState {
        require(state.nQubits == numQubits) { "Cannot measure a ${state.nQubits}-qubit state with a measurement configured for $numQubits qubits" }
        val M0 = getProjMatrix(basis0)
        val M0k = M0.expandWithID(qubit, numQubits)
        val w0 = M0k mmul state.tensor
        val norm0 = norm(w0)
        val p0 = norm0 * norm0

        if (rand.next() < p0) {
            return QState(w0 ediv norm0)
        }

        val M1 = getProjMatrix(basis1)
        val M1k = M1.expandWithID(qubit, numQubits)
        val w1 = M1k mmul state.tensor
        val norm1 = norm(w1)

        return QState(w1 ediv norm1)
    }
}