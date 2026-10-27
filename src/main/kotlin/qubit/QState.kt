package dk.maxkandersen.qubit

import dk.maxkandersen.util.kron
import dk.maxkandersen.util.minus
import dk.maxkandersen.util.pretty
import dk.maxkandersen.util.one
import dk.maxkandersen.util.zero
import dk.maxkandersen.util.plus
import org.ejml.data.ZMatrixRMaj
import org.ejml.dense.row.MatrixFeatures_ZDRM
import java.util.Locale
import kotlin.math.abs
import kotlin.math.sqrt

class QState(val tensor: ZMatrixRMaj) {

    companion object {

        fun zero() = QState(zero)
        fun one() = QState(one)
        fun plus() = QState(plus)
        fun minus() = QState(minus)
        fun negOne() = QState(ZMatrixRMaj( 2, 1, true,
            0.0, 0.0,
            -1.0, 0.0))
        fun negMinus() = QState(ZMatrixRMaj( 2, 1, true,
            -1/sqrt(2.0), 0.0,
            1/sqrt(2.0), 0.0))

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
        val tol = 1e-12
        val terms = ArrayList<String>()
        for (i in 0 until tensor.numRows) {
            val real = tensor.getReal(i, 0)
            val imag = tensor.getImag(i, 0)
            if (abs(real) < tol && abs(imag) < tol) continue
            val bitstring = i.toString(2).padStart(nQubits, '0')
            terms.add("(${String.format(Locale.ROOT, "%.3f", real)} + ${String.format(Locale.ROOT, "%.3f", imag)}i)|$bitstring>")
        }
        return terms.joinToString(" + ")
    }

    infix fun isIdenticalTo(other: QState): Boolean {
        val tol = 1e-12
        return MatrixFeatures_ZDRM.isEquals(this.tensor, other.tensor, tol)
    }
}