package dk.maxkandersen.action.gate

import dk.maxkandersen.qubit.QState
import org.ejml.data.ZMatrixRMaj
import kotlin.test.Test
import kotlin.test.assertFailsWith

class GateValidationTest {

    @Test
    fun applyThrowsOnDimensionMismatch() {
        assertFailsWith<IllegalArgumentException> { HGate().apply(QState.baseState(2)) }
        assertFailsWith<IllegalArgumentException> { CXGate().apply(QState.zero()) }
    }

    @Test
    fun seqThrowsOnIncompatibleDimensions() {
        assertFailsWith<IllegalArgumentException> { HGate() seq CXGate() }
    }

    @Test
    fun anyGateRequiresSquareMatrix() {
        assertFailsWith<IllegalArgumentException> { AnyGate(ZMatrixRMaj(2, 3)) }
    }

    @Test
    fun multiGateRequiresAtLeastOneSquareGate() {
        assertFailsWith<IllegalArgumentException> { MultiGate(emptyList()) }
        assertFailsWith<IllegalArgumentException> { MultiGate(listOf(AnyGate(ZMatrixRMaj(2, 3)))) }
    }

    @Test
    fun expandChecksQubitIndexAndCount() {
        assertFailsWith<IllegalArgumentException> { HGate().expand(0) }
        assertFailsWith<IllegalArgumentException> { HGate().expandWithID(3, 2) }
        assertFailsWith<IllegalArgumentException> { HGate().expandWithID(0, 0) }
    }

    @Test
    fun generateMultiGateChecksIndices() {
        assertFailsWith<IllegalArgumentException> { CXGate().generateMultiGate(0, 1, 1) }
        assertFailsWith<IllegalArgumentException> { CXGate().generateMultiGate(0, 5, 2) }
        assertFailsWith<IllegalArgumentException> { CXGate().generateMultiGate(-1, 1, 2) }
        assertFailsWith<IllegalArgumentException> { CXGate().generateMultiGate(0, 0, 2) }
    }

    @Test
    fun pGateRequiresFiniteAngle() {
        assertFailsWith<IllegalArgumentException> { PGate(Double.NaN) }
        assertFailsWith<IllegalArgumentException> { PGate(Double.POSITIVE_INFINITY) }
    }

    @Test
    fun swapGateChecksQubitIndices() {
        assertFailsWith<IllegalArgumentException> { SwapGate(0, 5, 2) }
        assertFailsWith<IllegalArgumentException> { SwapGate(-1, 1, 2) }
    }
}
