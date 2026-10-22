package dk.maxkandersen.gate

import org.ejml.data.ZMatrixRMaj

class XGate : SingleQubitGate {
    override val matrix = ZMatrixRMaj(2, 2, true,
        0.0, 0.0, 1.0, 0.0,
        1.0, 0.0, 0.0, 0.0
    )

}