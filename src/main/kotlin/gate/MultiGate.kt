package dk.maxkandersen.gate

import dk.maxkandersen.util.kron
import org.ejml.data.ZMatrixRMaj

class MultiGate(gates: List<Gate>) : Gate {
    override var matrix = ZMatrixRMaj(1, 1, true,
        1.0, 0.0
    )
    init {
        for (gate in gates) {
            matrix = matrix kron gate.matrix
        }
    }
}