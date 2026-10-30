package dk.maxkandersen.action.gate

import dk.maxkandersen.util.ident
import dk.maxkandersen.util.put
import org.ejml.data.ZMatrixRMaj

open class CtrlGate(gate: SingleQubitGate) : DoubleQubitGate {
    override val matrix: ZMatrixRMaj = run {
        val id = ident(4)
        id.put(gate.matrix, 2, 2)
    }
}