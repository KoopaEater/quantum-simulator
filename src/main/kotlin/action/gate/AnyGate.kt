package dk.maxkandersen.action.gate

import org.ejml.data.ZMatrixRMaj

class AnyGate(override val matrix: ZMatrixRMaj) : Gate {
    init {
        require(matrix.numRows == matrix.numCols) { "Gate matrix must be square, got ${matrix.numRows}x${matrix.numCols}" }
    }
}