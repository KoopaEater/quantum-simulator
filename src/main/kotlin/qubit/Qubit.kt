package dk.maxkandersen.qubit

import dk.maxkandersen.util.kron
import dk.maxkandersen.util.pretty
import org.ejml.data.ZMatrixRMaj

class Qubit(val tensor: ZMatrixRMaj) {

    companion object {

        fun zero() = Qubit(ZMatrixRMaj(
            2, 1, true,
            1.0, 0.0,
            0.0, 0.0
        ))

        fun one() = Qubit(ZMatrixRMaj(
            2, 1, true,
            0.0, 0.0,
            1.0, 0.0
        ))

        fun baseState(qubits: Int): Qubit {
            var state = zero()
            for (i in 1 until qubits) {
                state = state kron zero()
            }
            return state
        }
    }

    infix fun kron(other: Qubit): Qubit {
        return Qubit(tensor kron other.tensor)
    }

    val pretty: String get() = tensor.pretty
}