package dk.maxkandersen.qubit

import dk.maxkandersen.random.RandomStrategy
import dk.maxkandersen.random.StandardRandomStrategy
import dk.maxkandersen.util.ediv
import dk.maxkandersen.util.emul
import dk.maxkandersen.util.kron
import dk.maxkandersen.util.minus
import dk.maxkandersen.util.norm
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

    init {
        require(tensor.numCols == 1) { "QState tensor must be a column vector (Nx1), got ${tensor.numRows}x${tensor.numCols}" }
        require(tensor.numRows >= 2 && (tensor.numRows and (tensor.numRows - 1)) == 0) { "QState tensor must have 2^n rows for n >= 1 qubits, got ${tensor.numRows} rows" }
        val tol = 1e-12
        val norm = norm(tensor)
        require(1.0-tol < norm && norm < 1.0+tol) { "QState tensor must have norm 1, got $norm" }
    }

    companion object {

        fun zero() = QState(zero)
        fun one() = QState(one)
        fun plus() = QState(plus)
        fun minus() = QState(minus)
        fun negZero() = QState(zero emul -1.0 )
        fun negOne() = QState(one emul -1.0)
        fun negPlus() = QState(plus emul -1.0)
        fun negMinus() = QState(minus emul -1.0)
        fun b00() = QState(ZMatrixRMaj(4, 1, true,
            1/sqrt(2.0), 0.0,
            0.0, 0.0,
            0.0, 0.0,
            1/sqrt(2.0), 0.0))
        fun b01() = QState(ZMatrixRMaj(4, 1, true,
            1/sqrt(2.0), 0.0,
            0.0, 0.0,
            0.0, 0.0,
            -1/sqrt(2.0), 0.0))
        fun b10() = QState(ZMatrixRMaj(4, 1, true,
            0.0, 0.0,
            1/sqrt(2.0), 0.0,
            1/sqrt(2.0), 0.0,
            0.0, 0.0))
        fun b11() = QState(ZMatrixRMaj(4, 1, true,
            0.0, 0.0,
            1/sqrt(2.0), 0.0,
            -1/sqrt(2.0), 0.0,
            0.0, 0.0))

        fun baseState(qubits: Int): QState {
            require(qubits >= 1) { "Cannot create a base state of $qubits qubits; need at least 1" }
            var state = zero()
            for (i in 1 until qubits) {
                state = state kron zero()
            }
            return state
        }

        fun random(qubits: Int, rand: RandomStrategy = StandardRandomStrategy()): QState {
            require(qubits >= 1) { "Cannot create a random state of $qubits qubits; need at least 1" }
            val dim = 1 shl qubits
            val tensor = ZMatrixRMaj(dim, 1)
            for (i in 0 until dim) {
                val real = rand.next() * 2.0 - 1.0
                val imag = rand.next() * 2.0 - 1.0
                tensor.setReal(i, 0, real)
                tensor.setImag(i, 0, imag)
            }
            val n = norm(tensor)
            require(n > 0.0) { "Random draw produced the zero vector and cannot be normalized to norm 1" }
            return QState(tensor ediv n)
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