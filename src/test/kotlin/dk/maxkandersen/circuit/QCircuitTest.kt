package dk.maxkandersen.circuit

import dk.maxkandersen.action.gate.CNOTGate
import dk.maxkandersen.action.gate.CtrlGate
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
    val b00 = QState.b00()

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

    @Test
    fun teleportationWorksCorrectly() {
        val psi = QState.random(1)
        
        val c00 = QCircuit(3)
        c00.setStartState(psi kron b00)
        c00.addAction(CNOTGate().generateMultiGate(0, 1, 3))
        c00.addAction(HGate().expandWithID(0, 3))
        c00.addAction(SingleQubitMeasurement(0, 3, rand = FixedRandomStrategy(0.0)))
        c00.addAction(SingleQubitMeasurement(1, 3, rand = FixedRandomStrategy(0.0)))
        c00.addAction(CtrlGate(XGate()).generateMultiGate(1, 2, 3))
        c00.addAction(CtrlGate(ZGate()).generateMultiGate(0, 2, 3))
        val res00 = c00.run()
        assertIdentical(q0 kron q0 kron psi, res00)

        val c10 = QCircuit(3)
        c10.setStartState(psi kron b00)
        c10.addAction(CNOTGate().generateMultiGate(0, 1, 3))
        c10.addAction(HGate().expandWithID(0, 3))
        c10.addAction(SingleQubitMeasurement(0, 3, rand = FixedRandomStrategy(0.9)))
        c10.addAction(SingleQubitMeasurement(1, 3, rand = FixedRandomStrategy(0.0)))
        c10.addAction(CtrlGate(XGate()).generateMultiGate(1, 2, 3))
        c10.addAction(CtrlGate(ZGate()).generateMultiGate(0, 2, 3))
        val res10 = c10.run()
        assertIdentical(q1 kron q0 kron psi, res10)

        val c01 = QCircuit(3)
        c01.setStartState(psi kron b00)
        c01.addAction(CNOTGate().generateMultiGate(0, 1, 3))
        c01.addAction(HGate().expandWithID(0, 3))
        c01.addAction(SingleQubitMeasurement(0, 3, rand = FixedRandomStrategy(0.0)))
        c01.addAction(SingleQubitMeasurement(1, 3, rand = FixedRandomStrategy(0.9)))
        c01.addAction(CtrlGate(XGate()).generateMultiGate(1, 2, 3))
        c01.addAction(CtrlGate(ZGate()).generateMultiGate(0, 2, 3))
        val res01 = c01.run()
        assertIdentical(q0 kron q1 kron psi, res01)

        val c11 = QCircuit(3)
        c11.setStartState(psi kron b00)
        c11.addAction(CNOTGate().generateMultiGate(0, 1, 3))
        c11.addAction(HGate().expandWithID(0, 3))
        c11.addAction(SingleQubitMeasurement(0, 3, rand = FixedRandomStrategy(0.9)))
        c11.addAction(SingleQubitMeasurement(1, 3, rand = FixedRandomStrategy(0.9)))
        c11.addAction(CtrlGate(XGate()).generateMultiGate(1, 2, 3))
        c11.addAction(CtrlGate(ZGate()).generateMultiGate(0, 2, 3))
        val res11 = c11.run()
        assertIdentical(q1 kron q1 kron psi, res11)
    }

}