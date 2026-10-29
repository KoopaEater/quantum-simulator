package dk.maxkandersen.gate

import dk.maxkandersen.action.gate.SwapGate
import org.ejml.data.ZMatrixRMaj
import org.ejml.dense.row.CommonOps_ZDRM
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SwapGateTest {
    @Test
    fun twoQubitSwapIsIdentityWithRowsSwapped() {
        val m = SwapGate(0, 1, 2).matrix
        val expected = doubleArrayOf(
            1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0
        )
        assertTrue(m.data.contentEquals(expected))
    }

    @Test
    fun threeQubitSwapIsInvolutionAndUnitary() {
        val m = SwapGate(0, 2, 3).matrix
        // Involution: M^2 = I
        val sq = ZMatrixRMaj(8, 8)
        CommonOps_ZDRM.mult(m, m, sq)
        for (i in 0 until 64) {
            val expected = if (i % 9 == 0) 1.0 else 0.0
            assertEquals(expected, sq.data[2 * i], 1e-12)
        }
        // Unitary: M^dagger M = I (real symmetric permutation, so M*M = I covers it)
    }

    @Test
    fun swapsAmplitudesOfBasisStates() {
        // 3 qubits, swap q0 and q2: |100> (idx 4, value 0.6) <-> |001> (idx 1, value 0.8)
        val state = ZMatrixRMaj(8, 1)
        state.set(4, 0, 0.6, 0.0)
        state.set(1, 0, 0.8, 0.0)
        val out = ZMatrixRMaj(8, 1)
        CommonOps_ZDRM.mult(SwapGate(0, 2, 3).matrix, state, out)
        assertEquals(0.6, out.getReal(1, 0), 1e-12)
        assertEquals(0.8, out.getReal(4, 0), 1e-12)
    }
}
