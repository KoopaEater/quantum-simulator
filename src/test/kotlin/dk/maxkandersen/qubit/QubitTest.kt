package dk.maxkandersen.qubit

import org.junit.jupiter.api.Test

class QubitTest {
    val q0 = QState.zero()
    val q1 = QState.one()
    @Test
    fun basisStatesPrintCorrectBitstrings() {
        val bitstring = q0.asBitStrings()
        assert(bitstring.equals("1.0")) // CONTINUE HERE!
    }
}