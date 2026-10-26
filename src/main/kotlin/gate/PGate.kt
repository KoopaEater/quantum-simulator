package dk.maxkandersen.gate

import org.ejml.data.ZMatrixRMaj
import kotlin.math.cos
import kotlin.math.sin

class PGate(theta: Double) : SingleQubitGate {
    override val matrix = ZMatrixRMaj(2, 2, true,
        1.0, 0.0, 0.0, 0.0,
        0.0, 0.0, cos(theta), sin(theta))
}