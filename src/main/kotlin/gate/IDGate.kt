package dk.maxkandersen.gate

import org.ejml.data.ZMatrixRMaj
import org.ejml.dense.row.CommonOps_ZDRM

class IDGate : SingleQubitGate {
    override val matrix = ZMatrixRMaj(
        2, 2, true,
        1.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 1.0, 0.0
    )

    override fun generateMultiGate(qubit: Int, numQubits: Int): Gate {
        return AnyGate(CommonOps_ZDRM.identity(1 shl numQubits))
    }
}