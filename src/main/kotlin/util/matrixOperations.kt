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

fun ident(dim: Int): ZMatrixRMaj = CommonOps_ZDRM.identity(dim)
val ident2 = ident(2)


fun getProjMatrix(matrix: ZMatrixRMaj): ZMatrixRMaj {
    require(matrix.numCols == 1) { "Projection basis must be a column vector (Nx1), got ${matrix.numRows}x${matrix.numCols}" }
    val proj = ZMatrixRMaj(matrix.numRows, matrix.numRows)
    CommonOps_ZDRM.multTransB(matrix, matrix, proj)
    return proj
}

val proj0 = getProjMatrix(zero)
val proj1 = getProjMatrix(one)
val projp = getProjMatrix(plus)
val projm = getProjMatrix(minus)

infix fun ZMatrixRMaj.mmul(other: ZMatrixRMaj): ZMatrixRMaj {
    require(this.numCols == other.numRows) { "Matrix dimension mismatch: cannot multiply a ${this.numRows}x${this.numCols} matrix by a ${other.numRows}x${other.numCols} matrix" }
    val result = ZMatrixRMaj(this.numRows, other.numCols)
    CommonOps_ZDRM.mult(this, other, result)
    return result
}

infix fun ZMatrixRMaj.ediv(divisor: Double): ZMatrixRMaj {
    require(divisor != 0.0) { "Cannot divide a matrix by zero" }
    val result = ZMatrixRMaj(this.numRows, this.numCols)
    CommonOps_ZDRM.elementDivide(this, divisor, 0.0, result)
    return result
}

infix fun ZMatrixRMaj.emul(scalar: Double): ZMatrixRMaj {
    val result = ZMatrixRMaj(this)
    CommonOps_ZDRM.scale(scalar, 0.0, result)
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

fun ZMatrixRMaj.put(other: ZMatrixRMaj, atRow: Int, atCol: Int): ZMatrixRMaj {
    require(atRow >= 0 && atCol >= 0) { "Insertion offset must be non-negative, got (row=$atRow, col=$atCol)" }
    require(atRow + other.numRows <= numRows && atCol + other.numCols <= numCols) { "Cannot put a ${other.numRows}x${other.numCols} matrix at (row=$atRow, col=$atCol) into a ${numRows}x${numCols} matrix: it does not fit" }
    val result = this
    CommonOps_ZDRM.extract(other, 0, other.numRows, 0, other.numCols, result, atRow, atCol)
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
    require(numQubits >= 1) { "Cannot expand a matrix to $numQubits qubits; need at least 1" }
    var result = ZMatrixRMaj(1, 1, true,
        1.0, 0.0)
    for (i in 0 until numQubits) {
        result = result kron this
    }
    return result
}
fun ZMatrixRMaj.expandWithID(qubit: Int, numQubits: Int): ZMatrixRMaj {
    require(this.numRows == this.numCols) { "Matrix must be square" }
    require(this.numRows == 2) { "Matrix must be 2x2" }
    require(numQubits >= 1) { "Cannot expand a matrix to $numQubits qubits; need at least 1" }
    require(qubit in 0 until numQubits) { "Qubit index $qubit is out of range for $numQubits qubits (valid: 0..${numQubits - 1})" }
    var result = ZMatrixRMaj(1, 1, true,
        1.0, 0.0)
    for (i in 0 until numQubits) {
        result = result kron if (i == qubit) this else ident2
    }
    return result
}
