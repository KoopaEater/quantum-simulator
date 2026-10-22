package dk.maxkandersen.gate

import org.ejml.data.ZMatrixRMaj

class CNOTGate : DoubleQubitGate {
    override val matrix = ZMatrixRMaj(4, 4, true,
        1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0
    )
}