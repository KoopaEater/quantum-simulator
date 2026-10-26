package dk.maxkandersen.gate

import org.ejml.data.ZMatrixRMaj

class ZGate : SingleQubitGate {
    override val matrix = ZMatrixRMaj(2, 2, true,
        1.0, 0.0, 0.0, 0.0,
        0.0, 0.0, -1.0, 0.0)
}