package dk.maxkandersen.util

import org.ejml.data.ZMatrixRMaj
import java.util.Locale
import kotlin.math.abs

val ZMatrixRMaj.pretty: String
    get() {
        val sb = StringBuilder()
        sb.append("ZMatrixRMaj(${numRows}x${numCols}):\n")

        for (r in 0 until numRows) {
            sb.append("[ ")
            for (c in 0 until numCols) {
                val re = getReal(r, c)
                val im = getImag(r, c)

                // Format complex number: e.g., 2.30 + 1.50i
                val sign = if (im >= 0) "+" else "-"
                sb.append(String.format(Locale.ROOT, "%.2f %s %.2fi", re, sign, abs(im)))

                if (c < numCols - 1) sb.append(",  ")
            }
            sb.append(" ]\n")
        }
        return sb.toString()
    }
