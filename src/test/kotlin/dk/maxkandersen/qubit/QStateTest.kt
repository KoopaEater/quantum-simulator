package dk.maxkandersen.qubit

import dk.maxkandersen.testutil.Assertions.assertEqual
import kotlin.test.Test

class QStateTest {
    val q0 = QState.zero()
    val q1 = QState.one()
    val qp = QState.plus()
    val qm = QState.minus()
    val q00 = QState.baseState(2)

    @Test
    fun basisStatesPrintCorrectBitstrings() {
        val bitstring0 = q0.asBitStrings()
        assertEqual("(1.000 + 0.000i)|0>", bitstring0)
        val bitstring1 = q1.asBitStrings()
        assertEqual("(1.000 + 0.000i)|1>", bitstring1)
        val bitstringp = qp.asBitStrings()
        assertEqual("(0.707 + 0.000i)|0> + (0.707 + 0.000i)|1>", bitstringp)
        val bitstringm = qm.asBitStrings()
        assertEqual("(0.707 + 0.000i)|0> + (-0.707 + 0.000i)|1>", bitstringm)
    }

    @Test
    fun multipleQubitsPrintCorrectBitstrings() {
        val bitstring00 = q00.asBitStrings()
        assertEqual("(1.000 + 0.000i)|00>", bitstring00)
    }

}