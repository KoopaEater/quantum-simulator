package dk.maxkandersen.circuit

import dk.maxkandersen.action.gate.HGate
import dk.maxkandersen.qubit.QState
import kotlin.test.Test
import kotlin.test.assertFailsWith

class QCircuitValidationTest {

    @Test
    fun circuitRequiresAtLeastOneQubit() {
        assertFailsWith<IllegalArgumentException> { QCircuit(0) }
        assertFailsWith<IllegalArgumentException> { QCircuit(-1) }
    }

    @Test
    fun addActionChecksGateDimensionMatchesState() {
        val c = QCircuit(1)
        assertFailsWith<IllegalArgumentException> { c.addAction(HGate().expand(2)) }
    }

    @Test
    fun setStartStateChecksQubitCount() {
        val c = QCircuit(1)
        assertFailsWith<IllegalArgumentException> { c.setStartState(QState.baseState(2)) }
    }
}
