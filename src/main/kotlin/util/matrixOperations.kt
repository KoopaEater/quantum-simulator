package dk.maxkandersen.util

import org.ejml.data.ZMatrixRMaj
import org.ejml.dense.row.CommonOps_ZDRM
import org.ejml.dense.row.NormOps_ZDRM
import kotlin.math.sqrt

val zero = ZMatrixRMaj(2, 1, true,
    1.0, 0.0,
    0.0, 0.0)
val one = ZMatrixRMaj(2, 1, true,
    0.0, 0.0,
    1.0, 0.0)
val plus = ZMatrixRMaj(2, 1, true,
    1/sqrt(2.0), 0.0,
    1/sqrt(2.0), 0.0)
val minus = ZMatrixRMaj(2, 1, true,
    1/sqrt(2.0), 0.0,
    -1/sqrt(2.0), 0.0)

val ident2 = ZMatrixRMaj(2, 2, true,
    1.0, 0.0, 0.0, 0.0,
    0.0, 0.0, 1.0, 0.0)

fun getProjMatrix(matrix: ZMatrixRMaj): ZMatrixRMaj {
    val proj = ZMatrixRMaj(matrix.numRows, matrix.numRows)
    CommonOps_ZDRM.multTransB(matrix, matrix, proj)
    return proj
}

val proj0 = getProjMatrix(zero)
val proj1 = getProjMatrix(one)
val projp = getProjMatrix(plus)
val projm = getProjMatrix(minus)

fun makeFullRegister(M: ZMatrixRMaj, k: Int, n: Int): ZMatrixRMaj {
    var Mk = ZMatrixRMaj(1, 1, true,
        1.0, 0.0)
    for (i in 0 until n) {
        val m = if (i == k) M else ident2
        Mk = Mk kron m
    }
    return Mk
}

infix fun ZMatrixRMaj.mmul(other: ZMatrixRMaj): ZMatrixRMaj {
    val result = ZMatrixRMaj(this.numRows, other.numCols)
    CommonOps_ZDRM.mult(this, other, result)
    return result
}

infix fun ZMatrixRMaj.ediv(divisor: Double): ZMatrixRMaj {
    val result = ZMatrixRMaj(this.numRows, this.numCols)
    CommonOps_ZDRM.elementDivide(this, divisor, 0.0, result)
    return result
}

fun norm(tensor: ZMatrixRMaj) = NormOps_ZDRM.normF(tensor)

// Courtesy of Gemini
infix fun ZMatrixRMaj.concat(other: ZMatrixRMaj): ZMatrixRMaj {
    require(this.numRows == other.numRows) { "Vectors must have matching row counts" }
    // Allocate combined matrix (e.g., 2 x 2)
    val result = ZMatrixRMaj(this.numRows, this.numCols + other.numCols)
    // Insert v0 at column 0
    CommonOps_ZDRM.extract(this, 0, this.numRows, 0, this.numCols, result, 0, 0)
    // Insert v1 at column 1 (starting at dstX0 = v0.numCols)
    CommonOps_ZDRM.extract(other, 0, other.numRows, 0, other.numCols, result, 0, this.numCols)
    return result
}

fun ZMatrixRMaj.dagger(): ZMatrixRMaj {
    val result = ZMatrixRMaj(this.numRows, this.numCols)
    CommonOps_ZDRM.transposeConjugate(this, result)
    return result
}

fun ZMatrixRMaj.expand(numQubits: Int): ZMatrixRMaj {
    require(this.numRows == this.numCols) { "Matrix must be square" }
    require(this.numRows == 2) { "Matrix must be 2x2" }
    var result = ZMatrixRMaj(1, 1, true,
        1.0, 0.0)
    for (i in 0 until numQubits) {
        result = result kron this
    }
    return result
}