package dk.maxkandersen.action.gate

import org.ejml.data.ZMatrixRMaj
import kotlin.math.cos
import kotlin.math.sin

open class PGate(theta: Double) : SingleQubitGate {
    init {
        require(theta.isFinite()) { "Phase angle theta must be finite, got $theta" }
    }

    override val matrix = ZMatrixRMaj(2, 2, true,
        1.0, 0.0, 0.0, 0.0,
        0.0, 0.0, cos(theta), sin(theta))
}