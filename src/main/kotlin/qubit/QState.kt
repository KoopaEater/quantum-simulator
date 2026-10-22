package dk.maxkandersen.qubit

import dk.maxkandersen.util.kron
import dk.maxkandersen.util.pretty
import org.ejml.data.ZMatrixRMaj
import org.ejml.dense.row.MatrixFeatures_ZDRM

class QState(val tensor: ZMatrixRMaj) {

    companion object {

        fun zero() = QState(ZMatrixRMaj(
            2, 1, true,
            1.0, 0.0,
            0.0, 0.0
        ))

        fun one() = QState(ZMatrixRMaj(
            2, 1, true,
            0.0, 0.0,
            1.0, 0.0
        ))

        fun baseState(qubits: Int): QState {
            var state = zero()
            for (i in 1 until qubits) {
                state = state kron zero()
            }
            return state
        }
    }

    val nQubits: Int get() = Integer.numberOfTrailingZeros(tensor.numRows)

    infix fun kron(other: QState): QState {
        return QState(tensor kron other.tensor)
    }

    val pretty: String get() = tensor.pretty

    fun asBitStrings(): String {
        val str = StringBuilder()
        for (i in 0 until tensor.numRows) {
            val real = tensor.getReal(i, 0)
            val imag = tensor.getImag(i, 0)
            if (real == 0.0 && imag == 0.0) continue
            val bitstring = i.toString(2).padStart(nQubits, '0')
            str.appendLine("|$bitstring>  ${tensor.getReal(i, 0)} + ${tensor.getImag(i, 0)}i")
        }
        return str.toString()
    }

    infix fun isIdenticalTo(other: QState): Boolean {
        val tol = 1e-12
        return MatrixFeatures_ZDRM.isEquals(this.tensor, other.tensor, tol)
    }
}