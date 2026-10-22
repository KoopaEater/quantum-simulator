package dk.maxkandersen.util

import org.ejml.data.DMatrixRMaj
import org.ejml.data.ZMatrixRMaj
import org.ejml.dense.row.CommonOps_DDRM

// Courtesy of Gemini

fun decomposeFast(z: ZMatrixRMaj): Pair<DMatrixRMaj, DMatrixRMaj> {
    val totalElements = z.numRows * z.numCols
    val realPart = DMatrixRMaj(z.numRows, z.numCols)
    val imagPart = DMatrixRMaj(z.numRows, z.numCols)

    var complexIdx = 0
    for (i in 0 until totalElements) {
        realPart.data[i] = z.data[complexIdx]     // Read real
        imagPart.data[i] = z.data[complexIdx + 1] // Read imaginary
        complexIdx += 2
    }

    return Pair(realPart, imagPart)
}

fun recomposeFast(realPart: DMatrixRMaj, imagPart: DMatrixRMaj): ZMatrixRMaj {
    val numElements = realPart.numRows * realPart.numCols
    val z = ZMatrixRMaj(realPart.numRows, realPart.numCols)

    var complexIdx = 0
    for (i in 0 until numElements) {
        z.data[complexIdx] = realPart.data[i]     // Set real component
        z.data[complexIdx + 1] = imagPart.data[i] // Set imaginary component
        complexIdx += 2
    }

    return z
}

infix fun ZMatrixRMaj.kron(other: ZMatrixRMaj): ZMatrixRMaj {

    val (aReal, aImag) = decomposeFast(this)
    val (bReal, bImag) = decomposeFast(other)

    val rowsC = aReal.numRows * bReal.numRows
    val colsC = aReal.numCols * bReal.numCols

    // Intermediate terms
    val arBr = DMatrixRMaj(rowsC, colsC)
    val aiBi = DMatrixRMaj(rowsC, colsC)
    val arBi = DMatrixRMaj(rowsC, colsC)
    val aiBr = DMatrixRMaj(rowsC, colsC)

    // Compute the 4 real Kronecker products
    CommonOps_DDRM.kron(aReal, bReal, arBr) // AR ⊗ BR
    CommonOps_DDRM.kron(aImag, bImag, aiBi) // AI ⊗ BI
    CommonOps_DDRM.kron(aReal, bImag, arBi) // AR ⊗ BI
    CommonOps_DDRM.kron(aImag, bReal, aiBr) // AI ⊗ BR

    // Real Part = (AR ⊗ BR) - (AI ⊗ BI)
    val cReal = DMatrixRMaj(rowsC, colsC)
    CommonOps_DDRM.subtract(arBr, aiBi, cReal)

    // Imaginary Part = (AR ⊗ BI) + (AI ⊗ BR)
    val cImag = DMatrixRMaj(rowsC, colsC)
    CommonOps_DDRM.add(arBi, aiBr, cImag)

    val z = recomposeFast(cReal, cImag)
    return z
}
