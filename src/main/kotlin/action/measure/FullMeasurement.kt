package dk.maxkandersen.action.measure

import dk.maxkandersen.qubit.QState
import dk.maxkandersen.random.RandomStrategy
import dk.maxkandersen.random.StandardRandomStrategy
import dk.maxkandersen.util.concat
import dk.maxkandersen.util.dagger
import dk.maxkandersen.util.expand
import dk.maxkandersen.util.mmul
import dk.maxkandersen.util.one
import dk.maxkandersen.util.zero
import org.ejml.data.ZMatrixRMaj

class FullMeasurement(val numQubits: Int, val basis0: ZMatrixRMaj = zero, val basis1: ZMatrixRMaj = one, val rand: RandomStrategy = StandardRandomStrategy()) : Measurement {
    override fun measure(state: QState): QState {
        val tensor = state.tensor
        val N = tensor.numRows

        val U = (basis0 concat basis1).expand(numQubits)
        val Ud = U.dagger()
        val tensor01 = Ud mmul tensor

        var cumSum = 0.0
        val cdf = MutableList(N) { 0.0 }
        for (i in 0 until N) {
            val real = tensor01.getReal(i, 0)
            val imag = tensor01.getImag(i, 0)
            cumSum += real * real + imag * imag
            cdf[i] = cumSum
        }
        val r = rand.next()
        // Clamp to the last outcome if floating-point rounding leaves the
        // cumulative sum slightly below 1.0 and r lands past the end.
        val index = cdf.indexOfFirst { it > r }.takeIf { it >= 0 } ?: (N - 1)
        val newTensor = ZMatrixRMaj(tensor.numRows, tensor.numCols)
        newTensor.setReal(index, 0, 1.0)
        return QState(U mmul newTensor)
    }
}