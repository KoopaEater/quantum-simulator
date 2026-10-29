package dk.maxkandersen.action.measure

import dk.maxkandersen.qubit.QState
import dk.maxkandersen.random.RandomStrategy
import dk.maxkandersen.random.StandardRandomStrategy
import dk.maxkandersen.util.ediv
import dk.maxkandersen.util.getProjMatrix
import dk.maxkandersen.util.makeFullRegister
import dk.maxkandersen.util.mmul
import dk.maxkandersen.util.norm
import dk.maxkandersen.util.one
import dk.maxkandersen.util.zero
import org.ejml.data.ZMatrixRMaj

class SingleQubitMeasurement(val qubit: Int, val numQubits: Int, val basis0: ZMatrixRMaj = zero, val basis1: ZMatrixRMaj = one, val rand: RandomStrategy = StandardRandomStrategy()) : Measurement {

    override fun measure(state: QState): QState {
        val M0 = getProjMatrix(basis0)
        val M0k = makeFullRegister(M0, qubit, numQubits)
        val w0 = M0k mmul state.tensor
        val norm0 = norm(w0)
        val p0 = norm0 * norm0

        if (rand.next() < p0) {
            return QState(w0 ediv norm0)
        }

        val M1 = getProjMatrix(basis1)
        val M1k = makeFullRegister(M1, qubit, numQubits)
        val w1 = M1k mmul state.tensor
        val norm1 = norm(w1)

        return QState(w1 ediv norm1)
    }
}