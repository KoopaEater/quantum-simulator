package dk.maxkandersen.action.gate

import dk.maxkandersen.util.ident2
import org.ejml.dense.row.CommonOps_ZDRM

class IDGate : SingleQubitGate {
    override val matrix = ident2

    override fun expandWithID(qubit: Int, numQubits: Int): Gate {
        return AnyGate(CommonOps_ZDRM.identity(1 shl numQubits))
    }
}