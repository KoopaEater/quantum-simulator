package dk.maxkandersen.action.measurement

import dk.maxkandersen.action.measure.FullMeasurement
import dk.maxkandersen.qubit.QState
import dk.maxkandersen.random.FixedRandomStrategy
import dk.maxkandersen.testutil.Assertions.assertIdentical
import dk.maxkandersen.util.minus
import dk.maxkandersen.util.one
import dk.maxkandersen.util.plus
import dk.maxkandersen.util.zero
import kotlin.test.Test


class FullMeasurementTest {
    val q0 = QState.zero()
    val q1 = QState.one()
    val qp = QState.plus()
    val qm = QState.minus()

    val rand0 = FixedRandomStrategy(0.0)
    val rand1 = FixedRandomStrategy(0.9)
    val rand01 = FixedRandomStrategy(0.3)
    val rand10 = FixedRandomStrategy(0.6)

    @Test
    fun plusMeasuresCorrectlyOnto01Basis() {
        val measurer0 = FullMeasurement(1, zero, one, rand0)
        val out0 = measurer0.measure(qp)
        assertIdentical(q0, out0)

        val measurer1 = FullMeasurement(1, zero, one, rand1)
        val out1 = measurer1.measure(qp)
        assertIdentical(q1, out1)
    }

    @Test
    fun minusMeasuresCorrectlyOnto01Basis() {
        val measurer0 = FullMeasurement(1, zero, one, rand0)
        val out0 = measurer0.measure(qm)
        assertIdentical(q0, out0)

        val measurer1 = FullMeasurement(1, zero, one, rand1)
        val out1 = measurer1.measure(qm)
        assertIdentical(q1, out1)
    }

    @Test
    fun zeroMeasuresCorrectlyOntoPMBasis() {
        val measurer0 = FullMeasurement(1, plus, minus, rand0)
        val out0 = measurer0.measure(q0)
        assertIdentical(qp, out0)

        val measurer1 = FullMeasurement(1, plus, minus, rand1)
        val out1 = measurer1.measure(q0)
        assertIdentical(qm, out1)
    }

    @Test
    fun oneMeasuresCorrectlyOntoPMBasis() {
        val measurer0 = FullMeasurement(1, plus, minus, rand0)
        val out0 = measurer0.measure(q1)
        assertIdentical(qp, out0)

        val measurer1 = FullMeasurement(1, plus, minus, rand1)
        val out1 = measurer1.measure(q1)
        assertIdentical(qm, out1)
    }

    @Test
    fun basisOntoSameBasisDoesNotChange() {
        val measurer0 = FullMeasurement(1, zero, one, rand0)
        val measurer1 = FullMeasurement(1, zero, one, rand1)

        val out00 = measurer0.measure(q0)
        assertIdentical(q0, out00)
        val out10 = measurer1.measure(q0)
        assertIdentical(q0, out10)

        val out01 = measurer0.measure(q1)
        assertIdentical(q1, out01)
        val out11 = measurer1.measure(q1)
        assertIdentical(q1, out11)
    }

    @Test
    fun fullMeasureOnMultiStateIsCorrect() {
        val qpp = qp kron qp

        val measurer0 = FullMeasurement(2, rand = rand0)
        val out0 = measurer0.measure(qpp)
        val expected0 = q0 kron q0
        assertIdentical(expected0, out0)

        val measurer01 = FullMeasurement(2, rand = rand01)
        val out01 = measurer01.measure(qpp)
        val expected01 = q0 kron q1
        assertIdentical(expected01, out01)

        val measurer10 = FullMeasurement(2, rand = rand10)
        val out10 = measurer10.measure(qpp)
        val expected10 = q1 kron q0
        assertIdentical(expected10, out10)

        val measurer1 = FullMeasurement(2, rand = rand1)
        val out1 = measurer1.measure(qpp)
        val expected1 = q1 kron q1
        assertIdentical(expected1, out1)
    }
}