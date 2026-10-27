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