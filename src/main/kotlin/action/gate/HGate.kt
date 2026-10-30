package dk.maxkandersen.action.gate

import org.ejml.data.ZMatrixRMaj
import org.ejml.dense.row.CommonOps_ZDRM
import kotlin.math.sqrt

class HGate() : SingleQubitGate {
    override val matrix = run {
        val m = ZMatrixRMaj(2,2,true,
            1.0, 0.0, 1.0, 0.0,
            1.0, 0.0, -1.0, 0.0)
        CommonOps_ZDRM.scale(1/sqrt(2.0), 0.0, m)
        m
    }
}