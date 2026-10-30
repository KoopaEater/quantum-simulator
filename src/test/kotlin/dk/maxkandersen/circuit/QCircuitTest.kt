package dk.maxkandersen.circuit

import dk.maxkandersen.action.gate.CNOTGate
import dk.maxkandersen.action.gate.HGate
import dk.maxkandersen.action.gate.IDGate
import dk.maxkandersen.action.gate.XGate
import dk.maxkandersen.action.gate.ZGate
import dk.maxkandersen.action.measure.FullMeasurement
import dk.maxkandersen.action.measure.SingleQubitMeasurement
import dk.maxkandersen.qubit.QState
import dk.maxkandersen.random.FixedRandomStrategy
import dk.maxkandersen.testutil.Assertions.assertIdentical
import kotlin.test.Test

class QCircuitTest {

    val q0 = QState.zero()
    val q1 = QState.one()
    val qp = QState.plus()
    val qm = QState.minus()

    @Test
    fun setStartStateWorksCorrectly() {
        val c = QCircuit(1)
        val res0 = c.run()
        assertIdentical(q0, res0)
        c.setStartState(q1)
        val res1 = c.run()
        assertIdentical(q1, res1)
    }

    @Test
    fun circuitWithOnlySingleGatesWorkCorrectly() {
        val cID = QCircuit(1)
        cID.addAction(IDGate())
        val resID = cID.run()
        assertIdentical(q0, resID)

        val cX = QCircuit(1)
        cX.addAction(XGate())
        val resX = cX.run()
        assertIdentical(q1, resX)

        val cH = QCircuit(1)
        cH.addAction(HGate())
        val resH = cH.run()
        assertIdentical(qp, resH)

        val cZ = QCircuit(1)
        cZ.addAction(ZGate())
        val resZ = cZ.run()
        assertIdentical(q0, resZ)
    }

    @Test
    fun circuitWithMultipleSingleGatesWorkCorrectly() {
        val cHZ = QCircuit(1)
        cHZ.addAction(HGate())
        cHZ.addAction(ZGate())
        val resHZ = cHZ.run()
        assertIdentical(qm, resHZ)

        val cHX = QCircuit(1)
        cHX.addAction(HGate())
        cHX.addAction(XGate())
        val resHX = cHX.run()
        assertIdentical(qp, resHX)

        val cX = QCircuit(1)
        cX.addAction(XGate())
        val resX = cX.run()
        val cHZH = QCircuit(1)
        cHZH.addAction(HGate())
        cHZH.addAction(ZGate())
        cHZH.addAction(HGate())
        val resHZH = cHZH.run()
        assertIdentical(resX, resHZH)
    }

    @Test
    fun circuitWithMultiGatesWorkCorrectly() {
        val c = QCircuit(3)
        c.addAction(CNOTGate().generateMultiGate(2, 0, 3))
        val res0 = c.run()
        assertIdentical(q0 kron q0 kron q0, res0)

        c.setStartState(q0 kron q0 kron q1)
        val res1 = c.run()
        assertIdentical(q1 kron q0 kron q1, res1)
    }

    @Test
    fun singleMeasurementWorksCorrectly() {
        val c0 = QCircuit(2)
        c0.addAction(HGate().expand(2))
        c0.addAction(SingleQubitMeasurement(0, 2, rand = FixedRandomStrategy(0.0)))
        val res0 = c0.run()
        assertIdentical(q0 kron qp, res0)

        val c1 = QCircuit(2)
        c1.addAction(HGate().expand(2))
        c1.addAction(SingleQubitMeasurement(0, 2, rand = FixedRandomStrategy(0.9)))
        val res1 = c1.run()
        assertIdentical(q1 kron qp, res1)
    }

    @Test
    fun fullMeasurementWorksCorrectly() {
        val c0 = QCircuit(2)
        c0.addAction(HGate().expand(2))
        c0.addAction(FullMeasurement(2, rand = FixedRandomStrategy(0.0)))
        val res0 = c0.run()
        assertIdentical(q0 kron q0, res0)

        val c1 = QCircuit(2)
        c1.addAction(HGate().expand(2))
        c1.addAction(FullMeasurement(2, rand = FixedRandomStrategy(0.9)))
        val res1 = c1.run()
        assertIdentical(q1 kron q1, res1)
    }

}