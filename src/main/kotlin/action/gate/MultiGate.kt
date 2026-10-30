package dk.maxkandersen.action.gate

import dk.maxkandersen.util.kron
import org.ejml.data.ZMatrixRMaj

class MultiGate(gates: List<Gate>) : Gate {
    override var matrix = ZMatrixRMaj(1, 1, true,
        1.0, 0.0
    )
    init {
        require(gates.isNotEmpty()) { "MultiGate needs at least one gate, got an empty list" }
        for (gate in gates) {
            require(gate.matrix.numRows == gate.matrix.numCols) { "Each gate in a MultiGate must have a square matrix, got ${gate.matrix.numRows}x${gate.matrix.numCols}" }
            matrix = matrix kron gate.matrix
        }
    }
}