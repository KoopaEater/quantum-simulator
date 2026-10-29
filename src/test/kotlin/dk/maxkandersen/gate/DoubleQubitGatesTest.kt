package dk.maxkandersen.gate

import dk.maxkandersen.action.gate.CNOTGate
import dk.maxkandersen.qubit.QState
import dk.maxkandersen.testutil.Assertions.assertIdentical
import kotlin.test.Test

class DoubleQubitGatesTest {
    val q00 = QState.baseState(2)
    val q01 = QState.zero() kron QState.one()
    val q10 = QState.one() kron QState.zero()
    val q11 = QState.one() kron QState.one()
    val q000 = QState.baseState(3)
    val q100 = QState.one() kron QState.zero() kron QState.zero()
    val q001 = QState.zero() kron QState.zero() kron QState.one()
    val q101 = QState.one() kron QState.zero() kron QState.one()

    @Test
    fun testCNOTGate() {
        val gate = CNOTGate()
        val out00 = gate.apply(q00)
        assertIdentical(q00, out00)
        val out01 = gate.apply(q01)
        assertIdentical(q01, out01)
        val out10 = gate.apply(q10)
        assertIdentical(q11, out10)
        val out11 = gate.apply(q11)
        assertIdentical(q10, out11)
    }

    @Test
    fun testMultiCNOTGate() {
        val gate = CNOTGate().generateMultiGate(2, 0, 3)
        val out000 = gate.apply(q000)
        assertIdentical(q000, out000)
        val out100 = gate.apply(q100)
        assertIdentical(q100, out100)
        val out001 = gate.apply(q001)
        assertIdentical(q101, out001)
        val out101 = gate.apply(q101)
        assertIdentical(q001, out101)
    }
}