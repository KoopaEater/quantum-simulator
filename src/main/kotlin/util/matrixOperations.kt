package dk.maxkandersen.util

import org.ejml.data.ZMatrixRMaj
import org.ejml.dense.row.CommonOps_ZDRM
import kotlin.math.sqrt

fun getProjMatrix(matrix: ZMatrixRMaj): ZMatrixRMaj {
    val proj = ZMatrixRMaj(matrix.numRows, matrix.numRows, true)
    CommonOps_ZDRM.multTransB(matrix, matrix, proj)
    return proj
}

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