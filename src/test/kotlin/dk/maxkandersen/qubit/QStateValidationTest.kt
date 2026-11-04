package dk.maxkandersen.qubit

import org.ejml.data.ZMatrixRMaj
import kotlin.test.Test
import kotlin.test.assertFailsWith

class QStateValidationTest {

    @Test
    fun tensorMustBeColumnVector() {
        assertFailsWith<IllegalArgumentException> { QState(ZMatrixRMaj(2, 2)) }
    }

    @Test
    fun tensorRowsMustBePowerOfTwo() {
        assertFailsWith<IllegalArgumentException> { QState(ZMatrixRMaj(3, 1)) }
        assertFailsWith<IllegalArgumentException> { QState(ZMatrixRMaj(1, 1)) }
    }

    @Test
    fun tensorMustHaveNormOne() {
        val two = ZMatrixRMaj(2, 1, true, 2.0, 0.0, 0.0, 0.0)
        assertFailsWith<IllegalArgumentException> { QState(two) }
    }

    @Test
    fun baseStateRequiresAtLeastOneQubit() {
        assertFailsWith<IllegalArgumentException> { QState.baseState(0) }
    }
}
