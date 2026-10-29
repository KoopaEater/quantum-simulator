package dk.maxkandersen.gate

import dk.maxkandersen.action.gate.HadamardGate
import dk.maxkandersen.action.gate.IDGate
import dk.maxkandersen.action.gate.PGate
import dk.maxkandersen.action.gate.SGate
import dk.maxkandersen.action.gate.TGate
import dk.maxkandersen.action.gate.XGate
import dk.maxkandersen.action.gate.ZGate
import dk.maxkandersen.qubit.QState
import dk.maxkandersen.testutil.Assertions.assertIdentical
import org.ejml.data.ZMatrixRMaj
import kotlin.math.PI
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.BeforeTest

class SingleQubitGatesTest {

    val q0 = QState.zero()
    val q1 = QState.one()
    val qp = QState.plus()
    val qm = QState.minus()

    @BeforeTest
    fun setUp() {

    }

    @Test
    fun testIDGate() {
        val gate = IDGate()
        val out0 = gate.apply(q0)
        assertIdentical(q0, out0)
        val out1 = gate.apply(q1)
        assertIdentical(q1, out1)
    }

    @Test
    fun testXGate() {
        val gate = XGate()
        val out0 = gate.apply(q0)
        assertIdentical(q1, out0)
        val out1 = gate.apply(q1)
        assertIdentical(q0, out1)
    }

    @Test
    fun testHadamardGate() {
        val gate = HadamardGate()
        val out0 = gate.apply(q0)
        assertIdentical(qp, out0)
        val out1 = gate.apply(q1)
        assertIdentical(qm, out1)
    }

    @Test
    fun testPGate() {
        val gate = PGate(PI / 2)
        val out0 = gate.apply(q0)
        assertIdentical(q0, out0)
        val out1 = gate.apply(q1)
        val expected = QState(ZMatrixRMaj(2, 1, true,
            0.0, 0.0,
            0.0, 1.0))
        assertIdentical(expected, out1)
    }

    @Test
    fun testSGate() {
        val gate = SGate()
        val out0 = gate.apply(q0)
        assertIdentical(q0, out0)
        val out1 = gate.apply(q1)
        val expected = QState(ZMatrixRMaj(2, 1, true,
            0.0, 0.0,
            0.0, 1.0))
        assertIdentical(expected, out1)
    }

    @Test
    fun testZGate() {
        val gate = ZGate()
        val out0 = gate.apply(q0)
        assertIdentical(q0, out0)
        val out1 = gate.apply(q1)
        val expected = QState(ZMatrixRMaj(2, 1, true,
            0.0, 0.0,
            -1.0, 0.0))
        assertIdentical(expected, out1)
    }

    @Test
    fun testTGate() {
        val gate = TGate()
        val out0 = gate.apply(q0)
        assertIdentical(q0, out0)
        val out1 = gate.apply(q1)
        val expected = QState(ZMatrixRMaj(2, 1, true,
            0.0, 0.0,
            1/sqrt(2.0), 1/sqrt(2.0)))
        assertIdentical(expected, out1)
    }

}