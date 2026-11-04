package dk.maxkandersen.util

import org.ejml.data.ZMatrixRMaj
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MatrixOperationsTest {

    @Test
    fun mmulWorksForMatchingDimensions() {
        val res = ident2 mmul zero
        assertEquals(2, res.numRows)
        assertEquals(1.0, res.getReal(0, 0), 1e-12)
        assertEquals(0.0, res.getReal(1, 0), 1e-12)
    }

    @Test
    fun mmulThrowsOnDimensionMismatch() {
        val a = ZMatrixRMaj(2, 3)
        val b = ZMatrixRMaj(2, 1)
        assertFailsWith<IllegalArgumentException> { a mmul b }
    }

    @Test
    fun edivWorksAndThrowsOnZero() {
        val res = one ediv 2.0
        assertEquals(0.5, res.getReal(1, 0), 1e-12)
        assertFailsWith<IllegalArgumentException> { one ediv 0.0 }
    }

    @Test
    fun putInsertsAtOffset() {
        val m = ZMatrixRMaj(4, 4)
        m.put(ident2, 2, 2)
        assertEquals(1.0, m.getReal(2, 2), 1e-12)
        assertEquals(1.0, m.getReal(3, 3), 1e-12)
        assertEquals(0.0, m.getReal(0, 0), 1e-12)
    }

    @Test
    fun putThrowsWhenOutOfBounds() {
        val m = ZMatrixRMaj(2, 2)
        assertFailsWith<IllegalArgumentException> { m.put(ident2, 1, 1) }
        assertFailsWith<IllegalArgumentException> { m.put(ident2, -1, 0) }
        assertFailsWith<IllegalArgumentException> { m.put(ident2, 0, -1) }
    }

    @Test
    fun expandChecksMatrixAndQubitCount() {
        assertEquals(4, ident2.expand(2).numRows)
        assertFailsWith<IllegalArgumentException> { ident2.expand(0) }
        assertFailsWith<IllegalArgumentException> { ZMatrixRMaj(3, 3).expand(2) }
        assertFailsWith<IllegalArgumentException> { ZMatrixRMaj(2, 3).expand(2) }
    }

    @Test
    fun expandWithIDChecksQubitIndex() {
        assertEquals(4, ident2.expandWithID(1, 2).numRows)
        assertFailsWith<IllegalArgumentException> { ident2.expandWithID(2, 2) }
        assertFailsWith<IllegalArgumentException> { ident2.expandWithID(-1, 2) }
        assertFailsWith<IllegalArgumentException> { ident2.expandWithID(0, 0) }
    }

    @Test
    fun getProjMatrixRequiresColumnVector() {
        val proj = getProjMatrix(zero)
        assertEquals(2, proj.numRows)
        assertEquals(1.0, proj.getReal(0, 0), 1e-12)
        assertFailsWith<IllegalArgumentException> { getProjMatrix(ZMatrixRMaj(2, 2)) }
    }
}
