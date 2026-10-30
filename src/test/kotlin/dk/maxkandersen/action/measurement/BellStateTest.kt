package dk.maxkandersen.action.measurement

import dk.maxkandersen.action.measure.SingleQubitMeasurement
import dk.maxkandersen.qubit.QState
import dk.maxkandersen.random.FixedRandomStrategy
import dk.maxkandersen.testutil.Assertions.assertIdentical
import kotlin.test.Test

class BellStateTest {

    val q0 = QState.zero()
    val q1 = QState.one()
    val qn1 = QState.negOne()
    val b00 = QState.b00()
    val b01 = QState.b01()
    val b10 = QState.b10()
    val b11 = QState.b11()


    @Test
    fun testB00() {
        val measurer0 = SingleQubitMeasurement(0, 2, rand = FixedRandomStrategy(0.0))
        val out0 = measurer0.measure(b00)
        assertIdentical(q0 kron q0, out0)

        val measurer1 = SingleQubitMeasurement(0, 2, rand = FixedRandomStrategy(1.0))
        val out1 = measurer1.measure(b00)
        assertIdentical(q1 kron q1, out1)
    }

    @Test
    fun testB01() {
        val measurer0 = SingleQubitMeasurement(0, 2, rand = FixedRandomStrategy(0.0))
        val out0 = measurer0.measure(b01)
        assertIdentical(q0 kron q0, out0)

        val measurer1 = SingleQubitMeasurement(0, 2, rand = FixedRandomStrategy(1.0))
        val out1 = measurer1.measure(b01)
        assertIdentical(qn1 kron q1, out1)
    }

    @Test
    fun testB10() {
        val measurer0 = SingleQubitMeasurement(0, 2, rand = FixedRandomStrategy(0.0))
        val out0 = measurer0.measure(b10)
        assertIdentical(q0 kron q1, out0)

        val measurer1 = SingleQubitMeasurement(0, 2, rand = FixedRandomStrategy(1.0))
        val out1 = measurer1.measure(b10)
        assertIdentical(q1 kron q0, out1)
    }

    @Test
    fun testB11() {
        val measurer0 = SingleQubitMeasurement(0, 2, rand = FixedRandomStrategy(0.0))
        val out0 = measurer0.measure(b11)
        assertIdentical(q0 kron q1, out0)

        val measurer1 = SingleQubitMeasurement(0, 2, rand = FixedRandomStrategy(1.0))
        val out1 = measurer1.measure(b11)
        assertIdentical(qn1 kron q0, out1)
    }
}