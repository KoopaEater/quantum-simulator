package dk.maxkandersen.qubit

import dk.maxkandersen.random.FixedRandomStrategy
import dk.maxkandersen.testutil.Assertions.assertEqual
import dk.maxkandersen.util.norm
import kotlin.test.Test
import kotlin.test.assertEquals

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

    @Test
    fun randomStateHasNormOneAndCorrectSize() {
        val r = QState.random(2, FixedRandomStrategy(0.75))
        assertEquals(2, r.nQubits)
        assertEquals(1.0, norm(r.tensor), 1e-12)
    }

}